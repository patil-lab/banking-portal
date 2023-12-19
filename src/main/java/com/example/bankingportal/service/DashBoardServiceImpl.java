package com.example.bankingportal.service;

import com.example.bankingportal.constants.CurrencyType;
import com.example.bankingportal.constants.TransactionType;
import com.example.bankingportal.dto.CustomPageResponse;
import com.example.bankingportal.dto.TransactionDto;
import com.example.bankingportal.entity.ExchangeRate;
import com.example.bankingportal.entity.Transaction;
import com.example.bankingportal.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class DashBoardServiceImpl implements DashBoardService {

    private final Logger LOGGER = LoggerFactory.getLogger(DashBoardServiceImpl.class);
    private final TransactionRepository transactionRepository;

    private final ExchangeRateService exchangeRateService;

    @Autowired
    public DashBoardServiceImpl(TransactionRepository transactionRepository, ExchangeRateService exchangeRateService) {
        this.transactionRepository = transactionRepository;
        this.exchangeRateService = exchangeRateService;
    }

    @Override
    public CustomPageResponse<TransactionDto> getTransactions(PageRequest pageRequest, String userId, CurrencyType currencyType) {
        LOGGER.info("GetTransactions for userId: " + userId);
        Page<Transaction> transactionsPage = transactionRepository.findByUserId(userId, pageRequest);

        List<Transaction> transactions = transactionsPage.getContent();

        BigDecimal totalCredit = BigDecimal.ZERO;
        BigDecimal totalDebit = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            // Fetch the exchange rate for the transaction's currency
            ExchangeRate exchangeRate = exchangeRateService.getExchangeRate(transaction.getTransactionCurrency(), currencyType);


            if (TransactionType.CASH_DEPOSIT.equals(transaction.getTransactionType())) {
                BigDecimal convertedCredit = transaction.getAmount().multiply(exchangeRate.getCurrencyRate());
                totalCredit = totalCredit.add(convertedCredit);
            }

            if (TransactionType.CASH_WITHDRAWAL.equals(transaction.getTransactionType())) {
                BigDecimal convertedDebit = transaction.getAmount().multiply(exchangeRate.getCurrencyRate());
                totalDebit = totalDebit.add(convertedDebit);
            }
        }


        return new CustomPageResponse<>(transactionsPage.getContent().stream().map(TransactionDto::toTransactionDto).toList(), transactionsPage.getNumber(),
                transactionsPage.getSize(), transactionsPage.getTotalPages(), transactionsPage.getTotalElements(), totalCredit, totalDebit);
    }


}
