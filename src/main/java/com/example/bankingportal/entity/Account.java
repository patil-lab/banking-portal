package com.example.bankingportal.entity;

import com.example.bankingportal.constants.CurrencyType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Account extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column( unique = true)
    private String accountNumber;
    private BigDecimal balance;
    private String account_type = "Saving";
    private String branch = "Bangalore";
    private String IFSC_code = "BLR001";
    @Enumerated(EnumType.STRING)
    private CurrencyType currencyType;
    private String Pin;
    @Enumerated(EnumType.STRING)
    private AccountStatus account_status;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
