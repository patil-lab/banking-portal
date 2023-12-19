package com.example.bankingportal.controller;

import com.example.bankingportal.constants.CurrencyType;
import com.example.bankingportal.dto.BankingServiceResult;
import com.example.bankingportal.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1.0/exchange")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @Autowired
    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }


    @GetMapping("/fetch")
    public ResponseEntity<BankingServiceResult<String>> fetchExchangeRates(@RequestParam("baseCode") CurrencyType baseCode){
        exchangeRateService.fetchCurrencyExchangeRate(baseCode);
        return ResponseEntity.ok(BankingServiceResult.succeed(HttpStatus.OK.toString()));
    }
}
