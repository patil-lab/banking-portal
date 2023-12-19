package com.example.bankingportal.service;

import com.example.bankingportal.dto.RegisterResponseDto;
import com.example.bankingportal.dto.UpdateUserDto;
import com.example.bankingportal.entity.User;
import com.example.bankingportal.entity.VerificationToken;

public interface UserService {

    public RegisterResponseDto registerUser(User user);

    public void verifyEmail(String token);

    RegisterResponseDto getUserDetailsByUserId(String userId);

    public void resendVerificationEmail(String accountNumber);

    User updateUser(UpdateUserDto updateUserDto);
}
