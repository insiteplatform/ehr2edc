package com.custodix.insite.mongodb.export.patient.infrastructure.embedded;

import com.custodix.insite.mongodb.export.patient.domain.model.VitalSignsInformation;

public class VitalSignsInfoMapping {
	private final String code;
	private final String namespace;
	private final VitalSignsInformation vitalSignsInformation;

	private VitalSignsInfoMapping(Builder builder) {
		code = builder.code;
		namespace = builder.namespace;
		vitalSignsInformation = builder.vitalSignsInformation;
	}

	public String getCode() {
		return code;
	}

	public String getNamespace() {
		return namespace;
	}

	public VitalSignsInformation getVitalSignsInformation() {
		return vitalSignsInformation;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String code;
		private String namespace;
		private VitalSignsInformation vitalSignsInformation;

		private Builder() {
		}

		public Builder withCode(String val) {
			code = val;
			return this;
		}

		public Builder withNamespace(String val) {
			namespace = val;
			return this;
		}

		public Builder withVitalSignsInformation(VitalSignsInformation val) {
			vitalSignsInformation = val;
			return this;
		}

		public VitalSignsInfoMapping build() {
			return new VitalSignsInfoMapping(this);
		}
	}
}