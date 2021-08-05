package com.custodix.insite.local.ehr2edc.snapshots;

import java.time.LocalDate;

import com.custodix.insite.local.ehr2edc.vocabulary.*;

public final class SubjectSnapshot {
	private final PatientCDWReference patientCDWReference;
	private final SubjectId subjectId;
	private final EDCSubjectReference edcSubjectReference;
	private final LocalDate dateOfConsent;
	private final LocalDate dateOfConsentWithdrawn;
	private final DeregisterReason deregisterReason;
	private final EHRRegistrationStatus ehrRegistrationStatus;

	private SubjectSnapshot(Builder builder) {
		patientCDWReference = builder.patientCDWReference;
		subjectId = builder.subjectId;
		edcSubjectReference = builder.edcSubjectReference;
		dateOfConsent = builder.dateOfConsent;
		dateOfConsentWithdrawn = builder.dateOfConsentWithdrawn;
		deregisterReason = builder.deregisterReason;
		ehrRegistrationStatus = builder.ehrRegistrationStatus;
	}

	public PatientCDWReference getPatientCDWReference() {
		return patientCDWReference;
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public LocalDate getDateOfConsent() {
		return dateOfConsent;
	}

	public LocalDate getDateOfConsentWithdrawn() {
		return dateOfConsentWithdrawn;
	}

	public DeregisterReason getDeregisterReason() {
		return deregisterReason;
	}

	public EHRRegistrationStatus getEHRRegistrationStatus() {
		return ehrRegistrationStatus;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public EDCSubjectReference getEdcSubjectReference() {
		return edcSubjectReference == null ? EDCSubjectReference.of(subjectId.getId()) : edcSubjectReference;
	}

	public static final class Builder {
		private PatientCDWReference patientCDWReference;
		private SubjectId subjectId;
		private EDCSubjectReference edcSubjectReference;
		private LocalDate dateOfConsent;
		private LocalDate dateOfConsentWithdrawn;
		private DeregisterReason deregisterReason;
		private EHRRegistrationStatus ehrRegistrationStatus;

		private Builder() {
		}

		public Builder withPatientCDWReference(PatientCDWReference patientCDWReference) {
			this.patientCDWReference = patientCDWReference;
			return this;
		}

		public Builder withSubjectId(SubjectId subjectId) {
			this.subjectId = subjectId;
			return this;
		}

		public Builder withEdcSubjectReference(EDCSubjectReference edcSubjectReference) {
			this.edcSubjectReference = edcSubjectReference;
			return this;
		}

		public Builder withDateOfConsent(LocalDate dateOfConsent) {
			this.dateOfConsent = dateOfConsent;
			return this;
		}

		public Builder withDateOfConsentWithdrawn(LocalDate dateOfConsentWithdrawn) {
			this.dateOfConsentWithdrawn = dateOfConsentWithdrawn;
			return this;
		}

		public Builder withDeregisterReason(DeregisterReason deregisterReason) {
			this.deregisterReason = deregisterReason;
			return this;
		}

		public Builder withEHRRegistrationStatus(EHRRegistrationStatus val) {
			ehrRegistrationStatus = val;
			return this;
		}

		public SubjectSnapshot build() {
			return new SubjectSnapshot(this);
		}
	}
}
