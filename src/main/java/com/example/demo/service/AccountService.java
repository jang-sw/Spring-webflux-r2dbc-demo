package com.example.demo.service;


import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.example.demo.dto.ResponseDto;

import reactor.core.publisher.Mono;

@Service
public class AccountService {

	
	public Mono<ResponseDto> getUserInfo(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> accountDataReqMono = serverRequest.formData();

		return accountDataReqMono.flatMap(marketDataReq -> {
			String accountId = marketDataReq.getFirst("accountId");
			
			return Mono.just(ResponseDto.builder().result(1).build());
		});

	}


}
