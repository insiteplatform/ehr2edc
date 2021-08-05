package eu.ehr4cr.workbench.local.service.email.model;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Immutable implementation of {@link UserRecoverMailContent}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableUserRecoverMailContent.builder()}.
 */
@SuppressWarnings({ "all" })
@ParametersAreNonnullByDefault
@Generated({ "Immutables.generator", "UserRecoverMailContent" })

public final class ImmutableUserRecoverMailContent implements UserRecoverMailContent {
	private final Integer recoverExpireValue;
	private final TimeUnit recoverExpireUnit;
	private final String recoverAcceptUrl;

	private ImmutableUserRecoverMailContent(Integer recoverExpireValue, TimeUnit recoverExpireUnit,
			String recoverAcceptUrl) {
		this.recoverExpireValue = recoverExpireValue;
		this.recoverExpireUnit = recoverExpireUnit;
		this.recoverAcceptUrl = recoverAcceptUrl;
	}

	/**
	 * @return The value of the {@code recoverExpireValue} attribute
	 */
	@Override
	public Integer getRecoverExpireValue() {
		return recoverExpireValue;
	}

	/**
	 * @return The value of the {@code recoverExpireUnit} attribute
	 */
	@Override
	public TimeUnit getRecoverExpireUnit() {
		return recoverExpireUnit;
	}

