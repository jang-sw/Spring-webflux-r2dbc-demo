package com.example.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.demo.config.Constant;
import com.example.demo.dto.ResponseDto;
import com.example.demo.entity.AccountEntity;
import com.example.demo.repo.AccountRepo;
import com.example.demo.util.CryptoUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.util.Date;
import java.util.UUID;
@Service
public class AccountService {

	@Autowired
	AccountRepo accountRepo;
	@Autowired
	CryptoUtil cryptoUtil;
	

	/**
	 * @param wallet
	 * @return nickname
	 * */
	public Mono<ResponseDto> getNicknameByWallet(ServerRequest serverRequest) {
		return accountRepo.findNicknameByWallet(serverRequest.queryParam("wallet").get().toLowerCase())
			.flatMap(account -> Mono.just(ResponseDto.builder().result(1).data(account).build()))
			.defaultIfEmpty(ResponseDto.builder().result(-2).build())
			.onErrorReturn(ResponseDto.builder().result(-1).build());
		
	}
	
//	/**
//	 * @param wallet
//	 * @param password
//	 * @return account
//	 * */
//	public Mono<ResponseDto> login(ServerRequest serverRequest) {
//		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
//		return formDataReqMono.flatMap(data -> 
//			 accountRepo.findByWalletAndPassword(data.getFirst("wallet").toLowerCase(), cryptoUtil.encodeSHA512(data.getFirst("password")))
//				.flatMap(account -> Mono.just(ResponseDto.builder().result(1).data(account).build()))
//				.defaultIfEmpty(ResponseDto.builder().result(-2).build())
//		).onErrorReturn(ResponseDto.builder().result(-1).build());
//	}
	/**
	 * @param wallet
	 * @param password
	 * @return account
	 * */
	public Mono<ServerResponse> login(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> {
			return accountRepo.findByWalletAndPassword(data.getFirst("wallet").toLowerCase(), cryptoUtil.encodeSHA512(data.getFirst("password")))
					.defaultIfEmpty(AccountEntity.builder().build())
					.flatMap(account -> ok()
							.contentType(MediaType.APPLICATION_JSON)
							.header("Authorization", cryptoUtil.getToken(UUID.randomUUID() + "_"+ account.getAccountId() + "_" + serverRequest.hashCode()))
							.body(Mono.just(ResponseDto.builder().result(1).data(account).build()), ResponseDto.class)
					).onErrorResume(e -> ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(Mono.just(ResponseDto.builder().result(-1).build()), ResponseDto.class));
		});
	}
	/**
	 * @param wallet
	 * @return count
	 * */
	public Mono<ResponseDto> getCountByWallet(ServerRequest serverRequest) {
		return accountRepo.countByWallet(serverRequest.queryParam("wallet").get().toLowerCase())
				.flatMap(cnt -> Mono.just(ResponseDto.builder().result(1).data(cnt).build()))
				.onErrorReturn(ResponseDto.builder().result(-1).build());
		
	}
	
	public Mono<ServerResponse> refresh(ServerRequest serverRequest) {
		try {
			String authToken = serverRequest.headers().firstHeader("Authorization").substring(7);
			String accountId = serverRequest.headers().firstHeader("accountId");
			Claims claims = Jwts.parser()
	                 .setSigningKey(Constant.SECRET_KEY)
	                 .parseClaimsJws(authToken)
	                 .getBody();
	        String id = claims.getSubject();
	        String d = cryptoUtil.AESDecrypt((String) claims.get("d"));
	        if(id != null && accountId.equals(d.split("_")[1]) && ((new Date().getTime() - claims.getExpiration().getTime()) / 60000) <= 5) {
	        	return ok()
					.contentType(MediaType.APPLICATION_JSON)
					.header("Authorization", cryptoUtil.getToken(UUID.randomUUID() + "_"+ accountId + "_" + serverRequest.hashCode()))
					.body(ResponseDto.builder().result(1).data(accountId).build(), ResponseDto.class);
	     
	        }
		}catch (Exception e) {
			e.printStackTrace();
		}
        return ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(ResponseDto.builder().result(-2).build(), ResponseDto.class);

	}
	/**
	 * @param nickname
	 * @return count
	 * */
	public Mono<ResponseDto> getCntByNickname(ServerRequest serverRequest) {
		return accountRepo.countByNickname(serverRequest.queryParam("nickname").get().toLowerCase())
			.flatMap(account -> Mono.just(ResponseDto.builder().result(1).data(account).build()))
			.onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	
	/**
	 * @param accountId
	 * @param auth
	 * @param nickname
	 * @param wallet
	 * @return count
	 * */
	public Mono<ResponseDto> getCntByUserInfo(ServerRequest serverRequest) {
		return accountRepo.countByAccountIdAndAuthAndNicknameAndWallet(Long.parseLong(serverRequest.headers().firstHeader("accountId")), serverRequest.queryParam("auth").get(), serverRequest.queryParam("nickname").get(), serverRequest.queryParam("wallet").get().toLowerCase())
			.flatMap(account -> Mono.just(ResponseDto.builder().result(1).data(account).build()))
			.onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	
	/**
	 * @param auth
	 * @param nickname
	 * @param wallet
	 * @param password
	 * @param walletAgree
	 * */
	public Mono<ResponseDto> saveAccount(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data ->
			accountRepo.saveAccount(AccountEntity.builder()
					.password(cryptoUtil.encodeSHA512(data.getFirst("password")))
					.auth("user")
					.nickname(data.getFirst("nickname"))
					.wallet(data.getFirst("wallet").toLowerCase())
					.walletAgree(data.getFirst("walletAgree"))
					.build())
				.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	/**
	 * @param main
	 * */
	public Mono<ResponseDto> updateMain(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data ->
			accountRepo.updateMain(Long.parseLong(serverRequest.headers().firstHeader("accountId")), data.getFirst("main"))
				.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
}
