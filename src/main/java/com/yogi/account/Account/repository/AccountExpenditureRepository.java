package com.yogi.account.Account.repository;

import com.yogi.account.Account.entity.AccountExpenditure;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountExpenditureRepository extends MongoRepository<AccountExpenditure, String> {
	@Query("{ 'month' : ?0 }")
	List<AccountExpenditure> findByMonth(String month);
	
	@Query(value = "{ 'id'  :?0, 'month'  :?1 }", fields = "{ 'expenditureFromAccount' : 1 }")
	Optional<AccountExpenditure> findExpenditureFromAccount(String id, String month);
	
	@Query(value = "{ 'id'  :?0, 'month'  :?1 }")
	List<AccountExpenditure> existByIdAndMonth(String id, String month);
}
