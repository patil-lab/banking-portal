package com.example.bankingportal.util;

import com.example.bankingportal.constants.BankingServiceError;
import com.example.bankingportal.exception.BankingServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUser {


    @Value("${spring.application.name}")
    private static String serviceName;

    public static String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)authentication.getPrincipal();

            if (principal != null) {
                return principal.getUsername();
            }
        }
        throw new BankingServiceException(serviceName, BankingServiceError.USER_DO_NOT_EXIST.getMessage(), BankingServiceError.USER_DO_NOT_EXIST.getErrorCode());
    }

}