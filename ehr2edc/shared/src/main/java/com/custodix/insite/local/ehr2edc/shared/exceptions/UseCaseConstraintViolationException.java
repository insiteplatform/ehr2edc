package com.custodix.insite.local.ehr2edc.shared.exceptions;

import static java.util.Collections.unmodifiableCollection;

import java.util.Arrays;
import java.util.Collection;

public class UseCaseConstraintViolationException extends RuntimeException {
	private final Collection<UseCaseConstraintViolation> constraintViolations;

	public UseCaseConstraintViolationException(Collection<UseCaseConstraintViolation> constraintViolations) {
		this.constraintViolations = constraintViolations;
	}

	public Collection<UseCaseConstraintViolation> getConstraintViolations() {
		return unmodifiableCollection(constraintViolations);
	}

	@Override
	public String getMessage() {
		return Arrays.toString(constraintViolations.toArray());
	}

}
