package com.yogi.account.Account.repository;

import com.yogi.account.Account.entity.ELSS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ELSSRepository extends MongoRepository<ELSS, String> {
	@Query("{'units':?0}")
	ELSS updateUnits(Double units,String id);
	
	@Query("{'units':?0}")
	public ELSS existByUnits(Double units);
}
