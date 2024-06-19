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
                route(POST("/api/account/updateMain").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(accountService.updateMain(request), ResponseDto.class))
                .andRoute(GET("/api/account/chkUserInfo").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(accountService.getCntByUserInfo(request), ResponseDto.class))
                .andRoute(GET("/openApi/account/cntByNickname").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(accountService.getCntByNickname(request), ResponseDto.class))
                .andRoute(GET("/openApi/account/findNicknameByWallet").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(accountService.getNicknameByWallet(request), ResponseDto.class))
                .andRoute(GET("/openApi/account/refresh").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> accountService.refresh(request)) 
                .andRoute(GET("/openApi/account/cntByWallet").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(accountService.getCountByWallet(request), ResponseDto.class))
                .andRoute(POST("/openApi/account/login").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> accountService.login(request))
                .andRoute(POST("/openApi/account/join").and(accept(MediaType.ALL).and(contentType(MediaType.ALL)))
                        , request -> ok().contentType(MediaType.APPLICATION_JSON).body(accountService.saveAccount(request), ResponseDto.class))
                
                ;
    }
    

}
