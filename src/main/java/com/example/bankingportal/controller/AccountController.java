package com.example.bankingportal.controller;

import com.example.bankingportal.constants.CurrencyType;
import com.example.bankingportal.dto.*;
import com.example.bankingportal.service.AccountService;
import com.example.bankingportal.util.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1.0/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BankingServiceResult<AccountResponseDto>> getAccountDetails(@PathVariable String userId){
        return ResponseEntity.ok(BankingServiceResult.succeed(accountService.getAccountDetailsByUser(userId)));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<BankingServiceResult<String>> createAccount(@PathVariable String userId, @RequestParam CurrencyType currencyType){
        accountService.createAccount(userId,currencyType);
        return ResponseEntity.ok(BankingServiceResult.succeed(HttpStatus.OK.toString()));
    }

    @GetMapping("/pin")
    public ResponseEntity<?> checkAccountPIN(@RequestParam String accountNumber) {
        boolean isPINValid = accountService.isPinCreated(accountNumber);


        Map<String, Object> result =  new HashMap<>();
        result.put("hasPIN",isPINValid );

        if (isPINValid) {
            result.put("msg", "PIN Created");

        } else {
            result.put("msg", "Pin Not Created");
        }

        return new ResponseEntity<>( result, HttpStatus.OK);
    }

    @PostMapping("/pin")
    public ResponseEntity<?> createPIN(@RequestBody NewPinRequestDto pinRequest) {
        accountService.createPIN(pinRequest.getAccountNumber(), pinRequest.getPassword(), pinRequest.getPin());

        Map<String, String> response =  new HashMap<>();
        response.put("msg", "PIN created successfully");

        return new ResponseEntity<>( response, HttpStatus.OK);


    }

    @PatchMapping("/pin")
    public ResponseEntity<?> updatePIN(@RequestBody UpdatePinRequestDto pinUpdateRequest) {
        accountService.updatePIN(CurrentUser.getUserId(), pinUpdateRequest.getOldPin(), pinUpdateRequest.getPassword(), pinUpdateRequest.getNewPin());

        Map<String, String> response =  new HashMap<>();
        response.put("msg", "PIN updated successfully");

        return new ResponseEntity<>( response, HttpStatus.OK);

    }

    @PostMapping("/deposit")
    public ResponseEntity<BankingServiceResult<String>> cashDeposit(@RequestBody @Valid TransactionDto transactionDto){
        accountService.cashDeposit(transactionDto);
        return ResponseEntity.ok(BankingServiceResult.succeed(HttpStatus.OK.toString()));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<BankingServiceResult<String>> cashWithdrawal(@RequestBody @Valid TransactionDto transactionDto){
        accountService.cashWithdrawal(transactionDto);
        return ResponseEntity.ok(BankingServiceResult.succeed(HttpStatus.OK.toString()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<BankingServiceResult<String>> fundTransfer(@RequestBody @Valid TransactionDto transactionDto){
        accountService.fundTransfer(transactionDto);
        return ResponseEntity.ok(BankingServiceResult.succeed(HttpStatus.OK.toString()));
    }
}
