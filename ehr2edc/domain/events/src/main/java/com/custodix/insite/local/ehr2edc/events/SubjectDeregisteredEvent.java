package com.custodix.insite.local.ehr2edc.events;

import java.time.LocalDate;

import com.custodix.insite.local.ehr2edc.vocabulary.DeregisterReason;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public class SubjectDeregisteredEvent {
	private final StudyId studyId;
	private final SubjectId subjectId;
	private final LocalDate date;
	private final DeregisterReason reason;
	private final PatientCDWReference patientCDWReference;

	private SubjectDeregisteredEvent(Builder builder) {
		patientCDWReference = builder.patientCDWReference;
		studyId = builder.studyId;
		subjectId = builder.subjectId;
		date = builder.date;
		reason = builder.reason;
	}

	public StudyId getStudyId() {
		return studyId;
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public LocalDate getDate() {
		return date;
	}

	public DeregisterReason getReason() {
		return reason;
	}

	public PatientCDWReference getPatientCDWReference() {
		return patientCDWReference;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private StudyId studyId;
		private LocalDate date;
		private SubjectId subjectId;
		private PatientCDWReference patientCDWReference;
		private DeregisterReason reason;

		private Builder() {
		}

		public Builder withStudyId(StudyId studyId) {
			this.studyId = studyId;
			return this;
		}

		public Builder withSubjectId(SubjectId subjectId) {
			this.subjectId = subjectId;
			return this;
		}

		public Builder withPatientCDWReference(PatientCDWReference patientCDWReference) {
			this.patientCDWReference = patientCDWReference;
			return this;
		}

		public Builder withDate(LocalDate date) {
			this.date = date;
			return this;
		}

		public Builder withReason(DeregisterReason reason) {
			this.reason = reason;
			return this;
		}

		public SubjectDeregisteredEvent build() {
			return new SubjectDeregisteredEvent(this);
		}
	}
}
