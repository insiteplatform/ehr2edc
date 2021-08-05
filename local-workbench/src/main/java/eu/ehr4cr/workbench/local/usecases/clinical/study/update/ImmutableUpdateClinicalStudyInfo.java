package eu.ehr4cr.workbench.local.usecases.clinical.study.update;

import java.util.Date;
import java.util.List;

import javax.annotation.Generated;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;

import eu.ehr4cr.workbench.local.annotation.Nullable;

/**
 * Immutable implementation of {@link UpdateClinicalStudyInfo}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableUpdateClinicalStudyInfo.builder()}.
 */
@SuppressWarnings({ "all" })
@ParametersAreNonnullByDefault
@Generated({ "Immutables.generator", "UpdateClinicalStudyInfo" })

public final class ImmutableUpdateClinicalStudyInfo implements UpdateClinicalStudyInfo {
	private final String publicId;
	private final String name;
	private final @Nullable
	String description;
	private final String author;
	private final String sponsor;
	private final Date modificationDate;
	private final long goal;

	private ImmutableUpdateClinicalStudyInfo(String publicId, String name, @Nullable String description, String author,
			String sponsor, Date modificationDate, long goal) {
		this.publicId = publicId;
		this.name = name;
		this.description = description;
		this.author = author;
		this.sponsor = sponsor;
		this.modificationDate = modificationDate;
		this.goal = goal;
	}

	/**
	 * @return The value of the {@code publicId} attribute
	 */
	@Override
	public String getPublicId() {
		return publicId;
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
	public @Nullable
	String getDescription() {
		return description;
	}

	/**
	 * @return The value of the {@code author} attribute
	 */
	@Override
	public String getAuthor() {
		return author;
	}

	/**
	 * @return The value of the {@code sponsor} attribute
	 */
	@Override
	public String getSponsor() {
		return sponsor;
	}

	/**
	 * @return The value of the {@code modificationDate} attribute
	 */
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}

