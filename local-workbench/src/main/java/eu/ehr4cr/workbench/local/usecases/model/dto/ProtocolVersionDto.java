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

/**
 * Immutable implementation of {@link AbstractProtocolVersionDto}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ProtocolVersionDto.builder()}.
 */
@SuppressWarnings({ "all" })

@ParametersAreNonnullByDefault
@Generated({ "Immutables.generator", "AbstractProtocolVersionDto" })

public final class ProtocolVersionDto implements AbstractProtocolVersionDto {
	private final Long id;
	private final String version;

	private ProtocolVersionDto(Long id, String version) {
		this.id = id;
		this.version = version;
	}

	/**
	 * @return The value of the {@code id} attribute
	 */
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * @return The value of the {@code version} attribute
	 */
	@Override
	public String getVersion() {
		return version;
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolVersionDto#getId() id} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for id (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolVersionDto withId(Long value) {
		if (Objects.equal(this.id, value))
			return this;
		return validate(new ProtocolVersionDto(value, this.version));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolVersionDto#getVersion() version} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for version (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolVersionDto withVersion(String value) {
		if (Objects.equal(this.version, value))
			return this;
		return validate(new ProtocolVersionDto(this.id, value));
	}

	/**
	 * This instance is equal to all instances of {@code ProtocolVersionDto} that have equal attribute values.
	 * @return {@code true} if {@code this} is equal to {@code another} instance
	 */
	@Override
	public boolean equals(@Nullable Object another) {
		if (this == another)
			return true;
		return another instanceof ProtocolVersionDto && equalTo((ProtocolVersionDto) another);
	}

	private boolean equalTo(ProtocolVersionDto another) {
		return Objects.equal(id, another.id) && Objects.equal(version, another.version);
	}

	/**
	 * Computes a hash code from attributes: {@code id}, {@code version}.
	 * @return hashCode value
	 */
	@Override
	public int hashCode() {
		int h = 5381;
		h += (h << 5) + Objects.hashCode(id);
		h += (h << 5) + Objects.hashCode(version);
		return h;
	}

	/**
	 * Prints the immutable value {@code ProtocolVersionDto} with attribute values.
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("ProtocolVersionDto")
				.omitNullValues()
				.add("id", id)
				.add("version", version)
				.toString();
	}

	private static final Validator validator = Validation.buildDefaultValidatorFactory()
			.getValidator();

	private static ProtocolVersionDto validate(ProtocolVersionDto instance) {
		Set<ConstraintViolation<ProtocolVersionDto>> constraintViolations = validator.validate(instance);
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(constraintViolations);
		}
		return instance;
	}

	/**
	 * Creates an immutable copy of a {@link AbstractProtocolVersionDto} value.
	 * Uses accessors to get values to initialize the new immutable instance.
	 * If an instance is already immutable, it is returned as is.
	 * @param instance The instance to copy
	 * @return A copied immutable ProtocolVersionDto instance
	 */
	static ProtocolVersionDto copyOf(AbstractProtocolVersionDto instance) {
		if (instance instanceof ProtocolVersionDto) {
			return (ProtocolVersionDto) instance;
		}
		return ProtocolVersionDto.builder()
				.from(instance)
				.build();
	}

	/**
	 * Creates a builder for {@link ProtocolVersionDto ProtocolVersionDto}.
	 * @return A new ProtocolVersionDto builder
	 */
	public static ProtocolVersionDto.Builder builder() {
		return new ProtocolVersionDto.Builder();
	}

	/**
	 * Builds instances of type {@link ProtocolVersionDto ProtocolVersionDto}.
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
		String version;

		private Builder() {
		}

		/**
		 * Fill a builder with attribute values from the provided {@code ProtocolVersionDto} instance.
		 * Regular attribute values will be replaced with those from the given instance.
		 * Absent optional values will not replace present values.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder from(ProtocolVersionDto instance) {
			return from((AbstractProtocolVersionDto) instance);
		}

		/**
		 * Copy abstract value type {@code AbstractProtocolVersionDto} instance into builder.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		final Builder from(AbstractProtocolVersionDto instance) {
			Preconditions.checkNotNull(instance, "instance");
			Long idValue = instance.getId();
			if (idValue != null) {
				withId(idValue);
			}
			String versionValue = instance.getVersion();
			if (versionValue != null) {
				withVersion(versionValue);
			}
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolVersionDto#getId() id} attribute.
		 * @param id The value for id (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withId(Long id) {
			this.id = id;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolVersionDto#getVersion() version} attribute.
		 * @param version The value for version (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withVersion(String version) {
			this.version = version;
			return this;
		}

		/**
		 * Builds a new {@link ProtocolVersionDto ProtocolVersionDto}.
		 * @return An immutable instance of ProtocolVersionDto
		 * @throws java.lang.IllegalStateException if any required attributes are missing
		 */
		public ProtocolVersionDto build() {
			return ProtocolVersionDto.validate(new ProtocolVersionDto(id, version));
		}
	}
}
