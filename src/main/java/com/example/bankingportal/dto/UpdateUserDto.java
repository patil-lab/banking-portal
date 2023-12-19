package com.example.bankingportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    private String name;

    private String password;
    @Email(message = "Invalid email format")
    private String email;
    private String address;

    private String phoneNumber;
}
