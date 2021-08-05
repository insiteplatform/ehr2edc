package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;

import javax.validation.constraints.NotBlank;

public final class ExternalSiteId {
	@NotBlank
	private final String id;

	private ExternalSiteId(Builder builder) {
		id = builder.id;
	}

	public String getId() {
		return id;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(ExternalSiteId copy) {
		Builder builder = new Builder();
		builder.id = copy.id;
		return builder;
	}

	public static ExternalSiteId of(String id) {
		return ExternalSiteId.newBuilder()
				.withId(id)
				.build();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ExternalSiteId externalSiteId = (ExternalSiteId) o;
		return Objects.equals(id, externalSiteId.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "ExternalSiteId{" + "id='" + id + '\'' + '}';
	}

	public static final class Builder {
		private String id;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public ExternalSiteId build() {
			return new ExternalSiteId(this);
		}
	}
}
