package com.custodix.insite.local.ehr2edc.shared.exceptions;

public class UserException extends RuntimeException {
	private final String message;

	public UserException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
