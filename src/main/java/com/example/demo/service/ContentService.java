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

import reactor.core.publisher.Mono;

@Service
public class ContentService {

	@Autowired
	ContentRepo contentRepo;
	@Autowired
	LikeRepo likeRepo;
	@Autowired
	ViewRepo viewRepo;
	
	private long getMaxPage(long dataSize, long pageSize) {
		long maxPages = dataSize / pageSize;
        if (dataSize % pageSize != 0 || maxPages == 0) {
            maxPages++; 
        }
        return maxPages;
    }
//	public Flux<BoxOpenLogEntity> getBoxOpenLog(Long accountId, Integer pageSize, Integer page){
//		return boxOpenLogRepo.findByAccountId(accountId, pageSize, (page - 1) * pageSize);
//	}
	public Mono<ResponseDto> getContent(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			contentRepo.findContentById(Long.parseLong(data.getFirst("contentId")),Long.parseLong(data.getFirst("accountId")))
				.flatMap(content -> Mono.just(ResponseDto.builder().result(1).data(content).build()))
				.defaultIfEmpty(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	
	
	public Mono<ResponseDto> getContentList(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> {
			Integer pageSize = Integer.parseInt(data.getFirst("pageSize"));
			Integer page = Integer.parseInt(data.getFirst("page"));
			if("author".equals(data.getFirst("select"))) {
				return Mono.zip(contentRepo.findContentsByAuthor(data.getFirst("type"), data.getFirst("subType"), data.getFirst("author"), data.getFirst("order"), pageSize, (page - 1) * pageSize).collectList().defaultIfEmpty(Collections.emptyList())
						, contentRepo.countByTypeAndSubTypeAndAuthor(data.getFirst("type"), data.getFirst("subType"), data.getFirst("author")))
					.flatMap(tuple -> Mono.just(ResponseDto.builder().data(ContentDto.ContentList.builder()
							.contents(tuple.getT1())
							.maxPage(getMaxPage(tuple.getT2(),pageSize))
							.build())
						.result(1).build()));
			} else if("title".equals(data.getFirst("select"))) {
				return Mono.zip(contentRepo.findContentsByTitle(data.getFirst("type"), data.getFirst("subType"), data.getFirst("title"), data.getFirst("order"), pageSize, (page - 1) * pageSize).collectList().defaultIfEmpty(Collections.emptyList())
						, contentRepo.countByTypeAndSubTypeAndTitle(data.getFirst("type"), data.getFirst("subType"), data.getFirst("title")))
					.flatMap(tuple -> Mono.just(ResponseDto.builder().data(ContentDto.ContentList.builder()
							.contents(tuple.getT1())
							.maxPage(getMaxPage(tuple.getT2(),pageSize))
							.build())
						.result(1).build()));
			} else {
				return Mono.zip(contentRepo.findContents(data.getFirst("type"), data.getFirst("subType"), data.getFirst("order"), pageSize, (page - 1) * pageSize).collectList().defaultIfEmpty(Collections.emptyList())
						, contentRepo.countByTypeAndSubType(data.getFirst("type"), data.getFirst("subType")))
					.flatMap(tuple -> Mono.just(ResponseDto.builder().data(ContentDto.ContentList.builder()
							.contents(tuple.getT1())
							.maxPage(getMaxPage(tuple.getT2(),pageSize))
							.build())
						.result(1).build()));
			}
		}).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	
	public Mono<ResponseDto> deleteContent(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			contentRepo.deleteById(Long.parseLong(data.getFirst("contentId")))
				.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	public Mono<ResponseDto> cancelLike(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			likeRepo.deleteById(Long.parseLong(data.getFirst("accountId")), Long.parseLong(data.getFirst("contentId")))
				.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	public Mono<ResponseDto> like(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			likeRepo.save(Long.parseLong(data.getFirst("accountId")), Long.parseLong(data.getFirst("contentId")))
				.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
	public Mono<ResponseDto> saveContent(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data ->
			contentRepo.saveContent(ContentEntity.builder()
				.author(data.getFirst("author"))
				.accountId(Long.parseLong(data.getFirst("accountId")))
				.title(data.getFirst("title"))
				.content(data.getFirst("content"))
				.type(data.getFirst("type"))
				.subType(data.getFirst("subType"))
				.build())
			.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}

	public Mono<ResponseDto> updateContent(ServerRequest serverRequest) {
		Mono<MultiValueMap<String, String>> formDataReqMono = serverRequest.formData();
		return formDataReqMono.flatMap(data -> 
			contentRepo.updateContent(data.getFirst("title") ,data.getFirst("content"), Long.parseLong(data.getFirst("contentId")))
				.thenReturn(ResponseDto.builder().result(1).build())
		).onErrorReturn(ResponseDto.builder().result(-1).build());
	}
}
