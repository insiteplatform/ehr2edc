package eu.ehr4cr.workbench.local.usecases.userrecovery;

import java.util.Locale;
import java.util.Set;

import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.NotThreadSafe;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import eu.ehr4cr.workbench.local.model.security.User;

/**
 * Immutable implementation of {@link AbstractRetrieveUserSecurityQuestionRequest}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code RetrieveUserSecurityQuestionRequest.builder()}.
 */
@SuppressWarnings({ "all" })

@ParametersAreNonnullByDefault
@Generated({ "Immutables.generator", "AbstractRetrieveUserSecurityQuestionRequest" })

public final class RetrieveUserSecurityQuestionRequest implements AbstractRetrieveUserSecurityQuestionRequest {
	private final User user;
	private final Locale locale;

	private RetrieveUserSecurityQuestionRequest(User user, Locale locale) {
		this.user = user;
		this.locale = locale;
	}

	/**
	 * @return The value of the {@code user} attribute
	 */
	@Override
	public User getUser() {
		return user;
	}

	/**
	 * @return The value of the {@code locale} attribute
	 */
	@Override
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractRetrieveUserSecurityQuestionRequest#getUser() user} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for user (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final RetrieveUserSecurityQuestionRequest withUser(User value) {
		if (this.user == value)
			return this;
		return validate(new RetrieveUserSecurityQuestionRequest(value, this.locale));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractRetrieveUserSecurityQuestionRequest#getLocale() locale} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for locale (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final RetrieveUserSecurityQuestionRequest withLocale(Locale value) {
		if (this.locale == value)
			return this;
		return validate(new RetrieveUserSecurityQuestionRequest(this.user, value));
	}

	/**
	 * This instance is equal to all instances of {@code RetrieveUserSecurityQuestionRequest} that have equal attribute values.
	 * @return {@code true} if {@code this} is equal to {@code another} instance
	 */
	@Override
	public boolean equals(@Nullable Object another) {
		if (this == another)
			return true;
		return another instanceof RetrieveUserSecurityQuestionRequest && equalTo(
				(RetrieveUserSecurityQuestionRequest) another);
	}

	private boolean equalTo(RetrieveUserSecurityQuestionRequest another) {
		return Objects.equal(user, another.user) && Objects.equal(locale, another.locale);
	}

	/**
	 * Computes a hash code from attributes: {@code user}, {@code locale}.
	 * @return hashCode value
	 */
	@Override
	public int hashCode() {
		int h = 5381;
		h += (h << 5) + Objects.hashCode(user);
		h += (h << 5) + Objects.hashCode(locale);
		return h;
	}

	/**
	 * Prints the immutable value {@code RetrieveUserSecurityQuestionRequest} with attribute values.
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("RetrieveUserSecurityQuestionRequest")
				.omitNullValues()
				.add("user", user)
				.add("locale", locale)
				.toString();
	}

	private static final Validator validator = Validation.buildDefaultValidatorFactory()
			.getValidator();

	private static RetrieveUserSecurityQuestionRequest validate(RetrieveUserSecurityQuestionRequest instance) {
		Set<ConstraintViolation<RetrieveUserSecurityQuestionRequest>> constraintViolations = validator.validate(
				instance);
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(constraintViolations);
		}
		return instance;
	}

	/**
	 * Creates an immutable copy of a {@link AbstractRetrieveUserSecurityQuestionRequest} value.
	 * Uses accessors to get values to initialize the new immutable instance.
	 * If an instance is already immutable, it is returned as is.
	 * @param instance The instance to copy
	 * @return A copied immutable RetrieveUserSecurityQuestionRequest instance
	 */
	static RetrieveUserSecurityQuestionRequest copyOf(AbstractRetrieveUserSecurityQuestionRequest instance) {
		if (instance instanceof RetrieveUserSecurityQuestionRequest) {
			return (RetrieveUserSecurityQuestionRequest) instance;
		}
		return RetrieveUserSecurityQuestionRequest.builder()
				.from(instance)
				.build();
	}

	/**
	 * Creates a builder for {@link RetrieveUserSecurityQuestionRequest RetrieveUserSecurityQuestionRequest}.
	 * @return A new RetrieveUserSecurityQuestionRequest builder
	 */
	public static RetrieveUserSecurityQuestionRequest.Builder builder() {
		return new RetrieveUserSecurityQuestionRequest.Builder();
	}

	/**
	 * Builds instances of type {@link RetrieveUserSecurityQuestionRequest RetrieveUserSecurityQuestionRequest}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	@NotThreadSafe
	public static final class Builder {
		private @Nullable
		User user;
		private @Nullable
		Locale locale;

		private Builder() {
		}

		/**
		 * Fill a builder with attribute values from the provided {@code RetrieveUserSecurityQuestionRequest} instance.
		 * Regular attribute values will be replaced with those from the given instance.
		 * Absent optional values will not replace present values.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder from(RetrieveUserSecurityQuestionRequest instance) {
			return from((AbstractRetrieveUserSecurityQuestionRequest) instance);
		}

		/**
		 * Copy abstract value type {@code AbstractRetrieveUserSecurityQuestionRequest} instance into builder.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		final Builder from(AbstractRetrieveUserSecurityQuestionRequest instance) {
			Preconditions.checkNotNull(instance, "instance");
			User userValue = instance.getUser();
			if (userValue != null) {
				withUser(userValue);
			}
			Locale localeValue = instance.getLocale();
			if (localeValue != null) {
				withLocale(localeValue);
			}
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractRetrieveUserSecurityQuestionRequest#getUser() user} attribute.
		 * @param user The value for user (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withUser(User user) {
			this.user = user;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractRetrieveUserSecurityQuestionRequest#getLocale() locale} attribute.
		 * @param locale The value for locale (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withLocale(Locale locale) {
			this.locale = locale;
			return this;
		}

		/**
		 * Builds a new {@link RetrieveUserSecurityQuestionRequest RetrieveUserSecurityQuestionRequest}.
		 * @return An immutable instance of RetrieveUserSecurityQuestionRequest
		 * @throws java.lang.IllegalStateException if any required attributes are missing
		 */
		public RetrieveUserSecurityQuestionRequest build() {
			return RetrieveUserSecurityQuestionRequest.validate(new RetrieveUserSecurityQuestionRequest(user, locale));
		}
	}
}
