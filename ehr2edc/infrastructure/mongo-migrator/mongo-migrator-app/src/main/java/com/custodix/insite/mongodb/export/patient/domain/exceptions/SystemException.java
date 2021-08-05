package com.custodix.insite.mongodb.export.patient.domain.exceptions;

public class SystemException extends RuntimeException {

	public SystemException(String message) {
		super(message);
	}

	public SystemException(String message, Exception e) {
		super(message, e);
	}
}
