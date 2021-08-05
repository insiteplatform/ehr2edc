package com.custodix.insite.local.ehr2edc.snapshots;

public final class CodeListSnapshot {
	private final String id;

	private CodeListSnapshot(final Builder builder) {
		id = builder.id;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getId() {
		return id;
	}

	public static final class Builder {
		private String id;

		private Builder() {
		}

		public Builder withId(final String val) {
			id = val;
			return this;
		}

		public CodeListSnapshot build() {
			return new CodeListSnapshot(this);
		}
	}
}
