package com.example.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.example.demo.dto.ResponseDto;
import com.example.demo.entity.AccountEntity;
import com.example.demo.repo.AccountRepo;
import com.example.demo.util.CryptoUtil;

import reactor.core.publisher.Mono;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
@Service
public class AccountService {

	@Autowired
	AccountRepo accountRepo;
	@Autowired
	CryptoUtil cryptoUtil;
	/**
	 * @param accountId
	 * @return account
	 * */
	public Mono<ResponseDto> getUserInfo(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			 accountRepo.findById(Long.parseLong(data.getFirst("accountId")))
				.flatMap(account -> Mono.just(ResponseDto.builder().result(1).data(account).build()))
				.defaultIfEmpty(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}

	/**
	 * @param wallet
	 * @return account
	 * */
	public Mono<ResponseDto> getNicknameByWallet(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			 accountRepo.findNicknameByWallet(data.getFirst("wallet"))
				.flatMap(account -> Mono.just(ResponseDto.builder().result(1).data(account).build()))
				.defaultIfEmpty(ResponseDto.builder().result(-2).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	
	/**
	 * @param wallet
	 * @param password
	 * @return account
	 * */
	public Mono<ResponseDto> login(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			 accountRepo.findByWalletAndPassword(data.getFirst("wallet"), cryptoUtil.encodeSHA512(data.getFirst("password")))
				.flatMap(account -> Mono.just(ResponseDto.builder().result(1).data(account).build()))
				.defaultIfEmpty(ResponseDto.builder().result(-2).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	
	/**
	 * @param nickname
	 * @return count
	 * */
	public Mono<ResponseDto> getCntByNickname(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			 accountRepo.countByNickname(data.getFirst("nickname"))
				.flatMap(account -> Mono.just(ResponseDto.builder().result(1).data(account).build()))
		).onErrorReturn(ResponseDto.builder().result(-1).build());
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
					.auth(data.getFirst("auth"))
					.nickname(data.getFirst("nickname"))
					.wallet(data.getFirst("wallet"))
					.walletAgree(data.getFirst("walletAgree"))
					.build())
				.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	/**
	 * @param accountId
	 * @param main
	 * */
	public Mono<ResponseDto> updateMain(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data ->
			accountRepo.updateMain(Long.parseLong(data.getFirst("accountId")), data.getFirst("main"))
				.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
}
