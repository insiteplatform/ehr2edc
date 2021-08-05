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
 * Immutable implementation of {@link ClinicalStudyInvitationMailContent}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableClinicalStudyInvitationMailContent.builder()}.
 */
@SuppressWarnings({ "all" })
@ParametersAreNonnullByDefault
@Generated({ "Immutables.generator", "ClinicalStudyInvitationMailContent" })

public final class ImmutableClinicalStudyInvitationMailContent implements ClinicalStudyInvitationMailContent {
	private final String participationUrl;

	private ImmutableClinicalStudyInvitationMailContent(String participationUrl) {
		this.participationUrl = participationUrl;
	}

	/**
	 * @return The value of the {@code participationUrl} attribute
	 */
	@Override
	public String getParticipationUrl() {
		return participationUrl;
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link ClinicalStudyInvitationMailContent#getParticipationUrl() participationUrl} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for participationUrl
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableClinicalStudyInvitationMailContent withParticipationUrl(String value) {
		if (this.participationUrl.equals(value))
			return this;
		String newValue = Preconditions.checkNotNull(value, "participationUrl");
		return new ImmutableClinicalStudyInvitationMailContent(newValue);
	}

	/**
	 * This instance is equal to all instances of {@code ImmutableClinicalStudyInvitationMailContent} that have equal attribute values.
	 * @return {@code true} if {@code this} is equal to {@code another} instance
	 */
	@Override
	public boolean equals(@Nullable Object another) {
		if (this == another)
			return true;
		return another instanceof ImmutableClinicalStudyInvitationMailContent && equalTo(
				(ImmutableClinicalStudyInvitationMailContent) another);
	}

	private boolean equalTo(ImmutableClinicalStudyInvitationMailContent another) {
		return participationUrl.equals(another.participationUrl);
	}

	/**
	 * Computes a hash code from attributes: {@code participationUrl}.
	 * @return hashCode value
	 */
	@Override
	public int hashCode() {
		int h = 5381;
		h += (h << 5) + participationUrl.hashCode();
		return h;
	}

	/**
	 * Prints the immutable value {@code ClinicalStudyInvitationMailContent} with attribute values.
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("ClinicalStudyInvitationMailContent")
				.omitNullValues()
				.add("participationUrl", participationUrl)
				.toString();
	}

	/**
	 * Creates an immutable copy of a {@link ClinicalStudyInvitationMailContent} value.
	 * Uses accessors to get values to initialize the new immutable instance.
	 * If an instance is already immutable, it is returned as is.
	 * @param instance The instance to copy
	 * @return A copied immutable ClinicalStudyInvitationMailContent instance
	 */
	public static ImmutableClinicalStudyInvitationMailContent copyOf(ClinicalStudyInvitationMailContent instance) {
		if (instance instanceof ImmutableClinicalStudyInvitationMailContent) {
			return (ImmutableClinicalStudyInvitationMailContent) instance;
		}
		return ImmutableClinicalStudyInvitationMailContent.builder()
				.from(instance)
				.build();
	}

	/**
	 * Creates a builder for {@link ImmutableClinicalStudyInvitationMailContent ImmutableClinicalStudyInvitationMailContent}.
	 * @return A new ImmutableClinicalStudyInvitationMailContent builder
	 */
	public static ImmutableClinicalStudyInvitationMailContent.Builder builder() {
		return new ImmutableClinicalStudyInvitationMailContent.Builder();
	}

	/**
	 * Builds instances of type {@link ImmutableClinicalStudyInvitationMailContent ImmutableClinicalStudyInvitationMailContent}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	@NotThreadSafe
	public static final class Builder {
		private static final long INIT_BIT_PARTICIPATION_URL = 0x1L;
		private long initBits = 0x1L;

		private @Nullable
		String participationUrl;

		private Builder() {
		}

		/**
		 * Fill a builder with attribute values from the provided {@code ClinicalStudyInvitationMailContent} instance.
		 * Regular attribute values will be replaced with those from the given instance.
		 * Absent optional values will not replace present values.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder from(ClinicalStudyInvitationMailContent instance) {
			Preconditions.checkNotNull(instance, "instance");
			participationUrl(instance.getParticipationUrl());
			return this;
		}

		/**
		 * Initializes the value for the {@link ClinicalStudyInvitationMailContent#getParticipationUrl() participationUrl} attribute.
		 * @param participationUrl The value for participationUrl
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder participationUrl(String participationUrl) {
			this.participationUrl = Preconditions.checkNotNull(participationUrl, "participationUrl");
			initBits &= ~INIT_BIT_PARTICIPATION_URL;
			return this;
		}

		/**
		 * Builds a new {@link ImmutableClinicalStudyInvitationMailContent ImmutableClinicalStudyInvitationMailContent}.
		 * @return An immutable instance of ClinicalStudyInvitationMailContent
		 * @throws java.lang.IllegalStateException if any required attributes are missing
		 */
		public ImmutableClinicalStudyInvitationMailContent build() {
			if (initBits != 0) {
				throw new IllegalStateException(formatRequiredAttributesMessage());
			}
			return new ImmutableClinicalStudyInvitationMailContent(participationUrl);
		}

		private String formatRequiredAttributesMessage() {
			List<String> attributes = Lists.newArrayList();
			if ((initBits & INIT_BIT_PARTICIPATION_URL) != 0)
				attributes.add("participationUrl");
			return "Cannot build ClinicalStudyInvitationMailContent, some of required attributes are not set "
					+ attributes;
		}
	}
}
