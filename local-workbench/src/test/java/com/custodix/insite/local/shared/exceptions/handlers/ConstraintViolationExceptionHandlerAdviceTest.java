package com.custodix.insite.local.shared.exceptions.handlers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.Before;
import org.junit.Test;

public class ConstraintViolationExceptionHandlerAdviceTest {
	private ConstraintViolationExceptionHandlerAdvice handler;

	@Before
	public void before() {
		handler = new ConstraintViolationExceptionHandlerAdvice();
	}

	@Test
	public void defaultMessageParsing() {
		ConstraintViolationException exception = createException("updateSecurityQuestion.arg0.securityQuestionId",
				"must not be blank");
		String message = handler.constraintViolationException(exception);
		assertThat(message).isEqualTo("Security question id must not be blank");
	}

	@Test
	public void passwordRuleMessageParsing() {
		ConstraintViolationException exception = createException("updatePassword.arg0.newPassword.value",
				"Password must be 8 or more characters in length");
		String message = handler.constraintViolationException(exception);
		assertThat(message).isEqualTo("Password must be 8 or more characters in length");
	}

	private ConstraintViolationException createException(String propertyPath, String message) {
		Set<ConstraintViolation<?>> violations = new HashSet<>();
		violations.add(new TestViolation(propertyPath, message));
		return new ConstraintViolationException(violations);
	}

	private class TestViolation implements ConstraintViolation {
		private final String propertyPath;
		private final String message;

		private TestViolation(String propertyPath, String message) {
			this.propertyPath = propertyPath;
			this.message = message;
		}

		@Override
		public String getMessage() {
			return message;
		}

		@Override
		public String getMessageTemplate() {
			return null;
		}

		@Override
		public Object getRootBean() {
			return null;
		}

		@Override
		public Class getRootBeanClass() {
			return null;
		}

		@Override
		public Object getLeafBean() {
			return null;
		}

		@Override
		public Object[] getExecutableParameters() {
			return new Object[0];
		}

		@Override
		public Object getExecutableReturnValue() {
			return null;
		}

		@Override
		public Path getPropertyPath() {
			return PathImpl.createPathFromString(propertyPath);
		}

		@Override
		public Object getInvalidValue() {
			return null;
		}

		@Override
		public ConstraintDescriptor<?> getConstraintDescriptor() {
			return null;
		}

		@Override
		public Object unwrap(Class type) {
			return null;
		}
	}
}