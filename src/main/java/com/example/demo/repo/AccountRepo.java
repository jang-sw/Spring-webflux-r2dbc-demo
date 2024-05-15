package com.example.demo.repo;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.AccountEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface AccountRepo extends R2dbcRepository<AccountEntity, Long>{


}
