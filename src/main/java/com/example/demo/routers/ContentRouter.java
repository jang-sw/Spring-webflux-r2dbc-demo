package com.example.demo.routers;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.demo.dto.ResponseDto;
import com.example.demo.service.AccountService;
import com.example.demo.service.ContentService;

@Configuration
public class ContentRouter {

	
	@Bean
    protected RouterFunction<ServerResponse> contentRoutes(ContentService contentService) {
        return RouterFunctions. 
                route(GET("/api/content/list").and(contentType(MediaType.ALL)).and(accept(MediaType.ALL))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(contentService.getContent(request), ResponseDto.class))
                .andRoute(GET("/api/content/detail").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(contentService.getContent(request), ResponseDto.class))
                .andRoute(POST("/api/content/create").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(contentService.getContent(request), ResponseDto.class))
                .andRoute(POST("/api/content/delete").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(contentService.getContent(request), ResponseDto.class))
                .andRoute(POST("/api/content/like").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(contentService.getContent(request), ResponseDto.class))
                .andRoute(POST("/api/content/cancelLike").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(contentService.getContent(request), ResponseDto.class))
                .andRoute(POST("/api/content/update").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(contentService.getContent(request), ResponseDto.class))
                ;
    }
    

}
