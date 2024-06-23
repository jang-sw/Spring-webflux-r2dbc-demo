package com.example.demo.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
public class HeaderFilter implements WebFilter {

	
	
	private boolean chkBrowser(String userAgent) {
		return userAgent.indexOf("MetaMaskMobile") > -1 || userAgent.indexOf("Trident") > -1 || userAgent.indexOf("Edge") > -1 || userAgent.indexOf("Whale") > -1
				|| userAgent.indexOf("Opera") > -1 || userAgent.indexOf("OPR") > -1 || userAgent.indexOf("Firefox") > -1
				|| (userAgent.indexOf("Safari") > -1 && userAgent.indexOf("Chrome") == -1)
				|| (userAgent.indexOf("Chrome") > -1);

	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		
		//exchange.getRequest().getHeaders().getSslInfo() 
		if(!chkBrowser(exchange.getRequest().getHeaders().get("User-Agent").get(0))) {
			ServerHttpResponse response = exchange.getResponse();
			response.setStatusCode(HttpStatus.BAD_REQUEST);
			return response.setComplete();
		}
		return chain.filter(exchange);
		
	}
}
