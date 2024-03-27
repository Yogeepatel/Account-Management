package com.yogi.account.Account.repository;

import com.yogi.account.Account.entity.AccountExpenditure;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountExpenditureRepository extends MongoRepository<AccountExpenditure, String> {
}
