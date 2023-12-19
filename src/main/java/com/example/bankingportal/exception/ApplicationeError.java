package com.example.bankingportal.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApplicationeError {

	@Expose
	private String message="An UnknownError Occured";

	@Expose
	private int code;


	@Expose
	@JsonIgnore
	private HttpStatus httpStatus;

	public ApplicationeError() {
	}

	public ApplicationeError(String message) {
		this.message = message;
	}

	public ApplicationeError(String message, int code) {
		this.message = message;
		this.code = code;
	}

	public ApplicationeError(String message, int code, HttpStatus httpStatus) {
		this.message = message;
		this.code = code;
		this.httpStatus = httpStatus;
	}
}