	/**
	 * @return The value of the {@code goal} attribute
	 */
	@Override
	public long getGoal() {
		return goal;
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UpdateClinicalStudyInfo#getPublicId() publicId} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for publicId
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUpdateClinicalStudyInfo withPublicId(String value) {
		if (this.publicId.equals(value))
			return this;
		String newValue = Preconditions.checkNotNull(value, "publicId");
		return new ImmutableUpdateClinicalStudyInfo(newValue, this.name, this.description, this.author, this.sponsor,
				this.modificationDate, this.goal);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UpdateClinicalStudyInfo#getName() name} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for name
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUpdateClinicalStudyInfo withName(String value) {
		if (this.name.equals(value))
			return this;
		String newValue = Preconditions.checkNotNull(value, "name");
		return new ImmutableUpdateClinicalStudyInfo(this.publicId, newValue, this.description, this.author,
				this.sponsor, this.modificationDate, this.goal);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UpdateClinicalStudyInfo#getDescription() description} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for description (can be {@code null})
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUpdateClinicalStudyInfo withDescription(@Nullable String value) {
		if (Objects.equal(this.description, value))
			return this;
		return new ImmutableUpdateClinicalStudyInfo(this.publicId, this.name, value, this.author, this.sponsor,
				this.modificationDate, this.goal);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UpdateClinicalStudyInfo#getAuthor() author} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for author
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUpdateClinicalStudyInfo withAuthor(String value) {
		if (this.author.equals(value))
			return this;
		String newValue = Preconditions.checkNotNull(value, "author");
		return new ImmutableUpdateClinicalStudyInfo(this.publicId, this.name, this.description, newValue, this.sponsor,
				this.modificationDate, this.goal);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UpdateClinicalStudyInfo#getSponsor() sponsor} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for sponsor
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUpdateClinicalStudyInfo withSponsor(String value) {
		if (this.sponsor.equals(value))
			return this;
		String newValue = Preconditions.checkNotNull(value, "sponsor");
		return new ImmutableUpdateClinicalStudyInfo(this.publicId, this.name, this.description, this.author, newValue,
				this.modificationDate, this.goal);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UpdateClinicalStudyInfo#getModificationDate() modificationDate} attribute.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for modificationDate
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUpdateClinicalStudyInfo withModificationDate(Date value) {
		if (this.modificationDate == value)
			return this;
		Date newValue = Preconditions.checkNotNull(value, "modificationDate");
		return new ImmutableUpdateClinicalStudyInfo(this.publicId, this.name, this.description, this.author,
				this.sponsor, newValue, this.goal);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UpdateClinicalStudyInfo#getGoal() goal} attribute.
	 * A value equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for goal
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUpdateClinicalStudyInfo withGoal(long value) {
		if (this.goal == value)
			return this;
		return new ImmutableUpdateClinicalStudyInfo(this.publicId, this.name, this.description, this.author,
				this.sponsor, this.modificationDate, value);
	}

	/**
	 * This instance is equal to all instances of {@code ImmutableUpdateClinicalStudyInfo} that have equal attribute values.
	 * @return {@code true} if {@code this} is equal to {@code another} instance
	 */
	@Override
	public boolean equals(@javax.annotation.Nullable Object another) {
		if (this == another)
			return true;
		return another instanceof ImmutableUpdateClinicalStudyInfo && equalTo(
				(ImmutableUpdateClinicalStudyInfo) another);
	}

	private boolean equalTo(ImmutableUpdateClinicalStudyInfo another) {
		return publicId.equals(another.publicId) && name.equals(another.name) && Objects.equal(description,
				another.description) && author.equals(another.author) && sponsor.equals(another.sponsor)
				&& modificationDate.equals(another.modificationDate) && goal == another.goal;
	}

	/**
	 * Computes a hash code from attributes: {@code publicId}, {@code name}, {@code description}, {@code author}, {@code sponsor}, {@code modificationDate}, {@code goal}.
	 * @return hashCode value
	 */
	@Override
	public int hashCode() {
		int h = 5381;
		h += (h << 5) + publicId.hashCode();
		h += (h << 5) + name.hashCode();
		h += (h << 5) + Objects.hashCode(description);
		h += (h << 5) + author.hashCode();
		h += (h << 5) + sponsor.hashCode();
		h += (h << 5) + modificationDate.hashCode();
		h += (h << 5) + Longs.hashCode(goal);
		return h;
	}

	/**
	 * Prints the immutable value {@code UpdateClinicalStudyInfo} with attribute values.
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("UpdateClinicalStudyInfo")
				.omitNullValues()
				.add("publicId", publicId)
				.add("name", name)
				.add("description", description)
				.add("author", author)
				.add("sponsor", sponsor)
				.add("modificationDate", modificationDate)
				.add("goal", goal)
				.toString();
	}

	/**
	 * Creates an immutable copy of a {@link UpdateClinicalStudyInfo} value.
	 * Uses accessors to get values to initialize the new immutable instance.
	 * If an instance is already immutable, it is returned as is.
	 * @param instance The instance to copy
	 * @return A copied immutable UpdateClinicalStudyInfo instance
	 */
	public static ImmutableUpdateClinicalStudyInfo copyOf(UpdateClinicalStudyInfo instance) {
		if (instance instanceof ImmutableUpdateClinicalStudyInfo) {
			return (ImmutableUpdateClinicalStudyInfo) instance;
		}
		return ImmutableUpdateClinicalStudyInfo.builder()
				.from(instance)
				.build();
	}

	/**
	 * Creates a builder for {@link ImmutableUpdateClinicalStudyInfo ImmutableUpdateClinicalStudyInfo}.
	 * @return A new ImmutableUpdateClinicalStudyInfo builder
	 */
	public static ImmutableUpdateClinicalStudyInfo.Builder builder() {
		return new ImmutableUpdateClinicalStudyInfo.Builder();
	}

	/**
	 * Builds instances of type {@link ImmutableUpdateClinicalStudyInfo ImmutableUpdateClinicalStudyInfo}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	@NotThreadSafe
	public static final class Builder {
		private static final long INIT_BIT_PUBLIC_ID = 0x1L;
		private static final long INIT_BIT_NAME = 0x2L;
		private static final long INIT_BIT_AUTHOR = 0x4L;
		private static final long INIT_BIT_SPONSOR = 0x8L;
		private static final long INIT_BIT_MODIFICATION_DATE = 0x10L;
		private static final long INIT_BIT_GOAL = 0x20L;
		private long initBits = 0x3fL;

		private @javax.annotation.Nullable
		String publicId;
		private @javax.annotation.Nullable
		String name;
		private @javax.annotation.Nullable
		String description;
		private @javax.annotation.Nullable
		String author;
		private @javax.annotation.Nullable
		String sponsor;
		private @javax.annotation.Nullable
		Date modificationDate;
		private long goal;

		private Builder() {
		}

		/**
		 * Fill a builder with attribute values from the provided {@code UpdateClinicalStudyInfo} instance.
		 * Regular attribute values will be replaced with those from the given instance.
		 * Absent optional values will not replace present values.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder from(UpdateClinicalStudyInfo instance) {
			Preconditions.checkNotNull(instance, "instance");
			publicId(instance.getPublicId());
			name(instance.getName());
			@Nullable
			String descriptionValue = instance.getDescription();
			if (descriptionValue != null) {
				description(descriptionValue);
			}
			author(instance.getAuthor());
			sponsor(instance.getSponsor());
			modificationDate(instance.getModificationDate());
			goal(instance.getGoal());
			return this;
		}

		/**
		 * Initializes the value for the {@link UpdateClinicalStudyInfo#getPublicId() publicId} attribute.
		 * @param publicId The value for publicId
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder publicId(String publicId) {
			this.publicId = Preconditions.checkNotNull(publicId, "publicId");
			initBits &= ~INIT_BIT_PUBLIC_ID;
			return this;
		}

		/**
		 * Initializes the value for the {@link UpdateClinicalStudyInfo#getName() name} attribute.
		 * @param name The value for name
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder name(String name) {
			this.name = Preconditions.checkNotNull(name, "name");
			initBits &= ~INIT_BIT_NAME;
			return this;
		}

		/**
		 * Initializes the value for the {@link UpdateClinicalStudyInfo#getDescription() description} attribute.
		 * @param description The value for description (can be {@code null})
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder description(@Nullable String description) {
			this.description = description;
			return this;
		}

		/**
		 * Initializes the value for the {@link UpdateClinicalStudyInfo#getAuthor() author} attribute.
		 * @param author The value for author
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder author(String author) {
			this.author = Preconditions.checkNotNull(author, "author");
			initBits &= ~INIT_BIT_AUTHOR;
			return this;
		}

		/**
		 * Initializes the value for the {@link UpdateClinicalStudyInfo#getSponsor() sponsor} attribute.
		 * @param sponsor The value for sponsor
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder sponsor(String sponsor) {
			this.sponsor = Preconditions.checkNotNull(sponsor, "sponsor");
			initBits &= ~INIT_BIT_SPONSOR;
			return this;
		}

		/**
		 * Initializes the value for the {@link UpdateClinicalStudyInfo#getModificationDate() modificationDate} attribute.
		 * @param modificationDate The value for modificationDate
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder modificationDate(Date modificationDate) {
			this.modificationDate = Preconditions.checkNotNull(modificationDate, "modificationDate");
			initBits &= ~INIT_BIT_MODIFICATION_DATE;
			return this;
		}

		/**
		 * Initializes the value for the {@link UpdateClinicalStudyInfo#getGoal() goal} attribute.
		 * @param goal The value for goal
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder goal(long goal) {
			this.goal = goal;
			initBits &= ~INIT_BIT_GOAL;
			return this;
		}

		/**
		 * Builds a new {@link ImmutableUpdateClinicalStudyInfo ImmutableUpdateClinicalStudyInfo}.
		 * @return An immutable instance of UpdateClinicalStudyInfo
		 * @throws java.lang.IllegalStateException if any required attributes are missing
		 */
		public ImmutableUpdateClinicalStudyInfo build() {
			if (initBits != 0) {
				throw new IllegalStateException(formatRequiredAttributesMessage());
			}
			return new ImmutableUpdateClinicalStudyInfo(publicId, name, description, author, sponsor, modificationDate,
					goal);
		}

		private String formatRequiredAttributesMessage() {
			List<String> attributes = Lists.newArrayList();
			if ((initBits & INIT_BIT_PUBLIC_ID) != 0)
				attributes.add("publicId");
			if ((initBits & INIT_BIT_NAME) != 0)
				attributes.add("name");
			if ((initBits & INIT_BIT_AUTHOR) != 0)
				attributes.add("author");
			if ((initBits & INIT_BIT_SPONSOR) != 0)
				attributes.add("sponsor");
			if ((initBits & INIT_BIT_MODIFICATION_DATE) != 0)
				attributes.add("modificationDate");
			if ((initBits & INIT_BIT_GOAL) != 0)
				attributes.add("goal");
			return "Cannot build UpdateClinicalStudyInfo, some of required attributes are not set " + attributes;
		}
	}
}
