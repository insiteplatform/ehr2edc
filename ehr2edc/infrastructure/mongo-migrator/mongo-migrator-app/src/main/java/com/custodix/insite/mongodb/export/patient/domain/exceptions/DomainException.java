package com.custodix.insite.mongodb.export.patient.domain.exceptions;

public class DomainException extends RuntimeException {
	public DomainException(String message) {
		super(message);
	}

	public DomainException(String message, Throwable throwable) {
		super(message, throwable);
	}
}