package com.custodix.insite.mongodb.export.patient.domain.exceptions;

public class SubjectMigrationAlreadyStartedException extends DomainException {
	public SubjectMigrationAlreadyStartedException(final String message) {
		super(message);
	}
}
