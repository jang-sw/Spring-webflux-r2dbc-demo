package com.example.demo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	
	@Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return authentication -> Mono.just(authentication);
    }
	
	@Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
        	.csrf(ServerHttpSecurity.CsrfSpec::disable)
        	.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        	.formLogin(ServerHttpSecurity.FormLoginSpec::disable)
        	.logout(ServerHttpSecurity.LogoutSpec::disable)
        	.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
        	.headers(headers -> headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.mode(Mode.SAMEORIGIN)))
            .authorizeExchange(auth -> auth
            		.pathMatchers("/openApi/**").permitAll()
                    .pathMatchers("/api/**").authenticated()
                    .anyExchange().permitAll()
            )
            .addFilterAt(new JwtAuthenticationWebFilter(reactiveAuthenticationManager()), SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }
	
	private UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedHeader("*");
        config.addExposedHeader("Authorization");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
