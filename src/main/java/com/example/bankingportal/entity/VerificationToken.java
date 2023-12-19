package com.example.bankingportal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class VerificationToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;


    @OneToOne(mappedBy = "token",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private User user;

    private LocalDateTime expiryDate;

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isExpired(){
        LocalDateTime now=LocalDateTime.now();

        return now.isAfter(expiryDate);
    }
}