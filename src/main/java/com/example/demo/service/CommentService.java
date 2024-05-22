package com.example.demo.service;


import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.example.demo.dto.ContentDto;
import com.example.demo.dto.ResponseDto;
import com.example.demo.entity.CommentEntity;
import com.example.demo.entity.ContentEntity;
import com.example.demo.repo.CommentRepo;
import com.example.demo.repo.ContentRepo;
import com.example.demo.repo.LikeRepo;
import com.example.demo.repo.ViewRepo;

import reactor.core.publisher.Mono;

@Service
public class CommentService {

	@Autowired
	ContentRepo contentRepo;
	@Autowired
	CommentRepo commentRepo;
	
	public Mono<ResponseDto> getComments(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			commentRepo.findByContentId(Long.parseLong(data.getFirst("contentId")))
				.collectList()
				.defaultIfEmpty(Collections.emptyList())
				.flatMap(comments -> Mono.just(ResponseDto.builder().result(1).data(comments).build()))
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	
	public Mono<ResponseDto> deleteComment(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			commentRepo.deleteById(Long.parseLong(data.getFirst("contentId")))
				.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	
	public Mono<ResponseDto> saveComment(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data ->
		commentRepo.saveComment(CommentEntity.builder()
				.accountId(Long.parseLong(data.getFirst("accountId")))
				.contentId(Long.parseLong(data.getFirst("contentId")))
				.nickname(data.getFirst("nickname"))
				.comment(data.getFirst("comment"))
				.build())
			.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
}
