package com.example.bankingportal.handler;


import com.example.bankingportal.constants.BankingServiceError;
import com.example.bankingportal.exception.BankingServiceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Component
public class CustomResponseErrorHandler implements ResponseErrorHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomResponseErrorHandler.class);
	private final ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

	@Value("${spring.application.name}")
	private String serviceName;

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return errorHandler.hasError(response);
	}

	@Override
	public void handleError(ClientHttpResponse response) {

		String theString = responseBodyToStr(response);
		theString = StringUtils.substring(theString, 1, theString.length() - 1);
		LOGGER.error(theString);
		throw new BankingServiceException(serviceName, BankingServiceError.CLIENT_SIDE_ERROR.getMessage(),BankingServiceError.CLIENT_SIDE_ERROR.getErrorCode());
	}


	private String responseBodyToStr(ClientHttpResponse response) {
		return new String(getResponseBody(response), StandardCharsets.UTF_8);
	}

	private byte[] getResponseBody(ClientHttpResponse response) {
		try {
			return FileCopyUtils.copyToByteArray(response.getBody());
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage());
			return new byte[0];
		}
	}
}