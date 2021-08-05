package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;

import javax.validation.constraints.NotBlank;

public final class ItemGroupDefinitionId {

	@NotBlank
	private final String id;

	private ItemGroupDefinitionId(Builder builder) {
		id = builder.id;
	}

	public static ItemGroupDefinitionId of(String value) {
		return ItemGroupDefinitionId.newBuilder()
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
		final ItemGroupDefinitionId that = (ItemGroupDefinitionId) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "ItemGroupDefinitionId{" + "id='" + id + '\'' + '}';
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

		public ItemGroupDefinitionId build() {
			return new ItemGroupDefinitionId(this);
		}
	}
}
