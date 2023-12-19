package com.example.bankingportal.dto;

import com.example.bankingportal.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegisterResponseDto {

    private String name;
    private String email;
    private String address;
    private String userId;
    private String phone_number;
    private List<String> accountNumber;

    public static RegisterResponseDto toRegisterResponseDto(User user){
        return RegisterResponseDto.builder().name(user.getName()).email(user.getEmail()).address(user.getAddress())
                .phone_number(user.getPhoneNumber()).userId(user.getUserId())
                .build();
    }

}
