package com.example.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.example.demo.dto.ResponseDto;
import com.example.demo.entity.AccountEntity;
import com.example.demo.repo.AccountRepo;

import reactor.core.publisher.Mono;

@Service
public class AccountService {

	@Autowired
	AccountRepo accountRepo;
	
	public Mono<ResponseDto> getUserInfo(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> accountDataReqMono = serverRequest.formData();
		return accountDataReqMono.flatMap(marketDataReq -> 
			 accountRepo.findById(Long.parseLong(marketDataReq.getFirst("accountId")))
				.flatMap(account -> Mono.just(ResponseDto.builder().result(1).data(account).build()))
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}


	public Mono<ResponseDto> getUserInfoByWallet(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> accountDataReqMono = serverRequest.formData();
		return accountDataReqMono.flatMap(marketDataReq -> 
			 accountRepo.findByWallet(marketDataReq.getFirst("wallet"))
				.flatMap(account -> Mono.just(ResponseDto.builder().result(1).data(account).build()))
				.defaultIfEmpty(ResponseDto.builder().result(-2).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	
	public Mono<ResponseDto> getCntByNickname(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> accountDataReqMono = serverRequest.formData();
		return accountDataReqMono.flatMap(marketDataReq -> 
			 accountRepo.countByNickname(marketDataReq.getFirst("nickname"))
				.flatMap(account -> Mono.just(ResponseDto.builder().result(1).data(account).build()))
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	
	public Mono<ResponseDto> saveAccount(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> accountDataReqMono = serverRequest.formData();
		return accountDataReqMono.flatMap(marketDataReq ->
			accountRepo.saveAccount(AccountEntity.builder()
					.auth(marketDataReq.getFirst("auth"))
					.nickname(marketDataReq.getFirst("nickname"))
					.wallet(marketDataReq.getFirst("wallet"))
					.walletAgree(marketDataReq.getFirst("walletAgree"))
					.build())
				.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
}
