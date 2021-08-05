package com.custodix.insite.mongodb.export.patient.domain.model.common;

public class Modifier {
	private final ModifierCategory category;
	private final String referenceCode;

	private Modifier(Builder builder) {
		category = builder.category;
		referenceCode = builder.referenceCode;
	}

	public ModifierCategory getCategory() {
		return category;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private ModifierCategory category;
		private String referenceCode;

		private Builder() {
		}

		public Builder withCategory(ModifierCategory category) {
			this.category = category;
			return this;
		}

		public Builder withReferenceCode(String referenceCode) {
			this.referenceCode = referenceCode;
			return this;
		}

		public Modifier build() {
			return new Modifier(this);
		}
	}
}
