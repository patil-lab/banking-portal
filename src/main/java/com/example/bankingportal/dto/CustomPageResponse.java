package com.example.bankingportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomPageResponse<T> {

        private List<T> content;
        private int page;
        private int size;
        private int totalPages;
        private long totalElements;
        private BigDecimal totalCredit;
        private BigDecimal totalDebit;

        // Constructors, getters, setters

}
