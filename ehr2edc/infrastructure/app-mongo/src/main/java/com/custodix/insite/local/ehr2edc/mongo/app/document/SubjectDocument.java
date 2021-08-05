package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public final class SubjectDocument {
	private final PatientCDWReferenceDocument patientCDWReference;
	private final String subjectId;
	private final String edcReference;
	private final LocalDate dateOfConsent;
	private final LocalDate dateOfConsentWithdrawn;
	private final DeregisterReason deregisterReason;
	private final EHRRegistrationStatus ehrRegistrationStatus;

	@PersistenceConstructor
	private SubjectDocument(PatientCDWReferenceDocument patientCDWReference, String subjectId, String edcReference,
			LocalDate dateOfConsent, LocalDate dateOfConsentWithdrawn, DeregisterReason deregisterReason,
			EHRRegistrationStatus ehrRegistrationStatus) {
		this.patientCDWReference = patientCDWReference;
		this.subjectId = subjectId;
		this.edcReference = edcReference;
		this.dateOfConsent = dateOfConsent;
		this.dateOfConsentWithdrawn = dateOfConsentWithdrawn;
		this.deregisterReason = deregisterReason;
		this.ehrRegistrationStatus = ehrRegistrationStatus;
	}

	private SubjectDocument(Builder builder) {
		patientCDWReference = builder.patientCDWReference;
		subjectId = builder.subjectId;
		edcReference = builder.edcReference;
		dateOfConsent = builder.dateOfConsent;
		dateOfConsentWithdrawn = builder.dateOfConsentWithdrawn;
		deregisterReason = builder.deregisterReason;
		ehrRegistrationStatus = builder.ehrRegistrationStatus;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	static Collection<SubjectDocument> fromSnapshots(Collection<SubjectSnapshot> subjects) {
		return subjects.stream()
				.map(s -> new SubjectDocument(new PatientCDWReferenceDocument(s.getPatientCDWReference()
						.getSource(), s.getPatientCDWReference()
						.getId()), s.getSubjectId()
						.getId(), s.getEdcSubjectReference()
						.getId(), s.getDateOfConsent(), s.getDateOfConsentWithdrawn(), s.getDeregisterReason(),
						s.getEHRRegistrationStatus()))
				.collect(Collectors.toList());
	}

	public PatientCDWReferenceDocument getPatientCDWReference() {
		return patientCDWReference;
	}

	public String getSubjectId() {
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

	public String getEdcReference() {
		return edcReference;
	}

	public EHRRegistrationStatus getEhrRegistrationStatus() {
		return ehrRegistrationStatus;
	}

	public SubjectSnapshot toSnapshot() {
		return SubjectSnapshot.newBuilder()
				.withSubjectId(SubjectId.of(subjectId))
				.withPatientCDWReference(PatientCDWReference.newBuilder()
						.withId(patientCDWReference.getId())
						.withSource(patientCDWReference.getSource())
						.build())
				.withDateOfConsent(dateOfConsent)
				.withDateOfConsentWithdrawn(dateOfConsentWithdrawn)
				.withDeregisterReason(deregisterReason)
				.withEdcSubjectReference(EDCSubjectReference.of(edcReference))
				.withEHRRegistrationStatus(ehrRegistrationStatus)
				.build();
	}

	public static final class Builder {
		private PatientCDWReferenceDocument patientCDWReference;
		private String subjectId;
		private String edcReference;
		private LocalDate dateOfConsent;
		private LocalDate dateOfConsentWithdrawn;
		private DeregisterReason deregisterReason;
		private EHRRegistrationStatus ehrRegistrationStatus;

		private Builder() {
		}

		public Builder withPatientCDWReference(final PatientCDWReferenceDocument val) {
			patientCDWReference = val;
			return this;
		}

		public Builder withSubjectId(final String val) {
			subjectId = val;
			return this;
		}

		public Builder withEdcReference(final String val) {
			edcReference = val;
			return this;
		}

		public Builder withDateOfConsent(final LocalDate val) {
			dateOfConsent = val;
			return this;
		}

		public Builder withDateOfConsentWithdrawn(final LocalDate val) {
			dateOfConsentWithdrawn = val;
			return this;
		}

		public Builder withDeregisterReason(final DeregisterReason val) {
			deregisterReason = val;
			return this;
		}

		public Builder withEhrRegistrationStatus(EHRRegistrationStatus val) {
			ehrRegistrationStatus = val;
			return this;
		}

		public SubjectDocument build() {
			return new SubjectDocument(this);
		}
	}
}
