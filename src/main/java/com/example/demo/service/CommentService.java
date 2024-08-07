package com.example.demo.service;


import java.util.Collections;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.example.demo.dto.ResponseDto;
import com.example.demo.entity.CommentEntity;
import com.example.demo.repo.CommentRepo;
import com.example.demo.repo.ContentRepo;

import reactor.core.publisher.Mono;

@Service
public class CommentService {

	@Autowired
	ContentRepo contentRepo;
	@Autowired
	CommentRepo commentRepo;
	
	/**
	 * 댓글 목록 불러오기
	 * 
	 * @param contentId
	 * @return comments
	 * */
	public Mono<ResponseDto> getComments(ServerRequest serverRequest) {
		return commentRepo.findByContentId(Long.parseLong(serverRequest.queryParam("contentId").get()))
				.collectList()
				.defaultIfEmpty(Collections.emptyList())
				.flatMap(comments -> Mono.just(ResponseDto.builder().result(1).data(comments).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	/**
	 * 댓글 삭제
	 * 
	 * @param commentId
	 * */
	public Mono<ResponseDto> deleteComment(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			commentRepo.delete(Long.parseLong(serverRequest.headers().firstHeader("accountId")), Long.parseLong(data.getFirst("commentId")))
				.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	/**
	 * 댓글 저장
	 * 
	 * @param accountId
	 * @param contentId
	 * @param nickname
	 * @param comment
	 * */
	public Mono<ResponseDto> saveComment(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data ->
		commentRepo.saveComment(CommentEntity.builder()
				.accountId(Long.parseLong(serverRequest.headers().firstHeader("accountId")))
				.contentId(Long.parseLong(data.getFirst("contentId")))
				.nickname(data.getFirst("nickname"))
				.comment(StringEscapeUtils.escapeHtml4(data.getFirst("comment")))
				.build())
			.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
}
