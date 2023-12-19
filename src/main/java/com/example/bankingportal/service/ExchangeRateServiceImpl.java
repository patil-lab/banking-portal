package com.example.bankingportal.service;

import com.example.bankingportal.constants.BankingServiceError;
import com.example.bankingportal.constants.CurrencyType;
import com.example.bankingportal.dto.ExchangeRateDto;
import com.example.bankingportal.entity.ExchangeRate;
import com.example.bankingportal.exception.BankingServiceException;
import com.example.bankingportal.repository.ExchangeRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    @Value("${exchange.rate.url}")
    private String apiHost;

    @Value("${exchange.rate.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    private final ExchangeRateRepository exchangeRateRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(DashBoardServiceImpl.class);
    @Value("${spring.application.name}")
    private String serviceName;

    @Autowired
    public ExchangeRateServiceImpl(RestTemplate restTemplate, ExchangeRateRepository exchangeRateRepository) {
        this.restTemplate = restTemplate;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @Override
    public void fetchCurrencyExchangeRate(CurrencyType baseCurrency) {


        String url = String.format("%s/%s/latest/%s", apiHost, apiKey, baseCurrency);
        String builder = UriComponentsBuilder.fromHttpUrl(url)
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);


        ResponseEntity<ExchangeRateDto> response = restTemplate.exchange(
                builder,
                HttpMethod.GET,
                httpEntity,
                ExchangeRateDto.class
        );

        List<ExchangeRate> exchangeRates = getExchangeRateList(response, baseCurrency);

        exchangeRateRepository.saveAll(exchangeRates);

    }

    @Override
    public ExchangeRate getExchangeRate(CurrencyType baseCurrency, CurrencyType currencyCode) {
        LOGGER.info(String.format("Exchange Rate  for baseCurrency %s", baseCurrency));
        Optional<ExchangeRate> optionalExchangeRate = exchangeRateRepository.findByBaseCurrencyAndCurrencyCode(baseCurrency.name(), currencyCode.name());
        if (optionalExchangeRate.isEmpty()) {

            throw new BankingServiceException(serviceName, BankingServiceError.NO_EXCHANGE_RATE.getMessage(), BankingServiceError.NO_EXCHANGE_RATE.getErrorCode());
        }
        return optionalExchangeRate.get();
    }

    private static List<ExchangeRate> getExchangeRateList(ResponseEntity<ExchangeRateDto> response, CurrencyType baseCurrency) {
        ExchangeRateDto responseBody = response.getBody();

        assert responseBody != null;

        List<ExchangeRate> exchangeRates = new ArrayList<>();

        Map<String, BigDecimal> exhangeRatesMap = responseBody.getConversionRates();
        exhangeRatesMap.forEach((currencyCode, rate) -> {
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setCurrencyCode(currencyCode);
            exchangeRate.setBaseCurrency(baseCurrency.toString());
            exchangeRate.setCurrencyRate(rate);
            exchangeRates.add(exchangeRate);
        });
        return exchangeRates;
    }
}
