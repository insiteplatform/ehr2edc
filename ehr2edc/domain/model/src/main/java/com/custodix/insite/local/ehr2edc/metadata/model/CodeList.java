package com.custodix.insite.local.ehr2edc.metadata.model;

import com.custodix.insite.local.ehr2edc.snapshots.CodeListSnapshot;

public final class CodeList {
	private String id;

	private CodeList(final Builder builder) {
		id = builder.id;
	}

	private CodeList(String id) {
		this.id = id;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getId() {
		return id;
	}

	public static CodeList restoreFrom(CodeListSnapshot codeList) {
		return codeList == null ? null : new CodeList(codeList.getId());
	}

	public CodeListSnapshot toSnapshot() {
		return CodeListSnapshot.newBuilder()
				.withId(id)
				.build();
	}

	public static final class Builder {
		private String id;

		private Builder() {
		}

		public Builder withId(final String val) {
			id = val;
			return this;
		}

		public CodeList build() {
			return new CodeList(this);
		}
	}
}
