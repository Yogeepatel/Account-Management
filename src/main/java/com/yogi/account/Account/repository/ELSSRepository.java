package com.yogi.account.Account.repository;

import com.yogi.account.Account.entity.ELSS;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ELSSRepository extends MongoRepository<ELSS, String> {
}
