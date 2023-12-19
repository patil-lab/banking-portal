package com.example.bankingportal.service;

import com.example.bankingportal.constants.CurrencyType;
import com.example.bankingportal.dto.AccountDto;
import com.example.bankingportal.dto.AccountResponseDto;
import com.example.bankingportal.dto.TransactionDto;
import com.example.bankingportal.entity.Account;

public interface AccountService {
    Account createAccount(String  userId, CurrencyType currencyType);
    boolean isPinCreated(String accountNumber) ;
    void createPIN(String accountNumber, String password, String pin) ;
    void updatePIN(String accountNumber, String oldPIN, String password, String newPIN);
    void cashDeposit(TransactionDto transactionDto);
    void cashWithdrawal(TransactionDto transactionDto);
    void fundTransfer(TransactionDto transactionDto);

    AccountResponseDto getAccountDetailsByUser(String userid);
}
