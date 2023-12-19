package com.example.bankingportal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankingServiceResult<T> {
    @JsonProperty("succeed")
    private boolean succeed;

    @JsonProperty("content")
    private T content;

    @JsonProperty("error")
    private T error;


    public static <T> BankingServiceResult<T> succeed(T content){
        return BankingServiceResult.<T>builder()
                .succeed(true)
                .content(content)
                .error(null)
                .build();
    }

    public static <T> BankingServiceResult<T> fail(T error){
        return BankingServiceResult.<T>builder()
                .succeed(false)
                .content(null)
                .error(error)
                .build();
    }
}
