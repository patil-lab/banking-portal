package com.example.bankingportal.config;import com.example.bankingportal.handler.CustomResponseErrorHandler;import org.springframework.context.annotation.Bean;import org.springframework.context.annotation.Configuration;import org.springframework.web.client.RestTemplate;@Configurationpublic class RestTemplateConfig {    public static final String DEFAULT_REST_TEMPLATE = "restTemplate";    @Bean(DEFAULT_REST_TEMPLATE)    public RestTemplate restTemplate(){        RestTemplate restTemplate = new RestTemplate();        restTemplate.setErrorHandler(new CustomResponseErrorHandler());        return new RestTemplate();    }}