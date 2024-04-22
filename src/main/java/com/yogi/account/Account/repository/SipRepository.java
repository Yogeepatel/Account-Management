package com.yogi.account.Account.repository;

import com.yogi.account.Account.entity.Sip;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SipRepository extends MongoRepository<Sip,String> {
}
