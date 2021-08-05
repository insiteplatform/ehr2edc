package eu.ehr4cr.workbench.local.testingutils;

import java.util.Arrays;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;

import com.google.common.collect.Iterables;

public class ConstraintViolationAssert extends AbstractAssert<ConstraintViolationAssert, ConstraintViolationException> {
	private static final String DEFAULT_FIELD = StringUtils.EMPTY;

	private ConstraintViolationAssert(final ConstraintViolationException constraintViolation) {
		super(constraintViolation, ConstraintViolationAssert.class);
	}

	public static ConstraintViolationAssert assertThat(final ConstraintViolationException constraintViolation) {
		return new ConstraintViolationAssert(constraintViolation);
	}

	public ConstraintViolationAssert containsExactly(final String field, final String message) {
		contains(field, message).hasSize(1);
		return this;
	}

	public ConstraintViolationAssert containsExactly(final String message) {
		contains(message).hasSize(1);
		return this;
	}

	public ConstraintViolationAssert contains(final String field, final String message) {
		isNotNull();
		Assertions.assertThat(actual.getConstraintViolations())
				.areExactly(1, constraintViolationEqualTo(field, message));
		return this;
	}

	public ConstraintViolationAssert contains(final String message) {
		contains(DEFAULT_FIELD, message);
		return this;
	}

	public ConstraintViolationAssert hasSize(final int expected) {
		Assertions.assertThat(actual.getConstraintViolations())
				.as("Check the number of constraint violations in ConstraintViolationException")
				.hasSize(expected);
		return this;
	}

	private Condition<ConstraintViolation> constraintViolationEqualTo(final String field, final String message) {
		return new ConstraintViolationCondition(field, message).as(" constraint violation for field: %s with message: %s",
				field, message);
	}

	private static final class ConstraintViolationCondition extends Condition<ConstraintViolation> {
		private final String field;
		private final String message;

		ConstraintViolationCondition(String field, String message) {
			this.field = field;
			this.message = message;
		}

		@Override
		public boolean matches(final ConstraintViolation exception) {
			return matchesMessage(exception) && matchesField(exception);
		}

		private boolean matchesField(final ConstraintViolation exception) {
			if (StringUtils.isNotBlank(field)) {
				String fieldExpected = Iterables.getLast(Arrays.asList(field.split("\\.")));
				String fieldActual = Iterables.getLast(exception.getPropertyPath())
						.getName();
				return fieldExpected.equals(fieldActual);
			}
			return true;
		}

		private boolean matchesMessage(final ConstraintViolation exception) {
			return exception.getMessage() != null && exception.getMessage()
					.equals(message);
		}
	}
}
