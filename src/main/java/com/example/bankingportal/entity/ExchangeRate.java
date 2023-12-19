package com.example.bankingportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(indexes =  @Index(columnList = "baseCurrency,currencyCode"))
public class ExchangeRate extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String baseCurrency;

    private String currencyCode;

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setCurrencyRate(BigDecimal currencyRate) {
        this.currencyRate = currencyRate;
    }

    private BigDecimal currencyRate;
}
