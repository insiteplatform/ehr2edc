package com.custodix.insite.local.ehr2edc.audit;

import com.custodix.insite.local.ehr2edc.shared.annotations.Audit;

@Audit
class TestAuditedClass {
	boolean successMethod() {
		return true;
	}

	boolean failureMethod() {
		throw new FailureException("An error occurred");
	}

	boolean aDifferentSuccessMethod() {
		return true;
	}

	boolean aDifferentFailureMethod() {
		throw new FailureException("An error occurred");
	}

	Argument aMethodWithArgument(Argument argument) {
		return argument;
	}

	static class FailureException extends RuntimeException {
		FailureException(String message) {
			super(message);
		}
	}

	static class Argument {
		private final String name;
		private final String surname;

		Argument(String name, String surname) {
			this.name = name;
			this.surname = surname;
		}
	}
}