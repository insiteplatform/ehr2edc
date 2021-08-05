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
 * Immutable implementation of {@link UserAcceptMailContent}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableUserAcceptMailContent.builder()}.
 */
@SuppressWarnings({ "all" })
@ParametersAreNonnullByDefault
@Generated({ "Immutables.generator", "UserAcceptMailContent" })

public final class ImmutableUserAcceptMailContent implements UserAcceptMailContent {
	private final Integer acceptExpireValue;
	private final TimeUnit acceptExpireUnit;
	private final String acceptAcceptUrl;

	private ImmutableUserAcceptMailContent(Integer acceptExpireValue, TimeUnit acceptExpireUnit,
			String acceptAcceptUrl) {
		this.acceptExpireValue = acceptExpireValue;
		this.acceptExpireUnit = acceptExpireUnit;
		this.acceptAcceptUrl = acceptAcceptUrl;
	}

	/**
	 * @return The value of the {@code acceptExpireValue} attribute
	 */
	@Override
	public Integer getAcceptExpireValue() {
		return acceptExpireValue;
	}

	/**
	 * @return The value of the {@code acceptExpireUnit} attribute
	 */
	@Override
	public TimeUnit getAcceptExpireUnit() {
		return acceptExpireUnit;
	}

	/**
	 * @return The value of the {@code acceptAcceptUrl} attribute
	 */
	@Override
	public String getAcceptAcceptUrl() {
		return acceptAcceptUrl;
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UserAcceptMailContent#getAcceptExpireValue() acceptExpireValue} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for acceptExpireValue
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUserAcceptMailContent withAcceptExpireValue(Integer value) {
		if (this.acceptExpireValue.equals(value))
			return this;
		Integer newValue = Preconditions.checkNotNull(value, "acceptExpireValue");
		return new ImmutableUserAcceptMailContent(newValue, this.acceptExpireUnit, this.acceptAcceptUrl);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UserAcceptMailContent#getAcceptExpireUnit() acceptExpireUnit} attribute.
	 * A value equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for acceptExpireUnit
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUserAcceptMailContent withAcceptExpireUnit(TimeUnit value) {
		if (this.acceptExpireUnit == value)
			return this;
		TimeUnit newValue = Preconditions.checkNotNull(value, "acceptExpireUnit");
		return new ImmutableUserAcceptMailContent(this.acceptExpireValue, newValue, this.acceptAcceptUrl);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UserAcceptMailContent#getAcceptAcceptUrl() acceptAcceptUrl} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for acceptAcceptUrl
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUserAcceptMailContent withAcceptAcceptUrl(String value) {
		if (this.acceptAcceptUrl.equals(value))
			return this;
		String newValue = Preconditions.checkNotNull(value, "acceptAcceptUrl");
		return new ImmutableUserAcceptMailContent(this.acceptExpireValue, this.acceptExpireUnit, newValue);
	}

	/**
	 * This instance is equal to all instances of {@code ImmutableUserAcceptMailContent} that have equal attribute values.
	 * @return {@code true} if {@code this} is equal to {@code another} instance
	 */
	@Override
	public boolean equals(@Nullable Object another) {
		if (this == another)
			return true;
		return another instanceof ImmutableUserAcceptMailContent && equalTo((ImmutableUserAcceptMailContent) another);
	}

	private boolean equalTo(ImmutableUserAcceptMailContent another) {
		return acceptExpireValue.equals(another.acceptExpireValue) && acceptExpireUnit.equals(another.acceptExpireUnit)
				&& acceptAcceptUrl.equals(another.acceptAcceptUrl);
	}

	/**
	 * Computes a hash code from attributes: {@code acceptExpireValue}, {@code acceptExpireUnit}, {@code acceptAcceptUrl}.
	 * @return hashCode value
	 */
	@Override
	public int hashCode() {
		int h = 5381;
		h += (h << 5) + acceptExpireValue.hashCode();
		h += (h << 5) + acceptExpireUnit.hashCode();
		h += (h << 5) + acceptAcceptUrl.hashCode();
		return h;
	}

	/**
	 * Prints the immutable value {@code UserAcceptMailContent} with attribute values.
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("UserAcceptMailContent")
				.omitNullValues()
				.add("acceptExpireValue", acceptExpireValue)
				.add("acceptExpireUnit", acceptExpireUnit)
				.add("acceptAcceptUrl", acceptAcceptUrl)
				.toString();
	}

	/**
	 * Creates an immutable copy of a {@link UserAcceptMailContent} value.
	 * Uses accessors to get values to initialize the new immutable instance.
	 * If an instance is already immutable, it is returned as is.
	 * @param instance The instance to copy
	 * @return A copied immutable UserAcceptMailContent instance
	 */
	public static ImmutableUserAcceptMailContent copyOf(UserAcceptMailContent instance) {
		if (instance instanceof ImmutableUserAcceptMailContent) {
			return (ImmutableUserAcceptMailContent) instance;
		}
		return ImmutableUserAcceptMailContent.builder()
				.from(instance)
				.build();
	}

	/**
	 * Creates a builder for {@link ImmutableUserAcceptMailContent ImmutableUserAcceptMailContent}.
	 * @return A new ImmutableUserAcceptMailContent builder
	 */
	public static ImmutableUserAcceptMailContent.Builder builder() {
		return new ImmutableUserAcceptMailContent.Builder();
	}

	/**
	 * Builds instances of type {@link ImmutableUserAcceptMailContent ImmutableUserAcceptMailContent}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	@NotThreadSafe
	public static final class Builder {
		private static final long INIT_BIT_ACCEPT_EXPIRE_VALUE = 0x1L;
		private static final long INIT_BIT_ACCEPT_EXPIRE_UNIT = 0x2L;
		private static final long INIT_BIT_ACCEPT_ACCEPT_URL = 0x4L;
		private long initBits = 0x7L;

		private @Nullable
		Integer acceptExpireValue;
		private @Nullable
		TimeUnit acceptExpireUnit;
		private @Nullable
		String acceptAcceptUrl;

		private Builder() {
		}

		/**
		 * Fill a builder with attribute values from the provided {@code UserAcceptMailContent} instance.
		 * Regular attribute values will be replaced with those from the given instance.
		 * Absent optional values will not replace present values.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder from(UserAcceptMailContent instance) {
			Preconditions.checkNotNull(instance, "instance");
			acceptExpireValue(instance.getAcceptExpireValue());
			acceptExpireUnit(instance.getAcceptExpireUnit());
			acceptAcceptUrl(instance.getAcceptAcceptUrl());
			return this;
		}

		/**
		 * Initializes the value for the {@link UserAcceptMailContent#getAcceptExpireValue() acceptExpireValue} attribute.
		 * @param acceptExpireValue The value for acceptExpireValue
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder acceptExpireValue(Integer acceptExpireValue) {
			this.acceptExpireValue = Preconditions.checkNotNull(acceptExpireValue, "acceptExpireValue");
			initBits &= ~INIT_BIT_ACCEPT_EXPIRE_VALUE;
			return this;
		}

		/**
		 * Initializes the value for the {@link UserAcceptMailContent#getAcceptExpireUnit() acceptExpireUnit} attribute.
		 * @param acceptExpireUnit The value for acceptExpireUnit
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder acceptExpireUnit(TimeUnit acceptExpireUnit) {
			this.acceptExpireUnit = Preconditions.checkNotNull(acceptExpireUnit, "acceptExpireUnit");
			initBits &= ~INIT_BIT_ACCEPT_EXPIRE_UNIT;
			return this;
		}

		/**
		 * Initializes the value for the {@link UserAcceptMailContent#getAcceptAcceptUrl() acceptAcceptUrl} attribute.
		 * @param acceptAcceptUrl The value for acceptAcceptUrl
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder acceptAcceptUrl(String acceptAcceptUrl) {
			this.acceptAcceptUrl = Preconditions.checkNotNull(acceptAcceptUrl, "acceptAcceptUrl");
			initBits &= ~INIT_BIT_ACCEPT_ACCEPT_URL;
			return this;
		}

		/**
		 * Builds a new {@link ImmutableUserAcceptMailContent ImmutableUserAcceptMailContent}.
		 * @return An immutable instance of UserAcceptMailContent
		 * @throws java.lang.IllegalStateException if any required attributes are missing
		 */
		public ImmutableUserAcceptMailContent build() {
			if (initBits != 0) {
				throw new IllegalStateException(formatRequiredAttributesMessage());
			}
			return new ImmutableUserAcceptMailContent(acceptExpireValue, acceptExpireUnit, acceptAcceptUrl);
		}

		private String formatRequiredAttributesMessage() {
			List<String> attributes = Lists.newArrayList();
			if ((initBits & INIT_BIT_ACCEPT_EXPIRE_VALUE) != 0)
				attributes.add("acceptExpireValue");
			if ((initBits & INIT_BIT_ACCEPT_EXPIRE_UNIT) != 0)
				attributes.add("acceptExpireUnit");
			if ((initBits & INIT_BIT_ACCEPT_ACCEPT_URL) != 0)
				attributes.add("acceptAcceptUrl");
			return "Cannot build UserAcceptMailContent, some of required attributes are not set " + attributes;
		}
	}
}
