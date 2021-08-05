package com.custodix.insite.mongodb.export.patient.domain.model.common;

public class Concept {
	private final String schema;
	private final String code;

	private Concept(final Builder builder) {
		schema = builder.schema;
		code = builder.code;
	}

	public Builder toBuilder() {
		Builder builder = new Builder();
		builder.schema = getSchema();
		builder.code = getCode();
		return builder;
	}

	public String getPathElement() {
		if (schema!=null && schema.isEmpty()) {
			return code;
		} else {
			return schema + "_" + code;
		}
	}

	public String getSchema() {
		return schema;
	}

	public String getCode() {
		return code;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String schema;
		private String code;

		private Builder() {
		}

		public Builder withSchema(String val) {
			schema = val;
			return this;
		}

		public Builder withCode(String val) {
			code = val;
			return this;
		}

		public Concept build() {
			return new Concept(this);
		}
	}
}