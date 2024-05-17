package com.example.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.example.demo.dto.ResponseDto;
import com.example.demo.repo.ContentRepo;
import com.example.demo.repo.LikeRepo;
import com.example.demo.repo.ViewRepo;

import reactor.core.publisher.Mono;

@Service
public class ContentService {

	@Autowired
	ContentRepo contentRepo;
	@Autowired
	LikeRepo likeRepo;
	@Autowired
	ViewRepo viewRepo;
	
	public Mono<ResponseDto> getContent(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			contentRepo.findById(Long.parseLong(data.getFirst("contentId")))
				.flatMap(content -> Mono.just(ResponseDto.builder().result(1).data(content).build()))
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
}
