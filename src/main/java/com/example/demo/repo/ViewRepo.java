package com.example.demo.repo;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.ViewEntity;
import com.example.demo.entity.compositeId.ViewLikeId;

import reactor.core.publisher.Mono;


public interface ViewRepo extends R2dbcRepository<ViewEntity, ViewLikeId>{

	@Query("INSERT IGNORE INTO tb_view(content_id, account_id) VALUES(:contentId, :accountId) ")
	public Mono<Void> save(@Param("accountId") Long accountId, @Param("contentId") Long contentId);
}
