package eu.ehr4cr.workbench.local.usecases.model.dto;

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
import com.google.common.primitives.Booleans;

import eu.ehr4cr.workbench.local.model.security.User;

/**
 * Immutable implementation of {@link AbstractInvestigatorDto}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code InvestigatorDto.builder()}.
 */
@SuppressWarnings({ "all" })

@ParametersAreNonnullByDefault
@Generated({ "Immutables.generator", "AbstractInvestigatorDto" })

public final class InvestigatorDto implements AbstractInvestigatorDto {
	private final Long id;
	private final User user;
	private final boolean occupied;

	private InvestigatorDto(Long id, User user, boolean occupied) {
		this.id = id;
		this.user = user;
		this.occupied = occupied;
	}

	/**
	 * @return The value of the {@code id} attribute
	 */
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * @return The value of the {@code user} attribute
	 */
	@Override
	public User getUser() {
		return user;
	}

	/**
	 * @return The value of the {@code occupied} attribute
	 */
	@Override
	public boolean isOccupied() {
		return occupied;
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractInvestigatorDto#getId() id} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for id (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final InvestigatorDto withId(Long value) {
		if (Objects.equal(this.id, value))
			return this;
		return validate(new InvestigatorDto(value, this.user, this.occupied));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractInvestigatorDto#getUser() user} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for user (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final InvestigatorDto withUser(User value) {
		if (this.user == value)
			return this;
		return validate(new InvestigatorDto(this.id, value, this.occupied));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractInvestigatorDto#isOccupied() occupied} attribute.
	 * A value equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for occupied
	 * @return A modified copy of the {@code this} object
	 */
	public final InvestigatorDto withOccupied(boolean value) {
		if (this.occupied == value)
			return this;
		return validate(new InvestigatorDto(this.id, this.user, value));
	}

	/**
	 * This instance is equal to all instances of {@code InvestigatorDto} that have equal attribute values.
	 * @return {@code true} if {@code this} is equal to {@code another} instance
	 */
	@Override
	public boolean equals(@Nullable Object another) {
		if (this == another)
			return true;
		return another instanceof InvestigatorDto && equalTo((InvestigatorDto) another);
	}

	private boolean equalTo(InvestigatorDto another) {
		return Objects.equal(id, another.id) && Objects.equal(user, another.user) && occupied == another.occupied;
	}

	/**
	 * Computes a hash code from attributes: {@code id}, {@code user}, {@code occupied}.
	 * @return hashCode value
	 */
	@Override
	public int hashCode() {
		int h = 5381;
		h += (h << 5) + Objects.hashCode(id);
		h += (h << 5) + Objects.hashCode(user);
		h += (h << 5) + Booleans.hashCode(occupied);
		return h;
	}

	/**
	 * Prints the immutable value {@code InvestigatorDto} with attribute values.
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("InvestigatorDto")
				.omitNullValues()
				.add("id", id)
				.add("user", user)
				.add("occupied", occupied)
				.toString();
	}

	private static final Validator validator = Validation.buildDefaultValidatorFactory()
			.getValidator();

	private static InvestigatorDto validate(InvestigatorDto instance) {
		Set<ConstraintViolation<InvestigatorDto>> constraintViolations = validator.validate(instance);
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(constraintViolations);
		}
		return instance;
	}

	/**
	 * Creates an immutable copy of a {@link AbstractInvestigatorDto} value.
	 * Uses accessors to get values to initialize the new immutable instance.
	 * If an instance is already immutable, it is returned as is.
	 * @param instance The instance to copy
	 * @return A copied immutable InvestigatorDto instance
	 */
	static InvestigatorDto copyOf(AbstractInvestigatorDto instance) {
		if (instance instanceof InvestigatorDto) {
			return (InvestigatorDto) instance;
		}
		return InvestigatorDto.builder()
				.from(instance)
				.build();
	}

	/**
	 * Creates a builder for {@link InvestigatorDto InvestigatorDto}.
	 * @return A new InvestigatorDto builder
	 */
	public static InvestigatorDto.Builder builder() {
		return new InvestigatorDto.Builder();
	}

	/**
	 * Builds instances of type {@link InvestigatorDto InvestigatorDto}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	@NotThreadSafe
	public static final class Builder {
		private @Nullable
		Long id;
		private @Nullable
		User user;
		private boolean occupied;

		private Builder() {
		}

		/**
		 * Fill a builder with attribute values from the provided {@code InvestigatorDto} instance.
		 * Regular attribute values will be replaced with those from the given instance.
		 * Absent optional values will not replace present values.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder from(InvestigatorDto instance) {
			return from((AbstractInvestigatorDto) instance);
		}

		/**
		 * Copy abstract value type {@code AbstractInvestigatorDto} instance into builder.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		final Builder from(AbstractInvestigatorDto instance) {
			Preconditions.checkNotNull(instance, "instance");
			Long idValue = instance.getId();
			if (idValue != null) {
				withId(idValue);
			}
			User userValue = instance.getUser();
			if (userValue != null) {
				withUser(userValue);
			}
			withOccupied(instance.isOccupied());
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractInvestigatorDto#getId() id} attribute.
		 * @param id The value for id (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withId(Long id) {
			this.id = id;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractInvestigatorDto#getUser() user} attribute.
		 * @param user The value for user (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withUser(User user) {
			this.user = user;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractInvestigatorDto#isOccupied() occupied} attribute.
		 * @param occupied The value for occupied
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withOccupied(boolean occupied) {
			this.occupied = occupied;
			return this;
		}

		/**
		 * Builds a new {@link InvestigatorDto InvestigatorDto}.
		 * @return An immutable instance of InvestigatorDto
		 * @throws java.lang.IllegalStateException if any required attributes are missing
		 */
		public InvestigatorDto build() {
			return InvestigatorDto.validate(new InvestigatorDto(id, user, occupied));
		}
	}
}
