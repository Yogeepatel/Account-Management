package com.yogi.account.Account.repository;

import com.yogi.account.Account.entity.YearlyExpenditure;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface YearlyExpenditureRepository  extends MongoRepository<YearlyExpenditure,String> {


}
