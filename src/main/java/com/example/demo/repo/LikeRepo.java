package com.example.demo.repo;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.LikeEntity;
import com.example.demo.entity.compositeId.ViewLikeId;

import reactor.core.publisher.Mono;


public interface LikeRepo extends R2dbcRepository<LikeEntity, ViewLikeId>{

	@Query(""
			+ "DELETE FROM tb_like WHERE content_id=:contentId AND account_id=:accountId")
	public Mono<Void> deleteById(@Param("accountId") Long accountId, @Param("contentId") Long contentId);

	@Query("INSERT IGNORE INTO tb_like(content_id, account_id) VALUES(:contentId, :accountId) ")
	public Mono<Void> save(@Param("accountId") Long accountId, @Param("contentId") Long contentId);
}
