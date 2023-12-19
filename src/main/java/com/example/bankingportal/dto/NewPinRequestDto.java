package com.example.bankingportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewPinRequestDto {
    private String accountNumber;
    private String pin;
    private String password ;
}
