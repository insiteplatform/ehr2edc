package eu.ehr4cr.workbench.local.usecases.model.dto;

import java.util.Date;
import java.util.Optional;
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

import eu.ehr4cr.workbench.local.model.clinicalStudy.ClinicalStudyState;
import eu.ehr4cr.workbench.local.model.clinicalStudy.ExternalUser;

/**
 * Immutable implementation of {@link AbstractClinicalStudyDto}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ClinicalStudyDto.builder()}.
 */
@SuppressWarnings({ "all" })

@ParametersAreNonnullByDefault
@Generated({ "Immutables.generator", "AbstractClinicalStudyDto" })

public final class ClinicalStudyDto implements AbstractClinicalStudyDto {
	private final Long id;
	private final String name;
	private final String description;
	private final Boolean archived;
	private final ClinicalStudyState state;
	private final ExternalUser sponsor;
	private final Long goal;
	private final Date deadline;
	private final Date lastUpdated;
	private final Long reached;
	private final Date creationDate;
	private final String externalId;
	private final boolean detailsViewPossible;
	private final @Nullable
	Long daysUntilDeadline;

	private ClinicalStudyDto(Long id, String name, String description, Boolean archived, ClinicalStudyState state,
			ExternalUser sponsor, Long goal, Date deadline, Date lastUpdated, Long reached, Date creationDate,
			String externalId, boolean detailsViewPossible, @Nullable Long daysUntilDeadline) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.archived = archived;
		this.state = state;
		this.sponsor = sponsor;
		this.goal = goal;
		this.deadline = deadline;
		this.lastUpdated = lastUpdated;
		this.reached = reached;
		this.creationDate = creationDate;
		this.externalId = externalId;
		this.detailsViewPossible = detailsViewPossible;
		this.daysUntilDeadline = daysUntilDeadline;
	}

	/**
	 * @return The value of the {@code id} attribute
	 */
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * @return The value of the {@code name} attribute
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @return The value of the {@code description} attribute
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * @return The value of the {@code archived} attribute
	 */
	@Override
	public Boolean getArchived() {
		return archived;
	}

	/**
	 * @return The value of the {@code state} attribute
	 */
	@Override
	public ClinicalStudyState getState() {
		return state;
	}

	/**
	 * @return The value of the {@code sponsor} attribute
	 */
	@Override
	public ExternalUser getSponsor() {
		return sponsor;
	}

	/**
	 * @return The value of the {@code goal} attribute
	 */
	@Override
	public Long getGoal() {
		return goal;
	}

	/**
	 * @return The value of the {@code deadline} attribute
	 */
	@Override
	public Date getDeadline() {
		return deadline;
	}

	/**
	 * @return The value of the {@code lastUpdated} attribute
	 */
	@Override
	public Date getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * @return The value of the {@code reached} attribute
	 */
	@Override
	public Long getReached() {
		return reached;
	}

	/**
	 * @return The value of the {@code creationDate} attribute
	 */
	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @return The value of the {@code externalId} attribute
	 */
	@Override
	public String getExternalId() {
		return externalId;
	}

	/**
	 * @return The value of the {@code detailsViewPossible} attribute
	 */
	@Override
	public boolean isDetailsViewPossible() {
		return detailsViewPossible;
	}

	/**
	 * @return The value of the {@code daysUntilDeadline} attribute
	 */
	@Override
	public Optional<Long> getDaysUntilDeadline() {
		return Optional.ofNullable(daysUntilDeadline);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractClinicalStudyDto#getId() id} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for id (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ClinicalStudyDto withId(Long value) {
		if (Objects.equal(this.id, value))
			return this;
		return validate(
				new ClinicalStudyDto(value, this.name, this.description, this.archived, this.state, this.sponsor,
						this.goal, this.deadline, this.lastUpdated, this.reached, this.creationDate, this.externalId,
						this.detailsViewPossible, this.daysUntilDeadline));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractClinicalStudyDto#getName() name} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for name (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ClinicalStudyDto withName(String value) {
		if (Objects.equal(this.name, value))
			return this;
		return validate(new ClinicalStudyDto(this.id, value, this.description, this.archived, this.state, this.sponsor,
				this.goal, this.deadline, this.lastUpdated, this.reached, this.creationDate, this.externalId,
				this.detailsViewPossible, this.daysUntilDeadline));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractClinicalStudyDto#getDescription() description} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for description (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ClinicalStudyDto withDescription(String value) {
		if (Objects.equal(this.description, value))
			return this;
		return validate(
				new ClinicalStudyDto(this.id, this.name, value, this.archived, this.state, this.sponsor, this.goal,
						this.deadline, this.lastUpdated, this.reached, this.creationDate, this.externalId,
						this.detailsViewPossible, this.daysUntilDeadline));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractClinicalStudyDto#getArchived() archived} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for archived (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ClinicalStudyDto withArchived(Boolean value) {
		if (Objects.equal(this.archived, value))
			return this;
		return validate(
				new ClinicalStudyDto(this.id, this.name, this.description, value, this.state, this.sponsor, this.goal,
						this.deadline, this.lastUpdated, this.reached, this.creationDate, this.externalId,
						this.detailsViewPossible, this.daysUntilDeadline));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractClinicalStudyDto#getState() state} attribute.
	 * A value equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for state (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ClinicalStudyDto withState(ClinicalStudyState value) {
		if (this.state == value)
			return this;
		return validate(new ClinicalStudyDto(this.id, this.name, this.description, this.archived, value, this.sponsor,
				this.goal, this.deadline, this.lastUpdated, this.reached, this.creationDate, this.externalId,
				this.detailsViewPossible, this.daysUntilDeadline));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractClinicalStudyDto#getSponsor() sponsor} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for sponsor (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ClinicalStudyDto withSponsor(ExternalUser value) {
		if (this.sponsor == value)
			return this;
		return validate(
				new ClinicalStudyDto(this.id, this.name, this.description, this.archived, this.state, value, this.goal,
						this.deadline, this.lastUpdated, this.reached, this.creationDate, this.externalId,
						this.detailsViewPossible, this.daysUntilDeadline));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractClinicalStudyDto#getGoal() goal} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for goal (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ClinicalStudyDto withGoal(Long value) {
		if (Objects.equal(this.goal, value))
			return this;
		return validate(
				new ClinicalStudyDto(this.id, this.name, this.description, this.archived, this.state, this.sponsor,
						value, this.deadline, this.lastUpdated, this.reached, this.creationDate, this.externalId,
						this.detailsViewPossible, this.daysUntilDeadline));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractClinicalStudyDto#getDeadline() deadline} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for deadline (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ClinicalStudyDto withDeadline(Date value) {
		if (this.deadline == value)
			return this;
		return validate(
				new ClinicalStudyDto(this.id, this.name, this.description, this.archived, this.state, this.sponsor,
						this.goal, value, this.lastUpdated, this.reached, this.creationDate, this.externalId,
						this.detailsViewPossible, this.daysUntilDeadline));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractClinicalStudyDto#getLastUpdated() lastUpdated} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for lastUpdated (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ClinicalStudyDto withLastUpdated(Date value) {
		if (this.lastUpdated == value)
			return this;
		return validate(
				new ClinicalStudyDto(this.id, this.name, this.description, this.archived, this.state, this.sponsor,
						this.goal, this.deadline, value, this.reached, this.creationDate, this.externalId,
						this.detailsViewPossible, this.daysUntilDeadline));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractClinicalStudyDto#getReached() reached} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for reached (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ClinicalStudyDto withReached(Long value) {
		if (Objects.equal(this.reached, value))
			return this;
		return validate(
				new ClinicalStudyDto(this.id, this.name, this.description, this.archived, this.state, this.sponsor,
						this.goal, this.deadline, this.lastUpdated, value, this.creationDate, this.externalId,
						this.detailsViewPossible, this.daysUntilDeadline));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractClinicalStudyDto#getCreationDate() creationDate} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for creationDate (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ClinicalStudyDto withCreationDate(Date value) {
		if (this.creationDate == value)
			return this;
		return validate(
				new ClinicalStudyDto(this.id, this.name, this.description, this.archived, this.state, this.sponsor,
						this.goal, this.deadline, this.lastUpdated, this.reached, value, this.externalId,
						this.detailsViewPossible, this.daysUntilDeadline));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractClinicalStudyDto#getExternalId() externalId} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for externalId (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ClinicalStudyDto withExternalId(String value) {
		if (Objects.equal(this.externalId, value))
			return this;
		return validate(
				new ClinicalStudyDto(this.id, this.name, this.description, this.archived, this.state, this.sponsor,
						this.goal, this.deadline, this.lastUpdated, this.reached, this.creationDate, value,
						this.detailsViewPossible, this.daysUntilDeadline));
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link AbstractClinicalStudyDto#isDetailsViewPossible() detailsViewPossible} attribute.
	 * A value equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for detailsViewPossible
	 * @return A modified copy of the {@code this} object
	 */
	public final ClinicalStudyDto withDetailsViewPossible(boolean value) {
		if (this.detailsViewPossible == value)
			return this;
		return validate(
				new ClinicalStudyDto(this.id, this.name, this.description, this.archived, this.state, this.sponsor,
						this.goal, this.deadline, this.lastUpdated, this.reached, this.creationDate, this.externalId,
						value, this.daysUntilDeadline));
	}

	/**
	 * Copy the current immutable object by setting a <i>present</i> value for the optional {@link AbstractClinicalStudyDto#getDaysUntilDeadline() daysUntilDeadline} attribute.
	 * @param value The value for daysUntilDeadline
	 * @return A modified copy of {@code this} object
	 */
	public final ClinicalStudyDto withDaysUntilDeadline(long value) {
		@Nullable
		Long newValue = value;
		if (Objects.equal(this.daysUntilDeadline, newValue))
			return this;
		return validate(
				new ClinicalStudyDto(this.id, this.name, this.description, this.archived, this.state, this.sponsor,
						this.goal, this.deadline, this.lastUpdated, this.reached, this.creationDate, this.externalId,
						this.detailsViewPossible, newValue));
	}

	/**
	 * Copy the current immutable object by setting an optional value for the {@link AbstractClinicalStudyDto#getDaysUntilDeadline() daysUntilDeadline} attribute.
	 * An equality check is used on inner nullable value to prevent copying of the same value by returning {@code this}.
	 * @param optional A value for daysUntilDeadline
	 * @return A modified copy of {@code this} object
	 */
	public final ClinicalStudyDto withDaysUntilDeadline(Optional<Long> optional) {
		@Nullable
		Long value = optional.orElse(null);
		if (Objects.equal(this.daysUntilDeadline, value))
			return this;
		return validate(
				new ClinicalStudyDto(this.id, this.name, this.description, this.archived, this.state, this.sponsor,
						this.goal, this.deadline, this.lastUpdated, this.reached, this.creationDate, this.externalId,
						this.detailsViewPossible, value));
	}

	/**
	 * This instance is equal to all instances of {@code ClinicalStudyDto} that have equal attribute values.
	 * @return {@code true} if {@code this} is equal to {@code another} instance
	 */
	@Override
	public boolean equals(@Nullable Object another) {
		if (this == another)
			return true;
		return another instanceof ClinicalStudyDto && equalTo((ClinicalStudyDto) another);
	}

	private boolean equalTo(ClinicalStudyDto another) {
		return Objects.equal(id, another.id) && Objects.equal(name, another.name) && Objects.equal(description,
				another.description) && Objects.equal(archived, another.archived) && Objects.equal(state, another.state)
				&& Objects.equal(sponsor, another.sponsor) && Objects.equal(goal, another.goal) && Objects.equal(
				deadline, another.deadline) && Objects.equal(lastUpdated, another.lastUpdated) && Objects.equal(reached,
				another.reached) && Objects.equal(creationDate, another.creationDate) && Objects.equal(externalId,
				another.externalId) && detailsViewPossible == another.detailsViewPossible && Objects.equal(
				daysUntilDeadline, another.daysUntilDeadline);
	}

	/**
	 * Computes a hash code from attributes: {@code id}, {@code name}, {@code description}, {@code archived}, {@code state}, {@code sponsor}, {@code goal}, {@code deadline}, {@code lastUpdated}, {@code reached}, {@code creationDate}, {@code externalId}, {@code detailsViewPossible}, {@code daysUntilDeadline}.
	 * @return hashCode value
	 */
	@Override
	public int hashCode() {
		int h = 5381;
		h += (h << 5) + Objects.hashCode(id);
		h += (h << 5) + Objects.hashCode(name);
		h += (h << 5) + Objects.hashCode(description);
		h += (h << 5) + Objects.hashCode(archived);
		h += (h << 5) + Objects.hashCode(state);
		h += (h << 5) + Objects.hashCode(sponsor);
		h += (h << 5) + Objects.hashCode(goal);
		h += (h << 5) + Objects.hashCode(deadline);
		h += (h << 5) + Objects.hashCode(lastUpdated);
		h += (h << 5) + Objects.hashCode(reached);
		h += (h << 5) + Objects.hashCode(creationDate);
		h += (h << 5) + Objects.hashCode(externalId);
		h += (h << 5) + Booleans.hashCode(detailsViewPossible);
		h += (h << 5) + Objects.hashCode(daysUntilDeadline);
		return h;
	}

	/**
	 * Prints the immutable value {@code ClinicalStudyDto} with attribute values.
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("ClinicalStudyDto")
				.omitNullValues()
				.add("id", id)
				.add("name", name)
				.add("description", description)
				.add("archived", archived)
				.add("state", state)
				.add("sponsor", sponsor)
				.add("goal", goal)
				.add("deadline", deadline)
				.add("lastUpdated", lastUpdated)
				.add("reached", reached)
				.add("creationDate", creationDate)
				.add("externalId", externalId)
				.add("detailsViewPossible", detailsViewPossible)
				.add("daysUntilDeadline", daysUntilDeadline)
				.toString();
	}

	private static final Validator validator = Validation.buildDefaultValidatorFactory()
			.getValidator();

	private static ClinicalStudyDto validate(ClinicalStudyDto instance) {
		Set<ConstraintViolation<ClinicalStudyDto>> constraintViolations = validator.validate(instance);
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(constraintViolations);
		}
		return instance;
	}

	/**
	 * Creates an immutable copy of a {@link AbstractClinicalStudyDto} value.
	 * Uses accessors to get values to initialize the new immutable instance.
	 * If an instance is already immutable, it is returned as is.
	 * @param instance The instance to copy
	 * @return A copied immutable ClinicalStudyDto instance
	 */
	static ClinicalStudyDto copyOf(AbstractClinicalStudyDto instance) {
		if (instance instanceof ClinicalStudyDto) {
			return (ClinicalStudyDto) instance;
		}
		return ClinicalStudyDto.builder()
				.from(instance)
				.build();
	}

	/**
	 * Creates a builder for {@link ClinicalStudyDto ClinicalStudyDto}.
	 * @return A new ClinicalStudyDto builder
	 */
	public static ClinicalStudyDto.Builder builder() {
		return new ClinicalStudyDto.Builder();
	}

	/**
	 * Builds instances of type {@link ClinicalStudyDto ClinicalStudyDto}.
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
		String name;
		private @Nullable
		String description;
		private @Nullable
		Boolean archived;
		private @Nullable
		ClinicalStudyState state;
		private @Nullable
		ExternalUser sponsor;
		private @Nullable
		Long goal;
		private @Nullable
		Date deadline;
		private @Nullable
		Date lastUpdated;
		private @Nullable
		Long reached;
		private @Nullable
		Date creationDate;
		private @Nullable
		String externalId;
		private boolean detailsViewPossible;
		private @Nullable
		Long daysUntilDeadline;

		private Builder() {
		}

		/**
		 * Fill a builder with attribute values from the provided {@code ClinicalStudyDto} instance.
		 * Regular attribute values will be replaced with those from the given instance.
		 * Absent optional values will not replace present values.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder from(ClinicalStudyDto instance) {
			return from((AbstractClinicalStudyDto) instance);
		}

		/**
		 * Copy abstract value type {@code AbstractClinicalStudyDto} instance into builder.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		final Builder from(AbstractClinicalStudyDto instance) {
			Preconditions.checkNotNull(instance, "instance");
			Long idValue = instance.getId();
			if (idValue != null) {
				withId(idValue);
			}
			String nameValue = instance.getName();
			if (nameValue != null) {
				withName(nameValue);
			}
			String descriptionValue = instance.getDescription();
			if (descriptionValue != null) {
				withDescription(descriptionValue);
			}
			Boolean archivedValue = instance.getArchived();
			if (archivedValue != null) {
				withArchived(archivedValue);
			}
			ClinicalStudyState stateValue = instance.getState();
			if (stateValue != null) {
				withState(stateValue);
			}
			ExternalUser sponsorValue = instance.getSponsor();
			if (sponsorValue != null) {
				withSponsor(sponsorValue);
			}
			Long goalValue = instance.getGoal();
			if (goalValue != null) {
				withGoal(goalValue);
			}
			Date deadlineValue = instance.getDeadline();
			if (deadlineValue != null) {
				withDeadline(deadlineValue);
			}
			Date lastUpdatedValue = instance.getLastUpdated();
			if (lastUpdatedValue != null) {
				withLastUpdated(lastUpdatedValue);
			}
			Long reachedValue = instance.getReached();
			if (reachedValue != null) {
				withReached(reachedValue);
			}
			Date creationDateValue = instance.getCreationDate();
			if (creationDateValue != null) {
				withCreationDate(creationDateValue);
			}
			String externalIdValue = instance.getExternalId();
			if (externalIdValue != null) {
				withExternalId(externalIdValue);
			}
			withDetailsViewPossible(instance.isDetailsViewPossible());
			Optional<Long> daysUntilDeadlineOptional = instance.getDaysUntilDeadline();
			if (daysUntilDeadlineOptional.isPresent()) {
				withDaysUntilDeadline(daysUntilDeadlineOptional);
			}
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractClinicalStudyDto#getId() id} attribute.
		 * @param id The value for id (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withId(Long id) {
			this.id = id;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractClinicalStudyDto#getName() name} attribute.
		 * @param name The value for name (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withName(String name) {
			this.name = name;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractClinicalStudyDto#getDescription() description} attribute.
		 * @param description The value for description (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractClinicalStudyDto#getArchived() archived} attribute.
		 * @param archived The value for archived (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withArchived(Boolean archived) {
			this.archived = archived;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractClinicalStudyDto#getState() state} attribute.
		 * @param state The value for state (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withState(ClinicalStudyState state) {
			this.state = state;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractClinicalStudyDto#getSponsor() sponsor} attribute.
		 * @param sponsor The value for sponsor (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withSponsor(ExternalUser sponsor) {
			this.sponsor = sponsor;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractClinicalStudyDto#getGoal() goal} attribute.
		 * @param goal The value for goal (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withGoal(Long goal) {
			this.goal = goal;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractClinicalStudyDto#getDeadline() deadline} attribute.
		 * @param deadline The value for deadline (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withDeadline(Date deadline) {
			this.deadline = deadline;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractClinicalStudyDto#getLastUpdated() lastUpdated} attribute.
		 * @param lastUpdated The value for lastUpdated (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withLastUpdated(Date lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractClinicalStudyDto#getReached() reached} attribute.
		 * @param reached The value for reached (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withReached(Long reached) {
			this.reached = reached;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractClinicalStudyDto#getCreationDate() creationDate} attribute.
		 * @param creationDate The value for creationDate (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withCreationDate(Date creationDate) {
			this.creationDate = creationDate;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractClinicalStudyDto#getExternalId() externalId} attribute.
		 * @param externalId The value for externalId (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withExternalId(String externalId) {
			this.externalId = externalId;
			return this;
		}

		/**
		 * Initializes the value for the {@link AbstractClinicalStudyDto#isDetailsViewPossible() detailsViewPossible} attribute.
		 * @param detailsViewPossible The value for detailsViewPossible
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withDetailsViewPossible(boolean detailsViewPossible) {
			this.detailsViewPossible = detailsViewPossible;
			return this;
		}

		/**
		 * Initializes the optional value {@link AbstractClinicalStudyDto#getDaysUntilDeadline() daysUntilDeadline} to daysUntilDeadline.
		 * @param daysUntilDeadline The value for daysUntilDeadline
		 * @return {@code this} builder for chained invocation
		 */
		public final Builder withDaysUntilDeadline(long daysUntilDeadline) {
			this.daysUntilDeadline = daysUntilDeadline;
			return this;
		}

		/**
		 * Initializes the optional value {@link AbstractClinicalStudyDto#getDaysUntilDeadline() daysUntilDeadline} to daysUntilDeadline.
		 * @param daysUntilDeadline The value for daysUntilDeadline
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder withDaysUntilDeadline(Optional<Long> daysUntilDeadline) {
			this.daysUntilDeadline = daysUntilDeadline.orElse(null);
			return this;
		}

		/**
		 * Builds a new {@link ClinicalStudyDto ClinicalStudyDto}.
		 * @return An immutable instance of ClinicalStudyDto
		 * @throws java.lang.IllegalStateException if any required attributes are missing
		 */
		public ClinicalStudyDto build() {
			return ClinicalStudyDto.validate(
					new ClinicalStudyDto(id, name, description, archived, state, sponsor, goal, deadline, lastUpdated,
							reached, creationDate, externalId, detailsViewPossible, daysUntilDeadline));
		}
	}
}
