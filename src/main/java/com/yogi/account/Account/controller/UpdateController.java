package com.yogi.account.Account.controller;

import com.yogi.account.Account.service.FetchBankDataService;
import com.yogi.account.Account.service.FetchMfStocksService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/update")
public class UpdateController {
	private FetchMfStocksService fetchMfStocksService;
	private FetchBankDataService fetchBankDataService;

	
	
	@GetMapping("/stocks")
	public String updateStocks()
	{
		return fetchMfStocksService.saveStocks();
	}
	
	@GetMapping("/sipandmf")
	public String updateSipandMf()
	{
		return fetchMfStocksService.saveSipandMf();
	}
	
	@GetMapping("/accountdetails")
	public String saveInvestmentAccountDetails()
	{
		return fetchMfStocksService.saveAccountDetails();
	}
}
