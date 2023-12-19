package com.example.bankingportal.exception;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class BankingServiceException extends RuntimeException {


    @Expose
    private String service;

    @Expose
    private ApplicationeError error;

    private static final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public BankingServiceException(String service,String message,int code){
        this.error=new ApplicationeError();
        this.service=service;
        this.getError().setMessage(message);
        this.getError().setCode(code);
    }

    public ApplicationeError getError() {
        return this.error;
    }

    public void setError(ApplicationeError error) {
        this.error = error;
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public String getMessage() {
        return gson.toJson(this);
    }

}
