package com.yogi.account.Account.repository;

import com.yogi.account.Account.entity.ELSS;
import com.yogi.account.Account.entity.Stocks;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StocksRepository extends MongoRepository<Stocks,String> {
	
	@Query("{'id':?0}")
	ELSS updateUnits( String id,Double units);
}
