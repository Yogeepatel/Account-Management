package com.yogi.account.Account.repository;

import com.yogi.account.Account.entity.BankDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BankDetailsRepositiory extends MongoRepository<BankDetails, String> {
}