	/**
	 * @return The value of the {@code recoverAcceptUrl} attribute
	 */
	@Override
	public String getRecoverAcceptUrl() {
		return recoverAcceptUrl;
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UserRecoverMailContent#getRecoverExpireValue() recoverExpireValue} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for recoverExpireValue
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUserRecoverMailContent withRecoverExpireValue(Integer value) {
		if (this.recoverExpireValue.equals(value))
			return this;
		Integer newValue = Preconditions.checkNotNull(value, "recoverExpireValue");
		return new ImmutableUserRecoverMailContent(newValue, this.recoverExpireUnit, this.recoverAcceptUrl);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UserRecoverMailContent#getRecoverExpireUnit() recoverExpireUnit} attribute.
	 * A value equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for recoverExpireUnit
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUserRecoverMailContent withRecoverExpireUnit(TimeUnit value) {
		if (this.recoverExpireUnit == value)
			return this;
		TimeUnit newValue = Preconditions.checkNotNull(value, "recoverExpireUnit");
		return new ImmutableUserRecoverMailContent(this.recoverExpireValue, newValue, this.recoverAcceptUrl);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UserRecoverMailContent#getRecoverAcceptUrl() recoverAcceptUrl} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for recoverAcceptUrl
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUserRecoverMailContent withRecoverAcceptUrl(String value) {
		if (this.recoverAcceptUrl.equals(value))
			return this;
		String newValue = Preconditions.checkNotNull(value, "recoverAcceptUrl");
		return new ImmutableUserRecoverMailContent(this.recoverExpireValue, this.recoverExpireUnit, newValue);
	}

	/**
	 * This instance is equal to all instances of {@code ImmutableUserRecoverMailContent} that have equal attribute values.
	 * @return {@code true} if {@code this} is equal to {@code another} instance
	 */
	@Override
	public boolean equals(@Nullable Object another) {
		if (this == another)
			return true;
		return another instanceof ImmutableUserRecoverMailContent && equalTo((ImmutableUserRecoverMailContent) another);
	}

	private boolean equalTo(ImmutableUserRecoverMailContent another) {
		return recoverExpireValue.equals(another.recoverExpireValue) && recoverExpireUnit.equals(
				another.recoverExpireUnit) && recoverAcceptUrl.equals(another.recoverAcceptUrl);
	}

	/**
	 * Computes a hash code from attributes: {@code recoverExpireValue}, {@code recoverExpireUnit}, {@code recoverAcceptUrl}.
	 * @return hashCode value
	 */
	@Override
	public int hashCode() {
		int h = 5381;
		h += (h << 5) + recoverExpireValue.hashCode();
		h += (h << 5) + recoverExpireUnit.hashCode();
		h += (h << 5) + recoverAcceptUrl.hashCode();
		return h;
	}

	/**
	 * Prints the immutable value {@code UserRecoverMailContent} with attribute values.
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("UserRecoverMailContent")
				.omitNullValues()
				.add("recoverExpireValue", recoverExpireValue)
				.add("recoverExpireUnit", recoverExpireUnit)
				.add("recoverAcceptUrl", recoverAcceptUrl)
				.toString();
	}

	/**
	 * Creates an immutable copy of a {@link UserRecoverMailContent} value.
	 * Uses accessors to get values to initialize the new immutable instance.
	 * If an instance is already immutable, it is returned as is.
	 * @param instance The instance to copy
	 * @return A copied immutable UserRecoverMailContent instance
	 */
	public static ImmutableUserRecoverMailContent copyOf(UserRecoverMailContent instance) {
		if (instance instanceof ImmutableUserRecoverMailContent) {
			return (ImmutableUserRecoverMailContent) instance;
		}
		return ImmutableUserRecoverMailContent.builder()
				.from(instance)
				.build();
	}

	/**
	 * Creates a builder for {@link ImmutableUserRecoverMailContent ImmutableUserRecoverMailContent}.
	 * @return A new ImmutableUserRecoverMailContent builder
	 */
	public static ImmutableUserRecoverMailContent.Builder builder() {
		return new ImmutableUserRecoverMailContent.Builder();
	}

	/**
	 * Builds instances of type {@link ImmutableUserRecoverMailContent ImmutableUserRecoverMailContent}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	@NotThreadSafe
	public static final class Builder {
		private static final long INIT_BIT_RECOVER_EXPIRE_VALUE = 0x1L;
		private static final long INIT_BIT_RECOVER_EXPIRE_UNIT = 0x2L;
		private static final long INIT_BIT_RECOVER_ACCEPT_URL = 0x4L;
		private long initBits = 0x7L;

		private @Nullable
		Integer recoverExpireValue;
		private @Nullable
		TimeUnit recoverExpireUnit;
		private @Nullable
		String recoverAcceptUrl;

		private Builder() {
		}

		/**
		 * Fill a builder with attribute values from the provided {@code UserRecoverMailContent} instance.
		 * Regular attribute values will be replaced with those from the given instance.
		 * Absent optional values will not replace present values.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder from(UserRecoverMailContent instance) {
			Preconditions.checkNotNull(instance, "instance");
			recoverExpireValue(instance.getRecoverExpireValue());
			recoverExpireUnit(instance.getRecoverExpireUnit());
			recoverAcceptUrl(instance.getRecoverAcceptUrl());
			return this;
		}

		/**
		 * Initializes the value for the {@link UserRecoverMailContent#getRecoverExpireValue() recoverExpireValue} attribute.
		 * @param recoverExpireValue The value for recoverExpireValue
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder recoverExpireValue(Integer recoverExpireValue) {
			this.recoverExpireValue = Preconditions.checkNotNull(recoverExpireValue, "recoverExpireValue");
			initBits &= ~INIT_BIT_RECOVER_EXPIRE_VALUE;
			return this;
		}

		/**
		 * Initializes the value for the {@link UserRecoverMailContent#getRecoverExpireUnit() recoverExpireUnit} attribute.
		 * @param recoverExpireUnit The value for recoverExpireUnit
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder recoverExpireUnit(TimeUnit recoverExpireUnit) {
			this.recoverExpireUnit = Preconditions.checkNotNull(recoverExpireUnit, "recoverExpireUnit");
			initBits &= ~INIT_BIT_RECOVER_EXPIRE_UNIT;
			return this;
		}

		/**
		 * Initializes the value for the {@link UserRecoverMailContent#getRecoverAcceptUrl() recoverAcceptUrl} attribute.
		 * @param recoverAcceptUrl The value for recoverAcceptUrl
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder recoverAcceptUrl(String recoverAcceptUrl) {
			this.recoverAcceptUrl = Preconditions.checkNotNull(recoverAcceptUrl, "recoverAcceptUrl");
			initBits &= ~INIT_BIT_RECOVER_ACCEPT_URL;
			return this;
		}

		/**
		 * Builds a new {@link ImmutableUserRecoverMailContent ImmutableUserRecoverMailContent}.
		 * @return An immutable instance of UserRecoverMailContent
		 * @throws java.lang.IllegalStateException if any required attributes are missing
		 */
		public ImmutableUserRecoverMailContent build() {
			if (initBits != 0) {
				throw new IllegalStateException(formatRequiredAttributesMessage());
			}
			return new ImmutableUserRecoverMailContent(recoverExpireValue, recoverExpireUnit, recoverAcceptUrl);
		}

		private String formatRequiredAttributesMessage() {
			List<String> attributes = Lists.newArrayList();
			if ((initBits & INIT_BIT_RECOVER_EXPIRE_VALUE) != 0)
				attributes.add("recoverExpireValue");
			if ((initBits & INIT_BIT_RECOVER_EXPIRE_UNIT) != 0)
				attributes.add("recoverExpireUnit");
			if ((initBits & INIT_BIT_RECOVER_ACCEPT_URL) != 0)
				attributes.add("recoverAcceptUrl");
			return "Cannot build UserRecoverMailContent, some of required attributes are not set " + attributes;
		}
	}
}
