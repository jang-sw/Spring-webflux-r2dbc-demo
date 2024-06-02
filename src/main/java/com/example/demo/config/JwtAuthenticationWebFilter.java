package com.example.demo.config;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import com.example.demo.util.AESUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

public class JwtAuthenticationWebFilter extends AuthenticationWebFilter {

	

	public JwtAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Value("${secret.key}")
	String SECRET_KEY = "your-secret-key";

	
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7);
            try {
                Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(authToken)
                    .getBody();
                String id = claims.getSubject();
                AESUtil aesUtil = new AESUtil();              
                if (id != null && claims.get("au") != null && aesUtil.decrypt((String) claims.get("au")).equals(id+"::Ahc28cn")) {
                	UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(id, null, new ArrayList<>());
					SecurityContext securityContext = new SecurityContextImpl(auth);
					return chain.filter(exchange)
					     .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

                }
            } catch (Exception e) {
            	return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token", e));
            }
        }
        return chain.filter(exchange);
    }
}
