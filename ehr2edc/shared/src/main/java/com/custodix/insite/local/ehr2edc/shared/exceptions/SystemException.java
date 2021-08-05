package com.custodix.insite.local.ehr2edc.shared.exceptions;

public class SystemException extends RuntimeException {

	public SystemException(String message, Throwable cause) {
		super(message, cause);
	}

	public SystemException(String message) {
		super(message);
	}

	public static SystemException of(String message) {
		return new SystemException(message);
	}
}
