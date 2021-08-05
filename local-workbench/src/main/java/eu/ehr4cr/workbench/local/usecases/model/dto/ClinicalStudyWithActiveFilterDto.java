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
 * Immutable implementation of {@link AbstractClinicalStudyWithActiveFilterDto}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ClinicalStudyWithActiveFilterDto.builder()}.
 */
@SuppressWarnings({ "all" })

@ParametersAreNonnullByDefault
@Generated({ "Immutables.generator", "AbstractClinicalStudyWithActiveFilterDto" })

public final class ClinicalStudyWithActiveFilterDto implements AbstractClinicalStudyWithActiveFilterDto {
	private final ClinicalStudyDto study;
	private final ProtocolVersionDto version;
	private final ProtocolVersionFilterDto activeFilter;

	private ClinicalStudyWithActiveFilterDto(ClinicalStudyDto study, ProtocolVersionDto version,
			ProtocolVersionFilterDto activeFilter) {
		this.study = study;
		this.version = version;
		this.activeFilter = activeFilter;
	}

	/**
	 * @return The value of the {@code study} attribute
	 */
	@Override
	public ClinicalStudyDto getStudy() {
		return study;
	}

	/**
	 * @return The value of the {@code version} attribute
	 */
	@Override
	public ProtocolVersionDto getVersion() {
		return version;
	}

	/**
	 * @return The value of the {@code activeFilter} attribute
	 */
	@Override
	public ProtocolVersionFilterDto getActiveFilter() {
		return activeFilter;
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractClinicalStudyWithActiveFilterDto#getStudy() study} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for study (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ClinicalStudyWithActiveFilterDto withStudy(ClinicalStudyDto value) {
		if (this.study == value)
			return this;
		return validate(new ClinicalStudyWithActiveFilterDto(value, this.version, this.activeFilter));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractClinicalStudyWithActiveFilterDto#getVersion() version} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for version (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ClinicalStudyWithActiveFilterDto withVersion(ProtocolVersionDto value) {
		if (this.version == value)
			return this;
		return validate(new ClinicalStudyWithActiveFilterDto(this.study, value, this.activeFilter));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractClinicalStudyWithActiveFilterDto#getActiveFilter() activeFilter} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for activeFilter (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ClinicalStudyWithActiveFilterDto withActiveFilter(ProtocolVersionFilterDto value) {
		if (this.activeFilter == value)
			return this;
		return validate(new ClinicalStudyWithActiveFilterDto(this.study, this.version, value));
	}

	/**
	 * This instance is equal to all instances of {@code ClinicalStudyWithActiveFilterDto} that have equal attribute values.
	 * @return {@code true} if {@code this} is equal to {@code another} instance
	 */
	@Override
	public boolean equals(@Nullable Object another) {
		if (this == another)
			return true;
		return another instanceof ClinicalStudyWithActiveFilterDto && equalTo(
				(ClinicalStudyWithActiveFilterDto) another);
	}

	private boolean equalTo(ClinicalStudyWithActiveFilterDto another) {
		return Objects.equal(study, another.study) && Objects.equal(version, another.version) && Objects.equal(
				activeFilter, another.activeFilter);
	}

	/**
	 * Computes a hash code from attributes: {@code study}, {@code version}, {@code activeFilter}.
	 * @return hashCode value
	 */
	@Override
	public int hashCode() {
		int h = 5381;
		h += (h << 5) + Objects.hashCode(study);
		h += (h << 5) + Objects.hashCode(version);
		h += (h << 5) + Objects.hashCode(activeFilter);
		return h;
	}

