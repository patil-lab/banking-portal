package com.example.bankingportal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String name;
    private String password;

    @Column(unique = true)
    private String email;

    private boolean isEmailVerified;
    private String address;
    private String phoneNumber;
    private int otpRetryCount;
    private LocalDateTime lastOtpRequestTime;

    // Establishing a one-to-one relationship with the account
    @OneToMany(mappedBy = "user")
    private List<Account> account;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id",referencedColumnName = "id")
    private VerificationToken token;

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUserId(String userId){this.userId=userId;}

    public void setPhoneNumber(String phone_number) {
        this.phoneNumber = phone_number;
    }

    // Convenience method to set the user's account
    public void setAccount(List<Account> account) {
        this.account = account;
    }

    public void setToken(VerificationToken  token){
        this.token=token;
    }

    public void setOtpRetryCount(int otpRetryCount) {
        this.otpRetryCount = otpRetryCount;
    }

    public void setLastOtpRequestTime(LocalDateTime lastOtpRequestTime) {
        this.lastOtpRequestTime = lastOtpRequestTime;
    }

    public void setEmailVerified(boolean isEmailVerified){
        this.isEmailVerified=isEmailVerified;
    }

}
