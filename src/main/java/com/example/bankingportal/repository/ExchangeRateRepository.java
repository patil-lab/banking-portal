package com.example.bankingportal.repository;

import com.example.bankingportal.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate,Long> {

    Optional<ExchangeRate> findByBaseCurrencyAndCurrencyCode(String baseCurrency,String currencyCode);
}
