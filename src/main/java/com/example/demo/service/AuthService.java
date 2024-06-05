package com.example.demo.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;

import com.example.demo.config.Constant;
import com.example.demo.util.CryptoUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class AuthService {

	@Autowired
	CryptoUtil cryptoUtil;
	
	public String getToken(ServerRequest serverRequest) {
		String subject = UUID.randomUUID() + "-" + serverRequest.hashCode();
		Map< String, Object> jti = new HashMap<>();
		try {
			jti.put("jti", cryptoUtil.AESEncrypt(subject + "::Ahc28Cn"));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return "Bearer " + Jwts.builder()
			.setClaims(jti)
	        .setSubject(subject)
	        .setExpiration(new Date(System.currentTimeMillis() + 1800000))
	        .signWith(SignatureAlgorithm.HS512, Constant.SECRET_KEY)
	        .compact();
	}
	 
	
}
