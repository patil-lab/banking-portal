package com.example.bankingportal.service;

import com.example.bankingportal.constants.BankingServiceError;
import com.example.bankingportal.exception.BankingServiceException;
import org.eclipse.angus.mail.smtp.SMTPSendFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${banking.service.url}")
    private String bankingServiceUrl;

    private final JavaMailSender javaMailSender;

    @Value("${spring.application.name}")
    private String serviceName;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async("processExecutor")
    @Override
    public void sendVerificationEmail(String toEmail, String verificationToken) {

       try {
           SimpleMailMessage message = new SimpleMailMessage();
           message.setTo(toEmail);
           message.setSubject("Verify your email");
           message.setText("Click the link to verify your email: " +
                   bankingServiceUrl+"/verify-email?token=" + verificationToken);

           javaMailSender.send(message);

       }catch ( Exception e){
           throw new BankingServiceException(serviceName, BankingServiceError.REVERIFY_EMAIL.getMessage(), BankingServiceError.REVERIFY_EMAIL.getErrorCode());
       }

    }
}
