package com.example.bankingportal.service;

import com.example.bankingportal.constants.CurrencyType;
import com.example.bankingportal.entity.ExchangeRate;

public interface ExchangeRateService {

    public void fetchCurrencyExchangeRate(CurrencyType currency);

    public ExchangeRate getExchangeRate(CurrencyType baseCurrency,CurrencyType currencyCode);
}
