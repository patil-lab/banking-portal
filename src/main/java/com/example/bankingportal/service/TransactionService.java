package com.example.bankingportal.service;

import com.example.bankingportal.entity.Transaction;

import java.util.List;

public interface TransactionService {

    void saveTransactions(List<Transaction> transactions);
}
