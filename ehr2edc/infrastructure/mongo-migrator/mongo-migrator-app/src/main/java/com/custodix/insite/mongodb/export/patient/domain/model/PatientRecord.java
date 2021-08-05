package com.custodix.insite.mongodb.export.patient.domain.model;

import java.sql.Timestamp;

public class PatientRecord {
	private final String id;
	private final String namespace;
	private final String gender;
	private final Timestamp birthDate;
	private final String vitalStatus;
	private final Timestamp deathDate;

	private PatientRecord(Builder builder) {
		id = builder.id;
		namespace = builder.namespace;
		gender = builder.gender;
		birthDate = builder.birthDate;
		vitalStatus = builder.vitalStatus;
		deathDate = builder.deathDate;
	}

	public String getId() {
		return id;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getGender() {
		return gender;
	}

	public Timestamp getBirthDate() {
		return birthDate;
	}

	public String getVitalStatus() {
		return vitalStatus;
	}

	public Timestamp getDeathDate() {
		return deathDate;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String id;
		private String namespace;
		private String gender;
		private Timestamp birthDate;
		private String vitalStatus;
		private Timestamp deathDate;

		private Builder() {
		}

		public Builder withId(String val) {
			id = val;
			return this;
		}

		public Builder withNamespace(String val) {
			namespace = val;
			return this;
		}

		public Builder withGender(String val) {
			gender = val;
			return this;
		}

		public Builder withBirthDate(Timestamp val) {
			birthDate = val;
			return this;
		}

		public Builder withVitalStatus(String val) {
			vitalStatus = val;
			return this;
		}

		public Builder withDeathDate(Timestamp val) {
			deathDate = val;
			return this;
		}

		public PatientRecord build() {
			return new PatientRecord(this);
		}
	}
}