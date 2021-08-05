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
 * Immutable implementation of {@link UserInviteMailContent}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableUserInviteMailContent.builder()}.
 */
@SuppressWarnings({ "all" })
@ParametersAreNonnullByDefault
@Generated({ "Immutables.generator", "UserInviteMailContent" })

public final class ImmutableUserInviteMailContent implements UserInviteMailContent {
	private final Integer inviteExpireValue;
	private final TimeUnit inviteExpireUnit;
	private final String inviteAcceptUrl;

	private ImmutableUserInviteMailContent(Integer inviteExpireValue, TimeUnit inviteExpireUnit,
			String inviteAcceptUrl) {
		this.inviteExpireValue = inviteExpireValue;
		this.inviteExpireUnit = inviteExpireUnit;
		this.inviteAcceptUrl = inviteAcceptUrl;
	}

	/**
	 * @return The value of the {@code inviteExpireValue} attribute
	 */
	@Override
	public Integer getInviteExpireValue() {
		return inviteExpireValue;
	}

	/**
	 * @return The value of the {@code inviteExpireUnit} attribute
	 */
	@Override
	public TimeUnit getInviteExpireUnit() {
		return inviteExpireUnit;
	}

	/**
	 * @return The value of the {@code inviteAcceptUrl} attribute
	 */
	@Override
	public String getInviteAcceptUrl() {
		return inviteAcceptUrl;
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UserInviteMailContent#getInviteExpireValue() inviteExpireValue} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for inviteExpireValue
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUserInviteMailContent withInviteExpireValue(Integer value) {
		if (this.inviteExpireValue.equals(value))
			return this;
		Integer newValue = Preconditions.checkNotNull(value, "inviteExpireValue");
		return new ImmutableUserInviteMailContent(newValue, this.inviteExpireUnit, this.inviteAcceptUrl);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UserInviteMailContent#getInviteExpireUnit() inviteExpireUnit} attribute.
	 * A value equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for inviteExpireUnit
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUserInviteMailContent withInviteExpireUnit(TimeUnit value) {
		if (this.inviteExpireUnit == value)
			return this;
		TimeUnit newValue = Preconditions.checkNotNull(value, "inviteExpireUnit");
		return new ImmutableUserInviteMailContent(this.inviteExpireValue, newValue, this.inviteAcceptUrl);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UserInviteMailContent#getInviteAcceptUrl() inviteAcceptUrl} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for inviteAcceptUrl
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUserInviteMailContent withInviteAcceptUrl(String value) {
		if (this.inviteAcceptUrl.equals(value))
			return this;
		String newValue = Preconditions.checkNotNull(value, "inviteAcceptUrl");
		return new ImmutableUserInviteMailContent(this.inviteExpireValue, this.inviteExpireUnit, newValue);
	}

	/**
	 * This instance is equal to all instances of {@code ImmutableUserInviteMailContent} that have equal attribute values.
	 * @return {@code true} if {@code this} is equal to {@code another} instance
	 */
	@Override
	public boolean equals(@Nullable Object another) {
		if (this == another)
			return true;
		return another instanceof ImmutableUserInviteMailContent && equalTo((ImmutableUserInviteMailContent) another);
	}

	private boolean equalTo(ImmutableUserInviteMailContent another) {
		return inviteExpireValue.equals(another.inviteExpireValue) && inviteExpireUnit.equals(another.inviteExpireUnit)
				&& inviteAcceptUrl.equals(another.inviteAcceptUrl);
	}

	/**
	 * Computes a hash code from attributes: {@code inviteExpireValue}, {@code inviteExpireUnit}, {@code inviteAcceptUrl}.
	 * @return hashCode value
	 */
	@Override
	public int hashCode() {
		int h = 5381;
		h += (h << 5) + inviteExpireValue.hashCode();
		h += (h << 5) + inviteExpireUnit.hashCode();
		h += (h << 5) + inviteAcceptUrl.hashCode();
		return h;
	}

	/**
	 * Prints the immutable value {@code UserInviteMailContent} with attribute values.
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("UserInviteMailContent")
				.omitNullValues()
				.add("inviteExpireValue", inviteExpireValue)
				.add("inviteExpireUnit", inviteExpireUnit)
				.add("inviteAcceptUrl", inviteAcceptUrl)
				.toString();
	}

	/**
	 * Creates an immutable copy of a {@link UserInviteMailContent} value.
	 * Uses accessors to get values to initialize the new immutable instance.
	 * If an instance is already immutable, it is returned as is.
	 * @param instance The instance to copy
	 * @return A copied immutable UserInviteMailContent instance
	 */
	public static ImmutableUserInviteMailContent copyOf(UserInviteMailContent instance) {
		if (instance instanceof ImmutableUserInviteMailContent) {
			return (ImmutableUserInviteMailContent) instance;
		}
		return ImmutableUserInviteMailContent.builder()
				.from(instance)
				.build();
	}

	/**
	 * Creates a builder for {@link ImmutableUserInviteMailContent ImmutableUserInviteMailContent}.
	 * @return A new ImmutableUserInviteMailContent builder
	 */
	public static ImmutableUserInviteMailContent.Builder builder() {
		return new ImmutableUserInviteMailContent.Builder();
	}

	/**
	 * Builds instances of type {@link ImmutableUserInviteMailContent ImmutableUserInviteMailContent}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	@NotThreadSafe
	public static final class Builder {
		private static final long INIT_BIT_INVITE_EXPIRE_VALUE = 0x1L;
		private static final long INIT_BIT_INVITE_EXPIRE_UNIT = 0x2L;
		private static final long INIT_BIT_INVITE_ACCEPT_URL = 0x4L;
		private long initBits = 0x7L;

		private @Nullable
		Integer inviteExpireValue;
		private @Nullable
		TimeUnit inviteExpireUnit;
		private @Nullable
		String inviteAcceptUrl;

		private Builder() {
		}

		/**
		 * Fill a builder with attribute values from the provided {@code UserInviteMailContent} instance.
		 * Regular attribute values will be replaced with those from the given instance.
		 * Absent optional values will not replace present values.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder from(UserInviteMailContent instance) {
			Preconditions.checkNotNull(instance, "instance");
			inviteExpireValue(instance.getInviteExpireValue());
			inviteExpireUnit(instance.getInviteExpireUnit());
			inviteAcceptUrl(instance.getInviteAcceptUrl());
			return this;
		}

		/**
		 * Initializes the value for the {@link UserInviteMailContent#getInviteExpireValue() inviteExpireValue} attribute.
		 * @param inviteExpireValue The value for inviteExpireValue
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder inviteExpireValue(Integer inviteExpireValue) {
			this.inviteExpireValue = Preconditions.checkNotNull(inviteExpireValue, "inviteExpireValue");
			initBits &= ~INIT_BIT_INVITE_EXPIRE_VALUE;
			return this;
		}

		/**
		 * Initializes the value for the {@link UserInviteMailContent#getInviteExpireUnit() inviteExpireUnit} attribute.
		 * @param inviteExpireUnit The value for inviteExpireUnit
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder inviteExpireUnit(TimeUnit inviteExpireUnit) {
			this.inviteExpireUnit = Preconditions.checkNotNull(inviteExpireUnit, "inviteExpireUnit");
			initBits &= ~INIT_BIT_INVITE_EXPIRE_UNIT;
			return this;
		}

		/**
		 * Initializes the value for the {@link UserInviteMailContent#getInviteAcceptUrl() inviteAcceptUrl} attribute.
		 * @param inviteAcceptUrl The value for inviteAcceptUrl
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder inviteAcceptUrl(String inviteAcceptUrl) {
			this.inviteAcceptUrl = Preconditions.checkNotNull(inviteAcceptUrl, "inviteAcceptUrl");
			initBits &= ~INIT_BIT_INVITE_ACCEPT_URL;
			return this;
		}

		/**
		 * Builds a new {@link ImmutableUserInviteMailContent ImmutableUserInviteMailContent}.
		 * @return An immutable instance of UserInviteMailContent
		 * @throws java.lang.IllegalStateException if any required attributes are missing
		 */
		public ImmutableUserInviteMailContent build() {
			if (initBits != 0) {
				throw new IllegalStateException(formatRequiredAttributesMessage());
			}
			return new ImmutableUserInviteMailContent(inviteExpireValue, inviteExpireUnit, inviteAcceptUrl);
		}

		private String formatRequiredAttributesMessage() {
			List<String> attributes = Lists.newArrayList();
			if ((initBits & INIT_BIT_INVITE_EXPIRE_VALUE) != 0)
				attributes.add("inviteExpireValue");
			if ((initBits & INIT_BIT_INVITE_EXPIRE_UNIT) != 0)
				attributes.add("inviteExpireUnit");
			if ((initBits & INIT_BIT_INVITE_ACCEPT_URL) != 0)
				attributes.add("inviteAcceptUrl");
			return "Cannot build UserInviteMailContent, some of required attributes are not set " + attributes;
		}
	}
}
