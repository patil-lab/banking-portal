package com.example.bankingportal.config;

import com.example.bankingportal.entity.Transaction;
import com.example.bankingportal.service.TransactionService;
import com.example.bankingportal.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaTransactionListener {

    private final TransactionService transactionService;

    private final Logger LOGGER = LoggerFactory.getLogger(KafkaTransactionListener.class);

    @Autowired
    public KafkaTransactionListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "patil-topic",groupId = "patil-group-id",containerFactory = "listenerContainerFactory")
    public void consumeTransaction(List<Transaction> transactions) {
        // Process the consumed transaction

        LOGGER.info("Transaction received:");
        transactions.forEach(transaction -> LOGGER.info(transaction.toString()));

        transactionService.saveTransactions(transactions);





    }
}
