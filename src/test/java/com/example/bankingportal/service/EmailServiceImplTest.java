package com.example.bankingportal.service;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Test
    void testSendVerificationEmail() {

        JavaMailSender mockJavaMailSender = mock(JavaMailSender.class);

        EmailServiceImpl emailServiceimpl = new EmailServiceImpl(mockJavaMailSender);

        // Call the method to be tested
        emailServiceimpl.sendVerificationEmail("test@example.com", "verificationToken");

        // wait for async task to send mail
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            // Verify that send method was called on the mockJavaMailSender
            verify(mockJavaMailSender).send(any(SimpleMailMessage.class));
        });
    }
}