package com.example.demo.repo;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.AccountEntity;

import reactor.core.publisher.Mono;


public interface AccountRepo extends R2dbcRepository<AccountEntity, Long>{

	@Query("SELECT nickname FROM tb_account WHERE wallet=:wallet")
	public Mono<String> findNicknameByWallet(@Param("wallet")String wallet);
	
	@Query("SELECT account_id, auth, main, nickname FROM tb_account WHERE wallet=:wallet AND password=:password")
	public Mono<AccountEntity> findByWalletAndPassword(@Param("wallet")String wallet, @Param("password")String password);
	public Mono<Long> countByNickname(String nickname);
	
	public Mono<Long> countByAccountIdAndAuthAndNicknameAndWallet(Long accountId, String auth, String nickname, String wallet);
	
	public Mono<Long> countByWallet(String wallet);
	
	@Query(""
			+ "INSERT INTO tb_account(wallet, nickname, auth, wallet_agree, password) "
			+ "VALUES(:#{#accountEntity.wallet},:#{#accountEntity.nickname},:#{#accountEntity.auth},:#{#accountEntity.walletAgree},:#{#accountEntity.password})")
	public Mono<Void> saveAccount(@Param("accountEntity")AccountEntity accountEntity);
	
	@Query(""
			+ "UPDATE tb_account "
			+ "SET main=:main "
			+ "WHERE account_id=:accountId ")
	public Mono<Void> updateMain(@Param("accountId")Long accountId, @Param("main")String main);
	
}
