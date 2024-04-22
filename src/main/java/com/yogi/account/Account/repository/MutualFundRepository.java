package com.yogi.account.Account.repository;

import com.yogi.account.Account.entity.MutualFund;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MutualFundRepository  extends MongoRepository<MutualFund,String> {
}