	/**
	 * Prints the immutable value {@code ClinicalStudyWithActiveFilterDto} with attribute values.
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("ClinicalStudyWithActiveFilterDto")
				.omitNullValues()
				.add("study", study)
				.add("version", version)
				.add("activeFilter", activeFilter)
				.toString();
	}

	private static final Validator validator = Validation.buildDefaultValidatorFactory()
			.getValidator();

	private static ClinicalStudyWithActiveFilterDto validate(ClinicalStudyWithActiveFilterDto instance) {
		Set<ConstraintViolation<ClinicalStudyWithActiveFilterDto>> constraintViolations = validator.validate(instance);
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(constraintViolations);
		}
		return instance;
	}

	/**
	 * Creates an immutable copy of a {@link AbstractClinicalStudyWithActiveFilterDto} value.
	 * Uses accessors to get values to initialize the new immutable instance.
	 * If an instance is already immutable, it is returned as is.
	 * @param instance The instance to copy
	 * @return A copied immutable ClinicalStudyWithActiveFilterDto instance
	 */
	static ClinicalStudyWithActiveFilterDto copyOf(AbstractClinicalStudyWithActiveFilterDto instance) {
		if (instance instanceof ClinicalStudyWithActiveFilterDto) {
			return (ClinicalStudyWithActiveFilterDto) instance;
		}
		return ClinicalStudyWithActiveFilterDto.builder()
				.from(instance)
				.build();
	}

	/**
	 * Creates a builder for {@link ClinicalStudyWithActiveFilterDto ClinicalStudyWithActiveFilterDto}.
	 * @return A new ClinicalStudyWithActiveFilterDto builder
	 */
	public static ClinicalStudyWithActiveFilterDto.Builder builder() {
		return new ClinicalStudyWithActiveFilterDto.Builder();
	}

	/**
	 * Builds instances of type {@link ClinicalStudyWithActiveFilterDto ClinicalStudyWithActiveFilterDto}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	@NotThreadSafe
	public static final class Builder {
		private @Nullable
		ClinicalStudyDto study;
		private @Nullable
		ProtocolVersionDto version;
		private @Nullable
		ProtocolVersionFilterDto activeFilter;

		private Builder() {
		}

		/**
		 * Fill a builder with attribute values from the provided {@code ClinicalStudyWithActiveFilterDto} instance.
		 * Regular attribute values will be replaced with those from the given instance.
		 * Absent optional values will not replace present values.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder from(ClinicalStudyWithActiveFilterDto instance) {
			return from((AbstractClinicalStudyWithActiveFilterDto) instance);
		}

		/**
		 * Copy abstract value type {@code AbstractClinicalStudyWithActiveFilterDto} instance into builder.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		final Builder from(AbstractClinicalStudyWithActiveFilterDto instance) {
			Preconditions.checkNotNull(instance, "instance");
			ClinicalStudyDto studyValue = instance.getStudy();
			if (studyValue != null) {
				withStudy(studyValue);
			}
			ProtocolVersionDto versionValue = instance.getVersion();
			if (versionValue != null) {
				withVersion(versionValue);
			}
			ProtocolVersionFilterDto activeFilterValue = instance.getActiveFilter();
			if (activeFilterValue != null) {
				withActiveFilter(activeFilterValue);
			}
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractClinicalStudyWithActiveFilterDto#getStudy() study} attribute.
		 * @param study The value for study (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withStudy(ClinicalStudyDto study) {
			this.study = study;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractClinicalStudyWithActiveFilterDto#getVersion() version} attribute.
		 * @param version The value for version (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withVersion(ProtocolVersionDto version) {
			this.version = version;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractClinicalStudyWithActiveFilterDto#getActiveFilter() activeFilter} attribute.
		 * @param activeFilter The value for activeFilter (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withActiveFilter(ProtocolVersionFilterDto activeFilter) {
			this.activeFilter = activeFilter;
			return this;
		}

		/**
		 * Builds a new {@link ClinicalStudyWithActiveFilterDto ClinicalStudyWithActiveFilterDto}.
		 * @return An immutable instance of ClinicalStudyWithActiveFilterDto
		 * @throws java.lang.IllegalStateException if any required attributes are missing
		 */
		public ClinicalStudyWithActiveFilterDto build() {
			return ClinicalStudyWithActiveFilterDto.validate(
					new ClinicalStudyWithActiveFilterDto(study, version, activeFilter));
		}
	}
}
