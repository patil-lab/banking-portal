package com.example.bankingportal.entity;

import com.example.bankingportal.constants.CurrencyType;
import com.example.bankingportal.constants.TransactionType;
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
@Table(indexes = @Index(columnList = "transactionId,userId"))
public class Transaction extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;

    private String userId;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private CurrencyType transactionCurrency;

    private BigDecimal amount=new BigDecimal(0);

    private String sourceAccount;

    private String targetAccount;
}
