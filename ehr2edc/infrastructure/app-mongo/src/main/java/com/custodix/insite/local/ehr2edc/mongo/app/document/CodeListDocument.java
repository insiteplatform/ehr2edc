package com.custodix.insite.local.ehr2edc.mongo.app.document;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.snapshots.CodeListSnapshot;

public final class CodeListDocument {
	private final String id;

	@PersistenceConstructor
	private CodeListDocument(String id) {
		this.id = id;
	}

	private CodeListDocument(final Builder builder) {
		id = builder.id;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(final CodeListDocument copy) {
		Builder builder = new Builder();
		builder.id = copy.getId();
		return builder;
	}

	static CodeListDocument fromSnapshot(CodeListSnapshot codeListSnapshot) {
		return codeListSnapshot == null ? null : new CodeListDocument(codeListSnapshot.getId());

	}

	public String getId() {
		return id;
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

		public CodeListDocument build() {
			return new CodeListDocument(this);
		}
	}
}
