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

@Configuration
public class AccountRouter {

	@Bean
    protected RouterFunction<ServerResponse> accountRoutes(AccountService accountService) {
        return RouterFunctions. 
                route(GET("/api/account/info").and(contentType(MediaType.ALL)).and(accept(MediaType.ALL))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(accountService.getUserInfo(request), ResponseDto.class))
                .andRoute(POST("/api/account/login").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(accountService.getUserInfo(request), ResponseDto.class))
                .andRoute(POST("/api/account/join").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(accountService.getUserInfo(request), ResponseDto.class))
                .andRoute(POST("/api/account/agree").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(accountService.getUserInfo(request), ResponseDto.class))
                ;
    }
    

}
