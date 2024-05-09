package com.yogi.account.Account.repository;

import com.yogi.account.Account.entity.SalaryUsage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SalaryUsageRepository extends MongoRepository<SalaryUsage, String> {

}
