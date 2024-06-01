package com.example.demo.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class AuthService {

	@Value("${secret.key}")
	String secret;
	
	public String getToken(ServerRequest serverRequest) {
		return "Bearer " + Jwts.builder()
	        .setSubject(UUID.randomUUID() + "-" + serverRequest.hashCode())
	        .setExpiration(new Date(System.currentTimeMillis() + 1800000))
	        .signWith(SignatureAlgorithm.HS512, secret)
	        .compact();
	}
	 
	
}
