package com.example.bankingportal.dto;

import com.example.bankingportal.constants.CurrencyType;
import com.example.bankingportal.entity.Account;
import com.example.bankingportal.entity.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {

    private String accountNumber;

    private BigDecimal balance;

    private String account_type;

    private String branch;

    private String IFSC_code ;

    private CurrencyType currencyType;

    private AccountStatus account_status;

    private String userId;

    public static AccountDto toAccountDto(Account account){
        return AccountDto.builder().accountNumber(account.getAccountNumber()).balance(account.getBalance()).account_type(account.getAccount_type())
                .branch(account.getBranch()).IFSC_code(account.getIFSC_code()).currencyType(account.getCurrencyType()).account_status(account.getAccount_status())
                .userId(account.getUser().getUserId()).build();
    }
}
