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
import com.example.demo.service.CommentService;

@Configuration
public class CommentRouter {

	@Bean
    protected RouterFunction<ServerResponse> commentRoutes(CommentService commentService) {
        return RouterFunctions. 
                route(GET("/openApi/comment/list").and(contentType(MediaType.ALL)).and(accept(MediaType.ALL))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(commentService.getComments(request), ResponseDto.class))
                 .andRoute(POST("/api/comment/save").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(commentService.saveComment(request), ResponseDto.class))
                 .andRoute(POST("/api/comment/delete").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                         , request -> ok().contentType(MediaType.APPLICATION_JSON).body(commentService.deleteComment(request), ResponseDto.class))
                 ;
    }
    

}
