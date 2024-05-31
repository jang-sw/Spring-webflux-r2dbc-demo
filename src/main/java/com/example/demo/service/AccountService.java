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
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			 accountRepo.findById(Long.parseLong(data.getFirst("accountId")))
				.flatMap(account -> Mono.just(ResponseDto.builder().result(1).data(account).build()))
				.defaultIfEmpty(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}


	public Mono<ResponseDto> getUserInfoByWallet(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			 accountRepo.findByWallet(data.getFirst("wallet"))
				.flatMap(account -> Mono.just(ResponseDto.builder().result(1).data(account).build()))
				.defaultIfEmpty(ResponseDto.builder().result(-2).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	
	public Mono<ResponseDto> getCntByNickname(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			 accountRepo.countByNickname(data.getFirst("nickname"))
				.flatMap(account -> Mono.just(ResponseDto.builder().result(1).data(account).build()))
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	
	public Mono<ResponseDto> saveAccount(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data ->
			accountRepo.saveAccount(AccountEntity.builder()
					.auth(data.getFirst("auth"))
					.nickname(data.getFirst("nickname"))
					.wallet(data.getFirst("wallet"))
					.walletAgree(data.getFirst("walletAgree"))
					.build())
				.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
}
