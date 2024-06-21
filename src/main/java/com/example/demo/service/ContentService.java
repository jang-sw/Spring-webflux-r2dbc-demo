package com.example.demo.service;


import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.example.demo.dto.ContentDto;
import com.example.demo.dto.ResponseDto;
import com.example.demo.entity.ContentEntity;
import com.example.demo.repo.ContentRepo;
import com.example.demo.repo.LikeRepo;
import com.example.demo.repo.ViewRepo;
import com.example.demo.util.TranslateUtil;

import reactor.core.publisher.Mono;

@Service
public class ContentService {

	@Autowired
	ContentRepo contentRepo;
	@Autowired
	LikeRepo likeRepo;
	@Autowired
	ViewRepo viewRepo;
	@Autowired
	TranslateUtil translateUtil;
	
	private long getMaxPage(long dataSize, long pageSize) {
		long maxPages = dataSize / pageSize;
        if (dataSize % pageSize != 0 || maxPages == 0) {
            maxPages++; 
        }
        return maxPages;
    }
	
	/**
	 * @param contentId
	 * @param accountId 현재 유저ID (선택)
	 * @return content
	 * */
	public Mono<ResponseDto> getContent(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			(serverRequest.headers().firstHeader("accountId") == null ? contentRepo.findContentById(Long.parseLong(data.getFirst("contentId"))) : contentRepo.findContentByIdAndAccountId(Long.parseLong(data.getFirst("contentId")),Long.parseLong(serverRequest.headers().firstHeader("accountId"))))
				.flatMap(content -> serverRequest.headers().firstHeader("accountId") == null ? 
					Mono.just(ResponseDto.builder().result(1).data(content).build()) 
					: viewRepo.save(Long.parseLong(serverRequest.headers().firstHeader("accountId")),Long.parseLong(data.getFirst("contentId"))).thenReturn(ResponseDto.builder().result(1).data(content).build())
				).defaultIfEmpty(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	/**
	 * @param contentId
	 * @param accountId 현재 유저ID (선택)
	 * @return content
	 * */
	public Mono<ResponseDto> getOpenContent(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> {
			return contentRepo.findById(Long.parseLong(data.getFirst("contentId")))
					.flatMap(content -> Mono.just(ResponseDto.builder().result(1).data(content).build()) 
					).defaultIfEmpty(ResponseDto.builder().result(1).build());
		}).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	/**
	 * @param page
	 * @param type 
	 * @param subType 
	 * @param select 
	 * @param author (select에 따른 선택)
	 * @param title (select에 따른 선택)
	 * @param content (select에 따른 선택)
	 * @return contentList
	 * */
	public Mono<ResponseDto> getContentList(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> {
			Integer pageSize = 10;
			Integer page = Integer.parseInt(data.getFirst("page"));
			if("author".equals(data.getFirst("select"))) {
				return Mono.zip(contentRepo.findContentsByAuthor(data.getFirst("type"), data.getFirst("subType"), data.getFirst("author"),  pageSize, (page - 1) * pageSize).collectList().defaultIfEmpty(Collections.emptyList())
						, contentRepo.countByTypeAndSubTypeAndAuthor(data.getFirst("type"), data.getFirst("subType"), data.getFirst("author")))
					.flatMap(tuple -> Mono.just(ResponseDto.builder().data(ContentDto.ContentList.builder()
							.contents(tuple.getT1())
							.maxPage(getMaxPage(tuple.getT2(),pageSize))
							.build())
						.result(1).build()));
			} else if("title".equals(data.getFirst("select"))) {
				return Mono.zip(contentRepo.findContentsByTitle(data.getFirst("type"), data.getFirst("subType"), data.getFirst("title"), data.getFirst("titleEng"),  pageSize, (page - 1) * pageSize).collectList().defaultIfEmpty(Collections.emptyList())
						, contentRepo.countByTypeAndSubTypeAndTitle(data.getFirst("type"), data.getFirst("subType"), data.getFirst("title"), data.getFirst("titleEng")))
					.flatMap(tuple -> Mono.just(ResponseDto.builder().data(ContentDto.ContentList.builder()
							.contents(tuple.getT1())
							.maxPage(getMaxPage(tuple.getT2(),pageSize))
							.build())
						.result(1).build()));
			} else if("content".equals(data.getFirst("select"))) {
				return Mono.zip(contentRepo.findContentsByContent(data.getFirst("type"), data.getFirst("subType"), data.getFirst("content"), data.getFirst("contentEng"), pageSize, (page - 1) * pageSize).collectList().defaultIfEmpty(Collections.emptyList())
						, contentRepo.countByTypeAndSubTypeAndContent(data.getFirst("type"), data.getFirst("subType"), data.getFirst("content"), data.getFirst("contentEng")))
					.flatMap(tuple -> Mono.just(ResponseDto.builder().data(ContentDto.ContentList.builder()
							.contents(tuple.getT1())
							.maxPage(getMaxPage(tuple.getT2(),pageSize))
							.build())
						.result(1).build()));
			} else {
				return Mono.zip(contentRepo.findContents(data.getFirst("type"), data.getFirst("subType"),  pageSize, (page - 1) * pageSize).collectList().defaultIfEmpty(Collections.emptyList())
						, contentRepo.countByTypeAndSubType(data.getFirst("type"), data.getFirst("subType")))
					.flatMap(tuple -> Mono.just(ResponseDto.builder().data(ContentDto.ContentList.builder()
							.contents(tuple.getT1())
							.maxPage(getMaxPage(tuple.getT2(),pageSize))
							.build())
						.result(1).build()));
			}
		}).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	
	/**
	 * @param contentId
	 * */
	public Mono<ResponseDto> deleteContent(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			contentRepo.delete( Long.parseLong(serverRequest.headers().firstHeader("accountId")), Long.parseLong(data.getFirst("contentId")))
				.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	
	/**
	 * @param accountId
	 * @param contentId
	 * */
	public Mono<ResponseDto> cancelLike(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			likeRepo.deleteById(Long.parseLong(serverRequest.headers().firstHeader("accountId")), Long.parseLong(data.getFirst("contentId")))
				.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	/**
	 * @param accountId
	 * @param contentId
	 * */
	public Mono<ResponseDto> like(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			likeRepo.save(Long.parseLong(serverRequest.headers().firstHeader("accountId")), Long.parseLong(data.getFirst("contentId")))
				.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	/**
	 * @param nickname
	 * @param accountId
	 * @param title
	 * @param contentOri
	 * @param content
	 * @param type
	 * @param subType
	 * */
	public Mono<ResponseDto> saveContent(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data ->
			contentRepo.saveContent(ContentEntity.builder()
				.author(data.getFirst("nickname"))
				.accountId(Long.parseLong(serverRequest.headers().firstHeader("accountId")))
				.title(data.getFirst("title"))
				.titleEng(translateUtil.translateToEng(data.getFirst("title")))
				.content(data.getFirst("content"))
				.contentOri(data.getFirst("contentOri"))
				.contentEng(translateUtil.translateToEng(data.getFirst("content")))
				.type(data.getFirst("type"))
				.subType(data.getFirst("subType"))
				.build())
			.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	/**
	 * @param title
	 * @param content
	 * @param contentId
	 * */
	public Mono<ResponseDto> updateContent(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			contentRepo.updateContent(data.getFirst("title") ,data.getFirst("content"), Long.parseLong(data.getFirst("contentId")), Long.parseLong(serverRequest.headers().firstHeader("accountId")))
				.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
}
