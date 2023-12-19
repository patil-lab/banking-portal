package com.example.bankingportal.constants;

import org.springframework.http.HttpStatus;

public enum BankingServiceError {
    UNKNOWN(0, "unknown error", HttpStatus.BAD_REQUEST),
    USER_DO_NOT_EXIST(1001,"User does not exist",HttpStatus.BAD_REQUEST),
    CLIENT_SIDE_ERROR(1002,"Client side error",HttpStatus.BAD_REQUEST),

    INVALID_JWT_TOKEN(1003,"Invalid Token",HttpStatus.BAD_REQUEST),

    ACCOUNT_NOT_FOUND(1004,"Account Not Found",HttpStatus.BAD_REQUEST),

    PASSWORD_DOES_NOT_MATCH(1005,"Password Does not Match",HttpStatus.BAD_REQUEST),

    PIN_DOES_NOT_MATCH(1006,"PIN Does not Match",HttpStatus.BAD_REQUEST),

    REVERIFY_EMAIL(1007,"Please verify your email and re-initiate transaction",HttpStatus.BAD_REQUEST),

    INSUFFICIENT_BALANCE(1008,"Insufficient Balance",HttpStatus.BAD_REQUEST),

    PLEASE_INPUT_TARGET_ACCOUNT(1009,"Please Input Target Account",HttpStatus.BAD_REQUEST),

    CURRENCY_MISMATCH(1010,"Source and Target Account Currency do not Match",HttpStatus.BAD_REQUEST),

    INVALID_TOKEN(1011,"Invalid Verification Token",HttpStatus.BAD_REQUEST),

    TOKEN_EXPIRED(1012,"Verification Token Expired",HttpStatus.BAD_REQUEST),

    NO_EXCHANGE_RATE(1013,"Exchange rate not found",HttpStatus.BAD_REQUEST);

    private final int errorCode;
    private final String message;
    private final HttpStatus httpStatus;

    BankingServiceError(int errorCode ,String message,HttpStatus status){
        this.message=message;
        this.errorCode=errorCode;
        this.httpStatus=status;
    }

    public String getMessage(){
        return this.message;
    }

    public int getErrorCode(){
        return this.errorCode;
    }

    public HttpStatus getHttpStatus(){
        return this.httpStatus;
    }
}
