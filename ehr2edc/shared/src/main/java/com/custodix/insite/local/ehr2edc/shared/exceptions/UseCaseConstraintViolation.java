package com.custodix.insite.local.ehr2edc.shared.exceptions;

import java.io.Serializable;
import java.util.Objects;

public final class UseCaseConstraintViolation implements Serializable {
	public static final long serialVersionUID = 1L;

	private final String message;
	private final String field;

	UseCaseConstraintViolation(String field, String message) {
		this.message = message;
		this.field = field;
	}

	public static UseCaseConstraintViolation constraintViolation(String field, String message) {
		return new UseCaseConstraintViolation(field, message);
	}

	public String getMessage() {
		return message;
	}

	public String getField() {
		return field;
	}

	@Override
	public String toString() {
		return field + ": " + message;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UseCaseConstraintViolation that = (UseCaseConstraintViolation) o;
		return Objects.equals(message, that.message) && Objects.equals(field, that.field);
	}

	@Override
	public int hashCode() {
		return Objects.hash(message, field);
	}
}