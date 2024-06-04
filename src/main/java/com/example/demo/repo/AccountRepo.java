package com.example.demo.repo;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.AccountEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface AccountRepo extends R2dbcRepository<AccountEntity, Long>{

	public Mono<AccountEntity> findByWallet(String wallet);
	public Mono<Long> countByNickname(String nickname);
	
	@Query(""
			+ "INSERT INTO tb_account(wallet, nickname, auth, wallet_agree) "
			+ "VALUES(:#{#accountEntity.wallet},:#{#accountEntity.nickname},:#{#accountEntity.auth},:#{#accountEntity.walletAgree})")
	public Mono<Void> saveAccount(@Param("accountEntity")AccountEntity accountEntity);
	
	@Query(""
			+ "UPDATE tb_account "
			+ "SET main=:main "
			+ "WHERE accountId=:accountId ")
	public Mono<Void> updateMain(@Param("accountId")Long accountId, @Param("main")String main);
	
}
