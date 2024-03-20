package com.yogi.account.Account.repository;

import com.yogi.account.Account.entity.FDs;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FDRepository  extends MongoRepository<FDs, String> {
}
