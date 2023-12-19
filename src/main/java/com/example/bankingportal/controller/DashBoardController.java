package com.example.bankingportal.controller;

import com.example.bankingportal.constants.CurrencyType;
import com.example.bankingportal.dto.BankingServiceResult;
import com.example.bankingportal.dto.CustomPageResponse;
import com.example.bankingportal.dto.TransactionDto;
import com.example.bankingportal.entity.Transaction;
import com.example.bankingportal.service.DashBoardService;
import com.example.bankingportal.util.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1.0/dashboard")
public class DashBoardController {

    private final DashBoardService dashBoardService;

    @Autowired
    public DashBoardController(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
    }

    @GetMapping("/transactions")
    public ResponseEntity<BankingServiceResult<CustomPageResponse<TransactionDto>>> getTransactions(@RequestParam(value = "offset", required = false) Integer offset,
                                                                                                    @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                                                                    @RequestParam(value = "currency") CurrencyType currencyType
    ) {
        if (null == offset) offset = 0;
        if (null == pageSize) pageSize = 10;
        return ResponseEntity.ok(BankingServiceResult.succeed(dashBoardService.getTransactions(PageRequest.of(offset, pageSize, Sort.by(Sort.Order.asc("id"))), CurrentUser.getUserId(),currencyType)));

    }
}
