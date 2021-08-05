package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException;
import com.fasterxml.jackson.databind.ObjectMapper;

class OpenClinicaCreateSubjectHttpErrorHandler {
	private final ObjectMapper objectMapper;
	private final ErrorTranslationService errorTranslationService;

	OpenClinicaCreateSubjectHttpErrorHandler(ObjectMapper objectMapper,
			ErrorTranslationService errorTranslationService) {
		this.objectMapper = objectMapper;
		this.errorTranslationService = errorTranslationService;
	}

	void handle(HttpClientErrorException e1) {
		if (e1.getStatusCode() == HttpStatus.BAD_REQUEST) {
			handleBadRequest(e1.getResponseBodyAsString());
		} else {
			throw e1;
		}
	}

	private void handleBadRequest(String responseString) {
		CreateSubject400Response response = parse400Response(responseString);
		String errorCode = response.getMessage();
		String errorMessage = errorTranslationService.translate(errorCode);
		throw new UserException(errorMessage);
	}

	private CreateSubject400Response parse400Response(String responseString) {
		try {
			return objectMapper.readValue(responseString, CreateSubject400Response.class);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static final class CreateSubject400Response {
		private String message;

		public String getMessage() {
			return message;
		}
	}
}
