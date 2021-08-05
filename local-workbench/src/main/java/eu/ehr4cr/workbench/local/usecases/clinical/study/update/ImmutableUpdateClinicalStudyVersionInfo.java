package eu.ehr4cr.workbench.local.usecases.clinical.study.update;

import java.util.List;
import java.util.Map;

import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * Immutable implementation of {@link UpdateClinicalStudyVersionInfo}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableUpdateClinicalStudyVersionInfo.builder()}.
 */
@SuppressWarnings({ "all" })
@ParametersAreNonnullByDefault
@Generated({ "Immutables.generator", "UpdateClinicalStudyVersionInfo" })

public final class ImmutableUpdateClinicalStudyVersionInfo implements UpdateClinicalStudyVersionInfo {
	private final String name;
	private final String queryContent;
	private final ImmutableMap<String, byte[]> documents;

	private ImmutableUpdateClinicalStudyVersionInfo(String name, String queryContent,
			ImmutableMap<String, byte[]> documents) {
		this.name = name;
		this.queryContent = queryContent;
		this.documents = documents;
	}

	/**
	 * @return The value of the {@code name} attribute
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @return The value of the {@code queryContent} attribute
	 */
	@Override
	public String getQueryContent() {
		return queryContent;
	}

	/**
	 * @return The value of the {@code documents} attribute
	 */
	@Override
	public ImmutableMap<String, byte[]> getDocuments() {
		return documents;
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UpdateClinicalStudyVersionInfo#getName() name} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for name
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUpdateClinicalStudyVersionInfo withName(String value) {
		if (this.name.equals(value))
			return this;
		String newValue = Preconditions.checkNotNull(value, "name");
		return new ImmutableUpdateClinicalStudyVersionInfo(newValue, this.queryContent, this.documents);
	}

	/**
	 * Copy the current immutable object by setting a value for the {@link UpdateClinicalStudyVersionInfo#getQueryContent() queryContent} attribute.
	 * An equals check used to prevent copying of the same value by returning {@code this}.
	 * @param value A new value for queryContent
	 * @return A modified copy of the {@code this} object
	 */
	public final ImmutableUpdateClinicalStudyVersionInfo withQueryContent(String value) {
		if (this.queryContent.equals(value))
			return this;
		String newValue = Preconditions.checkNotNull(value, "queryContent");
		return new ImmutableUpdateClinicalStudyVersionInfo(this.name, newValue, this.documents);
	}

	/**
	 * Copy the current immutable object by replacing the {@link UpdateClinicalStudyVersionInfo#getDocuments() documents} map with the specified map.
	 * Nulls are not permitted as keys or values.
	 * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
	 * @param entries The entries to be added to the documents map
	 * @return A modified copy of {@code this} object
	 */
	public final ImmutableUpdateClinicalStudyVersionInfo withDocuments(Map<String, ? extends byte[]> entries) {
		if (this.documents == entries)
			return this;
		ImmutableMap<String, byte[]> newValue = ImmutableMap.copyOf(entries);
		return new ImmutableUpdateClinicalStudyVersionInfo(this.name, this.queryContent, newValue);
	}

	/**
	 * This instance is equal to all instances of {@code ImmutableUpdateClinicalStudyVersionInfo} that have equal attribute values.
	 * @return {@code true} if {@code this} is equal to {@code another} instance
	 */
	@Override
	public boolean equals(@Nullable Object another) {
		if (this == another)
			return true;
		return another instanceof ImmutableUpdateClinicalStudyVersionInfo && equalTo(
				(ImmutableUpdateClinicalStudyVersionInfo) another);
	}

	private boolean equalTo(ImmutableUpdateClinicalStudyVersionInfo another) {
		return name.equals(another.name) && queryContent.equals(another.queryContent) && documents.equals(
				another.documents);
	}

	/**
	 * Computes a hash code from attributes: {@code name}, {@code queryContent}, {@code documents}.
	 * @return hashCode value
	 */
	@Override
	public int hashCode() {
		int h = 5381;
		h += (h << 5) + name.hashCode();
		h += (h << 5) + queryContent.hashCode();
		h += (h << 5) + documents.hashCode();
		return h;
	}

	/**
	 * Prints the immutable value {@code UpdateClinicalStudyVersionInfo} with attribute values.
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper("UpdateClinicalStudyVersionInfo")
				.omitNullValues()
				.add("name", name)
				.add("queryContent", queryContent)
				.add("documents", documents)
				.toString();
	}

	/**
	 * Creates an immutable copy of a {@link UpdateClinicalStudyVersionInfo} value.
	 * Uses accessors to get values to initialize the new immutable instance.
	 * If an instance is already immutable, it is returned as is.
	 * @param instance The instance to copy
	 * @return A copied immutable UpdateClinicalStudyVersionInfo instance
	 */
	public static ImmutableUpdateClinicalStudyVersionInfo copyOf(UpdateClinicalStudyVersionInfo instance) {
		if (instance instanceof ImmutableUpdateClinicalStudyVersionInfo) {
			return (ImmutableUpdateClinicalStudyVersionInfo) instance;
		}
		return ImmutableUpdateClinicalStudyVersionInfo.builder()
				.from(instance)
				.build();
	}

	/**
	 * Creates a builder for {@link ImmutableUpdateClinicalStudyVersionInfo ImmutableUpdateClinicalStudyVersionInfo}.
	 * @return A new ImmutableUpdateClinicalStudyVersionInfo builder
	 */
	public static ImmutableUpdateClinicalStudyVersionInfo.Builder builder() {
		return new ImmutableUpdateClinicalStudyVersionInfo.Builder();
	}

	/**
	 * Builds instances of type {@link ImmutableUpdateClinicalStudyVersionInfo ImmutableUpdateClinicalStudyVersionInfo}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	@NotThreadSafe
	public static final class Builder {
		private static final long INIT_BIT_NAME = 0x1L;
		private static final long INIT_BIT_QUERY_CONTENT = 0x2L;
		private long initBits = 0x3L;

		private @Nullable
		String name;
		private @Nullable
		String queryContent;
		private ImmutableMap.Builder<String, byte[]> documents = ImmutableMap.builder();

		private Builder() {
		}

		/**
		 * Fill a builder with attribute values from the provided {@code UpdateClinicalStudyVersionInfo} instance.
		 * Regular attribute values will be replaced with those from the given instance.
		 * Absent optional values will not replace present values.
		 * Collection elements and entries will be added, not replaced.
		 * @param instance The instance from which to copy values
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder from(UpdateClinicalStudyVersionInfo instance) {
			Preconditions.checkNotNull(instance, "instance");
			name(instance.getName());
			queryContent(instance.getQueryContent());
			putAllDocuments(instance.getDocuments());
			return this;
		}

		/**
		 * Initializes the value for the {@link UpdateClinicalStudyVersionInfo#getName() name} attribute.
		 * @param name The value for name
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder name(String name) {
			this.name = Preconditions.checkNotNull(name, "name");
			initBits &= ~INIT_BIT_NAME;
			return this;
		}

		/**
		 * Initializes the value for the {@link UpdateClinicalStudyVersionInfo#getQueryContent() queryContent} attribute.
		 * @param queryContent The value for queryContent
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder queryContent(String queryContent) {
			this.queryContent = Preconditions.checkNotNull(queryContent, "queryContent");
			initBits &= ~INIT_BIT_QUERY_CONTENT;
			return this;
		}

		/**
		 * Put one entry to the {@link UpdateClinicalStudyVersionInfo#getDocuments() documents} map.
		 * @param key The key in the documents map
		 * @param value The associated value in the documents map
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder putDocuments(String key, byte[] value) {
			this.documents.put(key, value);
			return this;
		}

		/**
		 * Put one entry to the {@link UpdateClinicalStudyVersionInfo#getDocuments() documents} map. Nulls are not permitted
		 * @param entry The key and value entry
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder putDocuments(Map.Entry<String, ? extends byte[]> entry) {
			this.documents.put(entry);
			return this;
		}

		/**
		 * Sets or replaces all mappings from the specified map as entries for the {@link UpdateClinicalStudyVersionInfo#getDocuments() documents} map. Nulls are not permitted
		 * @param documents The entries that will be added to the documents map
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder documents(Map<String, ? extends byte[]> documents) {
			this.documents = ImmutableMap.builder();
			return putAllDocuments(documents);
		}

		/**
		 * Put all mappings from the specified map as entries to {@link UpdateClinicalStudyVersionInfo#getDocuments() documents} map. Nulls are not permitted
		 * @param documents The entries that will be added to the documents map
		 * @return {@code this} builder for use in a chained invocation
		 */
		public final Builder putAllDocuments(Map<String, ? extends byte[]> documents) {
			this.documents.putAll(documents);
			return this;
		}

		/**
		 * Builds a new {@link ImmutableUpdateClinicalStudyVersionInfo ImmutableUpdateClinicalStudyVersionInfo}.
		 * @return An immutable instance of UpdateClinicalStudyVersionInfo
		 * @throws java.lang.IllegalStateException if any required attributes are missing
		 */
		public ImmutableUpdateClinicalStudyVersionInfo build() {
			if (initBits != 0) {
				throw new IllegalStateException(formatRequiredAttributesMessage());
			}
			return new ImmutableUpdateClinicalStudyVersionInfo(name, queryContent, documents.build());
		}

		private String formatRequiredAttributesMessage() {
			List<String> attributes = Lists.newArrayList();
			if ((initBits & INIT_BIT_NAME) != 0)
				attributes.add("name");
			if ((initBits & INIT_BIT_QUERY_CONTENT) != 0)
				attributes.add("queryContent");
			return "Cannot build UpdateClinicalStudyVersionInfo, some of required attributes are not set " + attributes;
		}
	}
}
