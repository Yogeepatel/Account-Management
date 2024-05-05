package com.yogi.account.Account.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yogi.account.Account.entity.*;
import com.yogi.account.Account.repository.*;
import com.yogi.account.Account.service.GeneratePdfService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@CrossOrigin
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
	private GeneratePdfService generatePdfService;
	
	@GetMapping("/elss/{pageNumber}")
	public Page<ELSS> getElssData (@PathVariable int pageNumber)   {
		Pageable pageable = PageRequest.of(pageNumber,20);
		return elssRepository.findAll(pageable);
	}
	
	@GetMapping("/stocks/{pageNumber}")
	public Page<Stocks> getStocksData (@PathVariable int pageNumber)   {
		Pageable pageable = PageRequest.of(pageNumber,20);
		return stocksRepository.findAll(pageable);
	}
	
	@GetMapping("/sip/{pageNumber}")
	public Page<Sip> getSipData (@PathVariable int pageNumber)   {
		Pageable pageable = PageRequest.of(pageNumber,20);
		return sipRepository.findAll(pageable);
	}
	
	@GetMapping("/mutualfund/{pageNumber}")
	public Page<MutualFund> getMfData (@PathVariable int pageNumber)   {
		Pageable pageable = PageRequest.of(pageNumber,20);
		System.out.println( mutualFundRepository.findAll(pageable));
		return mutualFundRepository.findAll(pageable);
	}
	
	@GetMapping("/fd/{pageNumber}")
	public Page<FDs> getFdData (@PathVariable int pageNumber)   {
		Pageable pageable = PageRequest.of(pageNumber,20);
		return fdRepository.findAll(pageable);
	}
	
	@GetMapping("/accountdetails")
	public List<BankDetails> getAccountsDetails ()   {
		return bankDetailsRepositiory.findAll();
	}
	
	@GetMapping("/monthlyExpenditure")
	public List<AccountExpenditure> getMonthlyExpendituredetails ()   {
		return accountExpenditureRepository.findAll();
	}

	@GetMapping("/summary")
	public String getSummary() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(generatePdfService.calculateSummaryDetails());
	}
}
