package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;

import javax.validation.constraints.NotBlank;

public final class ItemId {

	@NotBlank
	private final String id;

	private ItemId(Builder builder) {
		id = builder.id;
	}

	public static ItemId of(String value) {
		return ItemId.newBuilder()
				.withId(value)
				.build();
	}

	public String getId() {
		return id;
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

		public ItemId build() {
			return new ItemId(this);
		}
	}

	@Override
	public String toString() {
		return "ItemId{" + "id='" + id + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final ItemId itemId = (ItemId) o;
		return Objects.equals(id, itemId.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
