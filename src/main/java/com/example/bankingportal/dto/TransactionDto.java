package com.example.bankingportal.dto;

import com.example.bankingportal.constants.TransactionType;
import com.example.bankingportal.entity.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    @NotNull
    @Positive(message = "Amount Value Should be Positive")

    private BigDecimal amount;
    @NotNull

    private TransactionType transaction_type;
    @NotBlank(message = "Source account cannot be blank")

    private String sourceAccountNumber;

    private String targetAccountNumber;

    @NotBlank(message = "pin cannot be blank")
    private String sourcePin;


    public static TransactionDto toTransactionDto(Transaction transaction){
        TransactionDto transactionDto=new TransactionDto();
        transactionDto.setTransaction_type(transaction.getTransactionType());
        transactionDto.setAmount(transaction.getAmount());
        transactionDto.setSourceAccountNumber(transaction.getSourceAccount());
        return transactionDto;
    }
}
