package com.example.bankingportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePinRequestDto {

    private String oldPin;
    private String newPin;
    private String password;

}
