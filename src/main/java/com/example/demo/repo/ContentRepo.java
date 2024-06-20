package com.example.demo.repo;

import java.util.List;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.ContentEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ContentRepo extends R2dbcRepository<ContentEntity, Long>{

	
		@Query(""
				+ "SELECT tc.* "
				+ 	", coalesce(l.like_count, 0) as likes "
				+ 	", coalesce(v.view_count, 0) as views "
				+ 	", coalesce(l.liked_count, 0) as liked "
				+ 	", coalesce(v.viewed_count, 0) as viewed "
				+ "FROM tb_content tc "
				+ "LEFT JOIN ( "
				+ 	"SELECT content_id "
				+ 	", count(*) as like_count "
				+ 	", sum(case when account_id = :accountId then 1 else 0 end) as liked_count "
				+ 	"FROM tb_like "
				+ 	"WHERE content_id = :contentId "
				+ 	"GROUP BY content_id "
				+ ") l ON tc.content_id = l.content_id "
				+ "LEFT JOIN ( "
				+ 	"SELECT content_id "
				+ 	", count(*) as view_count "
				+ 	", sum(case when account_id = :accountId then 1 else 0 end) as viewed_count "
				+ "FROM tb_view "
				+ "WHERE content_id = :contentId "
				+ "GROUP BY content_id ) v ON tc.content_id = v.content_id "
				+ "WHERE tc.content_id = :contentId "
				+ "")
		public Mono<ContentEntity> findContentByIdAndAccountId(@Param("contentId")Long contentId, @Param("accountId")Long accountId);
		
		@Query(""
				+ "SELECT tc.* "
				+ 	", coalesce(l.like_count, 0) as likes "
				+ 	", coalesce(v.view_count, 0) as views "
				+ "FROM tb_content tc "
				+ "LEFT JOIN ( "
				+ 	"SELECT content_id "
				+ 	", count(*) as like_count "
				+ 	"FROM tb_like "
				+ 	"WHERE content_id = :contentId "
				+ 	"GROUP BY content_id "
				+ ") l ON tc.content_id = l.content_id "
				+ "LEFT JOIN ( "
				+ 	"SELECT content_id "
				+ 	", count(*) as view_count "
				+ "FROM tb_view "
				+ "WHERE content_id = :contentId "
				+ "GROUP BY content_id ) v ON tc.content_id = v.content_id "
				+ "WHERE tc.content_id = :contentId "
				+ "")
		public Mono<ContentEntity> findContentById(@Param("contentId")Long contentId);
		
		@Query(""
				+ "SELECT tc.* "
				+ "FROM tb_content tc "
				+ "WHERE content_id = :contentId "
				+ "AND type IN (:types) ")
		public Mono<ContentEntity> findOpenContentById(@Param("contentId")Long contentId, @Param("types")List<String> types);
		
		@Query(""
				+ "SELECT "
				+ 	"tc.*, "
				+ 	"COALESCE(tl.like_count, 0) AS likes, "
				+ 	"COALESCE(tv.view_count, 0) AS views "
				+ "FROM "
				+ 	"tb_content tc "
				+ "LEFT JOIN "
				+  	"(SELECT content_id, COUNT(*) AS like_count "
				+ 	"FROM tb_like "
				+ 	"GROUP BY content_id) tl ON tc.content_id = tl.content_id "
				+ "LEFT JOIN "
				+ 	"(SELECT content_id, COUNT(*) AS view_count "
				+ 	"FROM tb_view "
				+ 	"GROUP BY content_id) tv ON tc.content_id = tv.content_id "
				+ "WHERE "
				+ 	"tc.type = :type AND tc.sub_type=:subType "
				+ "ORDER BY content_id DESC  "
				+ "LIMIT :limit OFFSET :offset "
				+ "")
		public Flux<ContentEntity> findContents(@Param("type")String type, @Param("subType")String subType,  @Param("limit")int limit, @Param("offset")int offset);

		public Mono<Long> countByTypeAndSubType(String type, String subType);
		
		@Query(""
				+ "SELECT "
				+ 	"tc.*, "
				+ 	"COALESCE(tl.like_count, 0) AS likes, "
				+ 	"COALESCE(tv.view_count, 0) AS views "
				+ "FROM "
				+ 	"tb_content tc "
				+ "LEFT JOIN "
				+ 	"(SELECT content_id, COUNT(*) AS like_count "
				+ 	"FROM tb_like "
				+ 	"GROUP BY content_id) tl ON tc.content_id = tl.content_id "
				+ "LEFT JOIN "
				+ 	"(SELECT content_id, COUNT(*) AS view_count "
				+ 	"FROM tb_view "
				+ 	"GROUP BY content_id) tv ON tc.content_id = tv.content_id "
				+ "WHERE "
				+ 	"tc.type = :type AND tc.sub_type=:subType "
				+ 	"AND tc.author like concat('%', :author, '%') "
				+ "ORDER BY content_id DESC  "
				+ "LIMIT :limit OFFSET :offset "
				+ "")
		public Flux<ContentEntity> findContentsByAuthor(@Param("type")String type, @Param("subType")String subType, @Param("author")String author,  @Param("limit")int limit, @Param("offset")int offset);

		@Query("SELECT count(*) FROM tb_content WHERE type=:type AND sub_type=:subType AND author like concat('%', :author, '%') ")
		public Mono<Long> countByTypeAndSubTypeAndAuthor(@Param("type")String type, @Param("subType")String subType, @Param("author")String author);
		
		@Query(""
				+ "SELECT "
				+ 	"tc.*, "
				+ 	"COALESCE(tl.like_count, 0) AS likes, "
				+ 	"COALESCE(tv.view_count, 0) AS views "
				+ "FROM "
				+ 	"tb_content tc "
				+ "LEFT JOIN "
				+ 	"(SELECT content_id, COUNT(*) AS like_count "
				+ 	"FROM tb_like "
				+ 	"GROUP BY content_id) tl ON tc.content_id = tl.content_id "
				+ "LEFT JOIN "
				+ 	"(SELECT content_id, COUNT(*) AS view_count "
				+ 	"FROM tb_view "
				+ 	"GROUP BY content_id) tv ON tc.content_id = tv.content_id "
				+ "WHERE "
				+ 	"tc.type = :type AND tc.sub_type=:subType "
				+ 	"AND (tc.title like concat('%', :title, '%') OR tc.title_eng like concat('%', :titleEng, '%') )"
				+ "ORDER BY content_id DESC  "
				+ "LIMIT :limit OFFSET :offset "
				+ "")
		public Flux<ContentEntity> findContentsByTitle(@Param("type")String type, @Param("subType")String subType, @Param("title")String title, @Param("titleEng")String titleEng,  @Param("limit")int limit, @Param("offset")int offset);

		@Query("SELECT count(*) FROM tb_content WHERE type=:type AND sub_type=:subType AND (title like concat('%', :title, '%') OR title_eng like concat('%', :titleEng, '%'))")
		public Mono<Long> countByTypeAndSubTypeAndTitle(@Param("type")String type, @Param("subType")String subType, @Param("title")String title, @Param("titleEng")String titleEng);

		@Query(""
				+ "SELECT "
				+ 	"tc.*, "
				+ 	"COALESCE(tl.like_count, 0) AS likes, "
				+ 	"COALESCE(tv.view_count, 0) AS views "
				+ "FROM "
				+ 	"tb_content tc "
				+ "LEFT JOIN "
				+ 	"(SELECT content_id, COUNT(*) AS like_count "
				+ 	"FROM tb_like "
				+ 	"GROUP BY content_id) tl ON tc.content_id = tl.content_id "
				+ "LEFT JOIN "
				+ 	"(SELECT content_id, COUNT(*) AS view_count "
				+ 	"FROM tb_view "
				+ 	"GROUP BY content_id) tv ON tc.content_id = tv.content_id "
				+ "WHERE "
				+ 	"tc.type = :type AND tc.sub_type=:subType "
				+ 	"AND (tc.content like concat('%', :content, '%') OR tc.contentEng like concat('%', :contentEng, '%')) "
				+ "ORDER BY content_id DESC  "
				+ "LIMIT :limit OFFSET :offset "
				+ "")
		public Flux<ContentEntity> findContentsByContent(@Param("type")String type, @Param("subType")String subType, @Param("content")String content, @Param("contentEng")String contentEng,  @Param("limit")int limit, @Param("offset")int offset);

		@Query("SELECT count(*) FROM tb_content WHERE type=:type AND sub_type=:subType AND (content like concat('%', :content, '%') OR content_eng like concat('%', :contentEng, '%'))")
		public Mono<Long> countByTypeAndSubTypeAndContent(@Param("type")String type, @Param("subType")String subType, @Param("content")String content, @Param("contentEng")String contentEng);
		
		@Query(""
				+ "INSERT INTO tb_content(account_id, author, title, title_eng, content, content_ori, content_eng, type, sub_type) "
				+ "VALUES(:#{#contentEntity.accountId},:#{#contentEntity.author},:#{#contentEntity.title},:#{#contentEntity.titleEng},:#{#contentEntity.content},:#{#contentEntity.contentOri},:#{#contentEntity.contentEng},:#{#contentEntity.type},:#{#contentEntity.subType})")
		public Mono<Void> saveContent(@Param("contentEntity") ContentEntity contentEntity);
		
		@Query(""
				+ "UPDATE tb_content "
				+ "SET title=:title "
				+ ", title_eng=:titleEng "
				+ ", content=:content "
				+ ", content_ori=:contentOri "
				+ ", content_eng=:contentEng "
				+ ", updated=now() "
				+ "WHERE content_id=:contentId "
				+ "AND account_id=:accountId ")
		public Mono<Void> updateContent(@Param("title") String title, @Param("content") String content, @Param("contentId") Long contentId, @Param("accountId") Long accountId);

		@Query(""
				+ "DELETE FROM tb_content WHERE account_id=:accountId AND content_id=:contentId")
		public Mono<Void> delete(@Param("accountId")Long accountId, @Param("contentId")Long contentId);
}
