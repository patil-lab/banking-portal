package com.example.bankingportal.service;

import com.example.bankingportal.entity.Transaction;
import com.example.bankingportal.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void saveTransactions(List<Transaction> transactions) {
        transactionRepository.saveAll(transactions);
    }


}
