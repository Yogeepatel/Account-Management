package com.yogi.account.Account.controller;


import com.yogi.account.Account.entity.*;
import com.yogi.account.Account.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/fetch")
public class fetchRepositoryDataController {
	private ELSSRepository elssRepository;
	private StocksRepository stocksRepository;
	private MutualFundRepository mutualFundRepository;
	private SipRepository sipRepository;
	private BankDetailsRepositiory bankDetailsRepositiory;
	private FDRepository fdRepository;
	private AccountExpenditureRepository accountExpenditureRepository;
	@GetMapping("/elss")
	public Page<ELSS> getElssData (@PathVariable int pageNumber)   {
		Pageable pageable = PageRequest.of(pageNumber,20);
		return elssRepository.findAll(pageable);
	}
	
	@GetMapping("/stocks")
	public Page<Stocks> getStocksData (@PathVariable int pageNumber)   {
		Pageable pageable = PageRequest.of(pageNumber,20);
		return stocksRepository.findAll(pageable);
	}
	
	@GetMapping("/sip")
	public Page<Sip> getSipData (@PathVariable int pageNumber)   {
		Pageable pageable = PageRequest.of(pageNumber,20);
		return sipRepository.findAll(pageable);
	}
	
	@GetMapping("/mutualFund")
	public Page<MutualFund> getMfData (@PathVariable int pageNumber)   {
		Pageable pageable = PageRequest.of(pageNumber,20);
		return mutualFundRepository.findAll(pageable);
	}
	
	@GetMapping("/fd")
	public Page<FDs> getFdData (@PathVariable int pageNumber)   {
		Pageable pageable = PageRequest.of(pageNumber,20);
		return fdRepository.findAll(pageable);
	}
	
	@GetMapping("/accountDetails")
	public List<BankDetails> getAccountsDetails ()   {
		return bankDetailsRepositiory.findAll();
	}
	
	@GetMapping("/monthlyExpenditure")
	public List<AccountExpenditure> getMonthlyExpendituredetails ()   {
		return accountExpenditureRepository.findAll();
	}
}
