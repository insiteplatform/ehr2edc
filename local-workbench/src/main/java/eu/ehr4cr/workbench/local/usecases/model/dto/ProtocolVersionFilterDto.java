package eu.ehr4cr.workbench.local.usecases.model.dto;

import java.util.Date;
import java.util.Set;

import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.NotThreadSafe;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import eu.ehr4cr.workbench.local.model.clinicalStudy.ExternalUser;
import eu.ehr4cr.workbench.local.model.security.User;

/**
 * Immutable implementation of {@link AbstractProtocolVersionFilterDto}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ProtocolVersionFilterDto.builder()}.
 */
@SuppressWarnings({ "all" })

@ParametersAreNonnullByDefault
@Generated({ "Immutables.generator", "AbstractProtocolVersionFilterDto" })

public final class ProtocolVersionFilterDto implements AbstractProtocolVersionFilterDto {
	private final Long id;
	private final String version;
	private final String jsonContent;
	private final Boolean archived;
	private final Boolean fromSponsor;
	private final Date creationDate;
	private final User creator;
	private final ExternalUser sponsor;
	private final JsonNode formattedJsonContent;

	private ProtocolVersionFilterDto(Long id, String version, String jsonContent, Boolean archived, Boolean fromSponsor,
			Date creationDate, User creator, ExternalUser sponsor, JsonNode formattedJsonContent) {
		this.id = id;
		this.version = version;
		this.jsonContent = jsonContent;
		this.archived = archived;
		this.fromSponsor = fromSponsor;
		this.creationDate = creationDate;
		this.creator = creator;
		this.sponsor = sponsor;
		this.formattedJsonContent = formattedJsonContent;
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
	 * @return The value of the {@code jsonContent} attribute
	 */
	@Override
	public String getJsonContent() {
		return jsonContent;
	}

	/**
	 * @return The value of the {@code archived} attribute
	 */
	@Override
	public Boolean isArchived() {
		return archived;
	}

	/**
	 * @return The value of the {@code fromSponsor} attribute
	 */
	@Override
	public Boolean isFromSponsor() {
		return fromSponsor;
	}

	/**
	 * @return The value of the {@code creationDate} attribute
	 */
	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @return The value of the {@code creator} attribute
	 */
	@Override
	public User getCreator() {
		return creator;
	}

	/**
	 * @return The value of the {@code sponsor} attribute
	 */
	@Override
	public ExternalUser getSponsor() {
		return sponsor;
	}

	/**
	 * @return The value of the {@code formattedJsonContent} attribute
	 */
	@Override
	public JsonNode getFormattedJsonContent() {
		return formattedJsonContent;
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolVersionFilterDto#getId() id} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for id (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolVersionFilterDto withId(Long value) {
		if (Objects.equal(this.id, value))
			return this;
		return validate(
				new ProtocolVersionFilterDto(value, this.version, this.jsonContent, this.archived, this.fromSponsor,
						this.creationDate, this.creator, this.sponsor, this.formattedJsonContent));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolVersionFilterDto#getVersion() version} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for version (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolVersionFilterDto withVersion(String value) {
		if (Objects.equal(this.version, value))
			return this;
		return validate(new ProtocolVersionFilterDto(this.id, value, this.jsonContent, this.archived, this.fromSponsor,
				this.creationDate, this.creator, this.sponsor, this.formattedJsonContent));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolVersionFilterDto#getJsonContent() jsonContent} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for jsonContent (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolVersionFilterDto withJsonContent(String value) {
		if (Objects.equal(this.jsonContent, value))
			return this;
		return validate(new ProtocolVersionFilterDto(this.id, this.version, value, this.archived, this.fromSponsor,
				this.creationDate, this.creator, this.sponsor, this.formattedJsonContent));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolVersionFilterDto#isArchived() archived} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for archived (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolVersionFilterDto withArchived(Boolean value) {
		if (Objects.equal(this.archived, value))
			return this;
		return validate(new ProtocolVersionFilterDto(this.id, this.version, this.jsonContent, value, this.fromSponsor,
				this.creationDate, this.creator, this.sponsor, this.formattedJsonContent));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolVersionFilterDto#isFromSponsor() fromSponsor} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for fromSponsor (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolVersionFilterDto withFromSponsor(Boolean value) {
		if (Objects.equal(this.fromSponsor, value))
			return this;
		return validate(new ProtocolVersionFilterDto(this.id, this.version, this.jsonContent, this.archived, value,
				this.creationDate, this.creator, this.sponsor, this.formattedJsonContent));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolVersionFilterDto#getCreationDate() creationDate} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for creationDate (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolVersionFilterDto withCreationDate(Date value) {
		if (this.creationDate == value)
			return this;
		return validate(
				new ProtocolVersionFilterDto(this.id, this.version, this.jsonContent, this.archived, this.fromSponsor,
						value, this.creator, this.sponsor, this.formattedJsonContent));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolVersionFilterDto#getCreator() creator} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for creator (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolVersionFilterDto withCreator(User value) {
		if (this.creator == value)
			return this;
		return validate(
				new ProtocolVersionFilterDto(this.id, this.version, this.jsonContent, this.archived, this.fromSponsor,
						this.creationDate, value, this.sponsor, this.formattedJsonContent));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolVersionFilterDto#getSponsor() sponsor} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for sponsor (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolVersionFilterDto withSponsor(ExternalUser value) {
		if (this.sponsor == value)
			return this;
		return validate(
				new ProtocolVersionFilterDto(this.id, this.version, this.jsonContent, this.archived, this.fromSponsor,
						this.creationDate, this.creator, value, this.formattedJsonContent));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractProtocolVersionFilterDto#getFormattedJsonContent() formattedJsonContent} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for formattedJsonContent (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ProtocolVersionFilterDto withFormattedJsonContent(JsonNode value) {
		if (this.formattedJsonContent == value)
			return this;
		return validate(
				new ProtocolVersionFilterDto(this.id, this.version, this.jsonContent, this.archived, this.fromSponsor,
						this.creationDate, this.creator, this.sponsor, value));
	}

	/**
	 * This instance is equal to all instances of {@code ProtocolVersionFilterDto} that have equal attribute values.
	 * @return {@code true} if {@code this} is equal to {@code another} instance
	 */
	@Override
	public boolean equals(@Nullable Object another) {
		if (this == another)
			return true;
		return another instanceof ProtocolVersionFilterDto && equalTo((ProtocolVersionFilterDto) another);
	}

	private boolean equalTo(ProtocolVersionFilterDto another) {
		return Objects.equal(id, another.id) && Objects.equal(version, another.version) && Objects.equal(jsonContent,
				another.jsonContent) && Objects.equal(archived, another.archived) && Objects.equal(fromSponsor,
				another.fromSponsor) && Objects.equal(creationDate, another.creationDate) && Objects.equal(creator,
				another.creator) && Objects.equal(sponsor, another.sponsor) && Objects.equal(formattedJsonContent,
				another.formattedJsonContent);
	}

	/**
	 * Computes a hash code from attributes: {@code id}, {@code version}, {@code jsonContent}, {@code archived}, {@code fromSponsor}, {@code creationDate}, {@code creator}, {@code sponsor}, {@code formattedJsonContent}.
	 * @return hashCode value
	 */
	@Override
	public int hashCode() {
		int h = 5381;
		h += (h << 5) + Objects.hashCode(id);
		h += (h << 5) + Objects.hashCode(version);
		h += (h << 5) + Objects.hashCode(jsonContent);
		h += (h << 5) + Objects.hashCode(archived);
		h += (h << 5) + Objects.hashCode(fromSponsor);
		h += (h << 5) + Objects.hashCode(creationDate);
		h += (h << 5) + Objects.hashCode(creator);
		h += (h << 5) + Objects.hashCode(sponsor);
		h += (h << 5) + Objects.hashCode(formattedJsonContent);
		return h;
	}

	/**
	 * Prints the immutable value {@code ProtocolVersionFilterDto} with attribute values.
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("ProtocolVersionFilterDto")
				.omitNullValues()
				.add("id", id)
				.add("version", version)
				.add("jsonContent", jsonContent)
				.add("archived", archived)
				.add("fromSponsor", fromSponsor)
				.add("creationDate", creationDate)
				.add("creator", creator)
				.add("sponsor", sponsor)
				.add("formattedJsonContent", formattedJsonContent)
				.toString();
	}

	private static final Validator validator = Validation.buildDefaultValidatorFactory()
			.getValidator();

	private static ProtocolVersionFilterDto validate(ProtocolVersionFilterDto instance) {
		Set<ConstraintViolation<ProtocolVersionFilterDto>> constraintViolations = validator.validate(instance);
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(constraintViolations);
		}
		return instance;
	}

	/**
	 * Creates an immutable copy of a {@link AbstractProtocolVersionFilterDto} value.
	 * Uses accessors to get values to initialize the new immutable instance.
	 * If an instance is already immutable, it is returned as is.
	 * @param instance The instance to copy
	 * @return A copied immutable ProtocolVersionFilterDto instance
	 */
	static ProtocolVersionFilterDto copyOf(AbstractProtocolVersionFilterDto instance) {
		if (instance instanceof ProtocolVersionFilterDto) {
			return (ProtocolVersionFilterDto) instance;
		}
		return ProtocolVersionFilterDto.builder()
				.from(instance)
				.build();
	}

	/**
	 * Creates a builder for {@link ProtocolVersionFilterDto ProtocolVersionFilterDto}.
	 * @return A new ProtocolVersionFilterDto builder
	 */
	public static ProtocolVersionFilterDto.Builder builder() {
		return new ProtocolVersionFilterDto.Builder();
	}

	/**
	 * Builds instances of type {@link ProtocolVersionFilterDto ProtocolVersionFilterDto}.
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
		private @Nullable
		String jsonContent;
		private @Nullable
		Boolean archived;
		private @Nullable
		Boolean fromSponsor;
		private @Nullable
		Date creationDate;
		private @Nullable
		User creator;
		private @Nullable
		ExternalUser sponsor;
		private @Nullable
		JsonNode formattedJsonContent;

		private Builder() {
		}

		/**
		 * Fill a builder with attribute values from the provided {@code ProtocolVersionFilterDto} instance.
		 * Regular attribute values will be replaced with those from the given instance.
		 * Absent optional values will not replace present values.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder from(ProtocolVersionFilterDto instance) {
			return from((AbstractProtocolVersionFilterDto) instance);
		}

		/**
		 * Copy abstract value type {@code AbstractProtocolVersionFilterDto} instance into builder.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		final Builder from(AbstractProtocolVersionFilterDto instance) {
			Preconditions.checkNotNull(instance, "instance");
			Long idValue = instance.getId();
			if (idValue != null) {
				withId(idValue);
			}
			String versionValue = instance.getVersion();
			if (versionValue != null) {
				withVersion(versionValue);
			}
			String jsonContentValue = instance.getJsonContent();
			if (jsonContentValue != null) {
				withJsonContent(jsonContentValue);
			}
			Boolean archivedValue = instance.isArchived();
			if (archivedValue != null) {
				withArchived(archivedValue);
			}
			Boolean fromSponsorValue = instance.isFromSponsor();
			if (fromSponsorValue != null) {
				withFromSponsor(fromSponsorValue);
			}
			Date creationDateValue = instance.getCreationDate();
			if (creationDateValue != null) {
				withCreationDate(creationDateValue);
			}
			User creatorValue = instance.getCreator();
			if (creatorValue != null) {
				withCreator(creatorValue);
			}
			ExternalUser sponsorValue = instance.getSponsor();
			if (sponsorValue != null) {
				withSponsor(sponsorValue);
			}
			JsonNode formattedJsonContentValue = instance.getFormattedJsonContent();
			if (formattedJsonContentValue != null) {
				withFormattedJsonContent(formattedJsonContentValue);
			}
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolVersionFilterDto#getId() id} attribute.
		 * @param id The value for id (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withId(Long id) {
			this.id = id;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolVersionFilterDto#getVersion() version} attribute.
		 * @param version The value for version (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withVersion(String version) {
			this.version = version;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolVersionFilterDto#getJsonContent() jsonContent} attribute.
		 * @param jsonContent The value for jsonContent (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withJsonContent(String jsonContent) {
			this.jsonContent = jsonContent;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolVersionFilterDto#isArchived() archived} attribute.
		 * @param archived The value for archived (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withArchived(Boolean archived) {
			this.archived = archived;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolVersionFilterDto#isFromSponsor() fromSponsor} attribute.
		 * @param fromSponsor The value for fromSponsor (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withFromSponsor(Boolean fromSponsor) {
			this.fromSponsor = fromSponsor;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolVersionFilterDto#getCreationDate() creationDate} attribute.
		 * @param creationDate The value for creationDate (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withCreationDate(Date creationDate) {
			this.creationDate = creationDate;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolVersionFilterDto#getCreator() creator} attribute.
		 * @param creator The value for creator (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withCreator(User creator) {
			this.creator = creator;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolVersionFilterDto#getSponsor() sponsor} attribute.
		 * @param sponsor The value for sponsor (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withSponsor(ExternalUser sponsor) {
			this.sponsor = sponsor;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractProtocolVersionFilterDto#getFormattedJsonContent() formattedJsonContent} attribute.
		 * @param formattedJsonContent The value for formattedJsonContent (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withFormattedJsonContent(JsonNode formattedJsonContent) {
			this.formattedJsonContent = formattedJsonContent;
			return this;
		}

		/**
		 * Builds a new {@link ProtocolVersionFilterDto ProtocolVersionFilterDto}.
		 * @return An immutable instance of ProtocolVersionFilterDto
		 * @throws java.lang.IllegalStateException if any required attributes are missing
		 */
		public ProtocolVersionFilterDto build() {
			return ProtocolVersionFilterDto.validate(
					new ProtocolVersionFilterDto(id, version, jsonContent, archived, fromSponsor, creationDate, creator,
							sponsor, formattedJsonContent));
		}
	}
}
