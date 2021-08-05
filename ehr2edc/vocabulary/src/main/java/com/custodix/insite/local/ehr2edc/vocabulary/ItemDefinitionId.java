package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;

import javax.validation.constraints.NotBlank;

public final class ItemDefinitionId {

	@NotBlank
	private final String id;

	private ItemDefinitionId(Builder builder) {
		id = builder.id;
	}

	public static ItemDefinitionId of(String value) {
		return ItemDefinitionId.newBuilder()
				.withId(value)
				.build();
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ItemDefinitionId that = (ItemDefinitionId) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public String toString() {
		return "ItemDefinitionId{" + "id='" + id + '\'' + '}';
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String id;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public ItemDefinitionId build() {
			return new ItemDefinitionId(this);
		}
	}
}
