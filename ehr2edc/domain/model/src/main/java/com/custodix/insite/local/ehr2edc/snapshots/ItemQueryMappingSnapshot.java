package com.custodix.insite.local.ehr2edc.snapshots;

public class ItemQueryMappingSnapshot {
	private final String value;

	private ItemQueryMappingSnapshot(final Builder builder) {
		value = builder.value;
	}

	public String getValue() {
		return value;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(final ItemQueryMappingSnapshot copy) {
		Builder builder = new Builder();
		builder.value = copy.getValue();
		return builder;
	}

	public static final class Builder {
		private String value;

		private Builder() {
		}

		public ItemQueryMappingSnapshot build() {
			return new ItemQueryMappingSnapshot(this);
		}

		public Builder withValue(final String val) {
			value = val;
			return this;
		}
	}
}
