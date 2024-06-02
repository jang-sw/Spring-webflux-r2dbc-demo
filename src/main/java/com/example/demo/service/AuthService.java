package com.example.demo.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.example.demo.util.AESUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class AuthService {

	@Value("${secret.key}")
	String secret;
	
	@Autowired
	AESUtil aesUtil;
	
	
	
	public String getToken(ServerRequest serverRequest) {
		String subject = UUID.randomUUID() + "-" + serverRequest.hashCode();
		Map< String, Object> au = new HashMap<>();
		try {
			au.put("au", aesUtil.encrypt(subject + "::Ahc28cn"));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return "Bearer " + Jwts.builder()
			.setClaims(au)
	        .setSubject(subject)
	        .setExpiration(new Date(System.currentTimeMillis() + 1800000))
	        .signWith(SignatureAlgorithm.HS512, secret)
	        .compact();
	}
	 
	
}
