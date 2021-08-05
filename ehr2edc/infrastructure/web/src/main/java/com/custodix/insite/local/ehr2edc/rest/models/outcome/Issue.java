package com.custodix.insite.local.ehr2edc.rest.models.outcome;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = Issue.Builder.class)
public final class Issue {
	private final String reference;
	private final String field;
	private final String message;

	private Issue(Builder builder) {
		reference = builder.reference;
		field = builder.field;
		message = builder.message;
	}

	public String getReference() {
		return reference;
	}

	public String getField() {
		return field;
	}

	public String getMessage() {
		return message;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String reference;
		private String field;
		private String message;

		private Builder() {
		}

		public Builder withReference(String reference) {
			this.reference = reference;
			return this;
		}

		public Builder withField(String field) {
			this.field = field;
			return this;
		}

		public Builder withMessage(String message) {
			this.message = message;
			return this;
		}

		public Issue build() {
			return new Issue(this);
		}
	}
}
