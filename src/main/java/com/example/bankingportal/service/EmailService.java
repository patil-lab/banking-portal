package com.example.bankingportal.service;

import java.util.concurrent.CompletableFuture;

public interface EmailService {

    public void sendVerificationEmail(String toEmail, String verificationToken);
}
