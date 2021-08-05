package com.custodix.insite.mongodb.export.patient.domain.model.patientsearchcriteriainformation;

import java.sql.Timestamp;

public class PatientSearchCriteriaInformationRecord {
	private final String id;
	private final String namespace;
	private final Timestamp birthDate;

	private PatientSearchCriteriaInformationRecord(Builder builder) {
		id = builder.id;
		namespace = builder.namespace;
		birthDate = builder.birthDate;
	}

	public String getId() {
		return id;
	}

	public String getNamespace() {
		return namespace;
	}

	public Timestamp getBirthDate() {
		return birthDate;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String id;
		private String namespace;
		private Timestamp birthDate;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withNamespace(String namespace) {
			this.namespace = namespace;
			return this;
		}

		public Builder withBirthDate(Timestamp val) {
			birthDate = val;
			return this;
		}

		public PatientSearchCriteriaInformationRecord build() {
			return new PatientSearchCriteriaInformationRecord(this);
		}
	}
}
