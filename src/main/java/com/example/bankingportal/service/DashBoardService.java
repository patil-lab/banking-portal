package com.example.bankingportal.service;

import com.example.bankingportal.constants.CurrencyType;
import com.example.bankingportal.dto.CustomPageResponse;
import com.example.bankingportal.dto.TransactionDto;
import com.example.bankingportal.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface DashBoardService {

    public CustomPageResponse<TransactionDto> getTransactions(PageRequest pageRequest, String userID, CurrencyType currencyType) ;
}
