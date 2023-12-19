package com.example.bankingportal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ExchangeRateDto {

    @JsonProperty(value = "result")
    private String result;

    @JsonProperty(value = "base_code")
    private String base_code;
    @JsonProperty(value = "documentation")
    private String documentation;

    @JsonProperty(value = "terms_of_use")
    private String termsOfUse;
    @JsonProperty(value = "time_last_update_unix")
    private long timeLastUpdateUnix;
    @JsonProperty(value = "time_last_update_utc")
    private String timeLastUpdateUtc;
    @JsonProperty(value = "time_next_update_unix")
    private long timeNextUpdateUnix;
    @JsonProperty(value = "time_next_update_utc")
    private String timeNextUpdateUtc;
    @JsonProperty(value = "conversion_rates")
    private Map<String, BigDecimal> conversionRates;


    @Override
    public String toString() {
        return "ExchangeRateApiResponse{" +
                "result='" + result + '\'' +
                ", documentation='" + documentation + '\'' +
                ", termsOfUse='" + termsOfUse + '\'' +
                ", timeLastUpdateUnix=" + timeLastUpdateUnix +
                ", timeLastUpdateUtc='" + timeLastUpdateUtc + '\'' +
                ", timeNextUpdateUnix=" + timeNextUpdateUnix +
                ", timeNextUpdateUtc='" + timeNextUpdateUtc + '\'' +
                ", conversionRates=" + conversionRates +
                '}';
    }


}
