package eu.ehr4cr.workbench.local.service.email.model;

import java.util.List;

import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Immutable implementation of {@link UserRegistrationMailContent}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableUserRegistrationMailContent.builder()}.
 */
@SuppressWarnings({ "all" })
@ParametersAreNonnullByDefault
@Generated({ "Immutables.generator", "UserRegistrationMailContent" })

public final class ImmutableUserRegistrationMailContent implements UserRegistrationMailContent {
	private final String username;
	private final String acceptUrl;

	private ImmutableUserRegistrationMailContent(String username, String acceptUrl) {
		this.username = username;
		this.acceptUrl = acceptUrl;
	}

	/**
	 * @return The value of the {@code username} attribute
	 */
	@Override
	public String getUsername() {
		return username;
	}

	/**
	 * @return The value of the {@code acceptUrl} attribute
	 */
	@Override
	public String getAcceptUrl() {
		return acceptUrl;
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UserRegistrationMailContent#getUsername() username} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for username
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUserRegistrationMailContent withUsername(String value) {
		if (this.username.equals(value))
			return this;
		String newValue = Preconditions.checkNotNull(value, "username");
		return new ImmutableUserRegistrationMailContent(newValue, this.acceptUrl);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UserRegistrationMailContent#getAcceptUrl() acceptUrl} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for acceptUrl
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUserRegistrationMailContent withAcceptUrl(String value) {
		if (this.acceptUrl.equals(value))
			return this;
		String newValue = Preconditions.checkNotNull(value, "acceptUrl");
		return new ImmutableUserRegistrationMailContent(this.username, newValue);
	}

	/**
	 * This instance is equal to all instances of {@code ImmutableUserRegistrationMailContent} that have equal attribute values.
	 * @return {@code true} if {@code this} is equal to {@code another} instance
	 */
	@Override
	public boolean equals(@Nullable Object another) {
		if (this == another)
			return true;
		return another instanceof ImmutableUserRegistrationMailContent && equalTo(
				(ImmutableUserRegistrationMailContent) another);
	}

	private boolean equalTo(ImmutableUserRegistrationMailContent another) {
		return username.equals(another.username) && acceptUrl.equals(another.acceptUrl);
	}

	/**
	 * Computes a hash code from attributes: {@code username}, {@code acceptUrl}.
	 * @return hashCode value
	 */
	@Override
	public int hashCode() {
		int h = 5381;
		h += (h << 5) + username.hashCode();
		h += (h << 5) + acceptUrl.hashCode();
		return h;
	}

	/**
	 * Prints the immutable value {@code UserRegistrationMailContent} with attribute values.
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("UserRegistrationMailContent")
				.omitNullValues()
				.add("username", username)
				.add("acceptUrl", acceptUrl)
				.toString();
	}

	/**
	 * Creates an immutable copy of a {@link UserRegistrationMailContent} value.
	 * Uses accessors to get values to initialize the new immutable instance.
	 * If an instance is already immutable, it is returned as is.
	 * @param instance The instance to copy
	 * @return A copied immutable UserRegistrationMailContent instance
	 */
	public static ImmutableUserRegistrationMailContent copyOf(UserRegistrationMailContent instance) {
		if (instance instanceof ImmutableUserRegistrationMailContent) {
			return (ImmutableUserRegistrationMailContent) instance;
		}
		return ImmutableUserRegistrationMailContent.builder()
				.from(instance)
				.build();
	}

	/**
	 * Creates a builder for {@link ImmutableUserRegistrationMailContent ImmutableUserRegistrationMailContent}.
	 * @return A new ImmutableUserRegistrationMailContent builder
	 */
	public static ImmutableUserRegistrationMailContent.Builder builder() {
		return new ImmutableUserRegistrationMailContent.Builder();
	}

	/**
	 * Builds instances of type {@link ImmutableUserRegistrationMailContent ImmutableUserRegistrationMailContent}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	@NotThreadSafe
	public static final class Builder {
		private static final long INIT_BIT_USERNAME = 0x1L;
		private static final long INIT_BIT_ACCEPT_URL = 0x2L;
		private long initBits = 0x3L;

		private @Nullable
		String username;
		private @Nullable
		String acceptUrl;

		private Builder() {
		}

		/**
		 * Fill a builder with attribute values from the provided {@code UserRegistrationMailContent} instance.
		 * Regular attribute values will be replaced with those from the given instance.
		 * Absent optional values will not replace present values.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder from(UserRegistrationMailContent instance) {
			Preconditions.checkNotNull(instance, "instance");
			username(instance.getUsername());
			acceptUrl(instance.getAcceptUrl());
			return this;
		}

		/**
		 * Initializes the value for the {@link UserRegistrationMailContent#getUsername() username} attribute.
		 * @param username The value for username
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder username(String username) {
			this.username = Preconditions.checkNotNull(username, "username");
			initBits &= ~INIT_BIT_USERNAME;
			return this;
		}

		/**
		 * Initializes the value for the {@link UserRegistrationMailContent#getAcceptUrl() acceptUrl} attribute.
		 * @param acceptUrl The value for acceptUrl
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder acceptUrl(String acceptUrl) {
			this.acceptUrl = Preconditions.checkNotNull(acceptUrl, "acceptUrl");
			initBits &= ~INIT_BIT_ACCEPT_URL;
			return this;
		}

		/**
		 * Builds a new {@link ImmutableUserRegistrationMailContent ImmutableUserRegistrationMailContent}.
		 * @return An immutable instance of UserRegistrationMailContent
		 * @throws java.lang.IllegalStateException if any required attributes are missing
		 */
		public ImmutableUserRegistrationMailContent build() {
			if (initBits != 0) {
				throw new IllegalStateException(formatRequiredAttributesMessage());
			}
			return new ImmutableUserRegistrationMailContent(username, acceptUrl);
		}

		private String formatRequiredAttributesMessage() {
			List<String> attributes = Lists.newArrayList();
			if ((initBits & INIT_BIT_USERNAME) != 0)
				attributes.add("username");
			if ((initBits & INIT_BIT_ACCEPT_URL) != 0)
				attributes.add("acceptUrl");
			return "Cannot build UserRegistrationMailContent, some of required attributes are not set " + attributes;
		}
	}
}
