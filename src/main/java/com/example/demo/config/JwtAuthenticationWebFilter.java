package com.example.demo.config;

import java.util.ArrayList;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import com.example.demo.util.CryptoUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationWebFilter extends AuthenticationWebFilter {

	


	public JwtAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    	String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    	if("/openApi/account/refresh".equals(exchange.getRequest().getPath().toString())) return chain.filter(exchange);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7);
            try {
                Claims claims = Jwts.parser()
                    .setSigningKey(Constant.SECRET_KEY)
                    .parseClaimsJws(authToken)
                    .getBody();
                String id = claims.getSubject();
               
                CryptoUtil cryptoUtil = new CryptoUtil();              
                if (id != null && claims.get("d") != null 
                		&& exchange.getRequest().getHeaders().get("accountId") != null 
                		&& exchange.getRequest().getHeaders().get("accountId").size() != 0 ) {
                	UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(id, null, new ArrayList<>());
					SecurityContext securityContext = new SecurityContextImpl(auth);
					String d = cryptoUtil.AESDecrypt((String) claims.get("d"));
					String accountId = d.split("_")[1];
					return accountId.equals(exchange.getRequest().getHeaders().get("accountId").get(0)) ? chain.filter(exchange)
					     .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext))) :  Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));
                }
            } catch (Exception e) {
            	return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token", e));
            }
        }
        return chain.filter(exchange);
    }
}
