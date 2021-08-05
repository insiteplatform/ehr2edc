package com.custodix.insite.mongodb.export.patient.snapshot;

import com.custodix.insite.mongodb.export.patient.vocabulary.ActiveSubjectId;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public final class ActiveSubjectSnapshot {

	private final ActiveSubjectId id;
	private final PatientIdentifier patientIdentifier;

	private ActiveSubjectSnapshot(final Builder builder) {
		id = builder.id;
		patientIdentifier = builder.patientIdentifier;
	}

	public ActiveSubjectId getId() {
		return id;
	}

	public PatientIdentifier getPatientIdentifier() {
		return patientIdentifier;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private PatientIdentifier patientIdentifier;
		private ActiveSubjectId id;

		private Builder() {
		}

		public Builder withPatientIdentifier(final PatientIdentifier val) {
			patientIdentifier = val;
			return this;
		}

		public ActiveSubjectSnapshot build() {
			return new ActiveSubjectSnapshot(this);
		}

		public Builder withId(final ActiveSubjectId val) {
			id = val;
			return this;
		}
	}
}
