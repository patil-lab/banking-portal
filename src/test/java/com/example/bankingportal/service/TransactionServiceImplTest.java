package com.example.bankingportal.service;

import com.example.bankingportal.entity.Transaction;
import com.example.bankingportal.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Test
    void testSaveTransactions() {
        when(transactionRepository.saveAll(any())).thenReturn(List.of(new Transaction(),new Transaction()));
        transactionService.saveTransactions(List.of(new Transaction(),new Transaction()));
        verify(transactionRepository, times(1)).saveAll(List.of(new Transaction(),new Transaction()));

    }
}