package com.example.demo.repo;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.CommentEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface CommentRepo extends R2dbcRepository<CommentEntity, Long>{

	public Flux<CommentEntity> findByContentId(Long contentId);
	
	@Query(""
			+ "INSERT INTO tb_comment(nickname, account_id, content_id, comment) "
			+ "VALUES(:#{#commentEntity.nickname},:#{#commentEntity.accountId},:#{#commentEntity.contentId},:#{#commentEntity.comment})")
	public Mono<Void> saveComment(@Param("commentEntity")CommentEntity commentEntity);
	
	@Query(""
			+ "DELETE FROM tb_comment WHERE account_id=:accountId AND comment_id=:commentId ")
	public Mono<Void> delete(@Param("accountId") Long accountId, @Param("commentId")Long commentId);
	
}
