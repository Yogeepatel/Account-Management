package com.yogi.account.Account.repository;

import com.yogi.account.Account.entity.UploadMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileUploadMetaDataRepository extends MongoRepository<UploadMetaData,String> {
}
