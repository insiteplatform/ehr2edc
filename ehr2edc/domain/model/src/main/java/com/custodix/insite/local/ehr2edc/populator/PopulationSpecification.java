package com.custodix.insite.local.ehr2edc.populator;

import java.time.LocalDate;

import com.custodix.insite.local.ehr2edc.ItemQueryMappings;
import com.custodix.insite.local.ehr2edc.metadata.model.EventDefinition;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public final class PopulationSpecification {
	private final EventDefinition eventDefinition;
	private final ItemQueryMappings itemQueryMapping;
	private final SubjectId subjectId;
	private final EDCSubjectReference edcSubjectReference;
	private final PatientCDWReference patientCDWReference;
	private final LocalDate referenceDate;
	private final LocalDate consentDate;
	private final StudyId studyId;
	private final UserIdentifier populator;

	private PopulationSpecification(Builder builder) {
		eventDefinition = builder.eventDefinition;
		itemQueryMapping = builder.itemQueryMapping;
		subjectId = builder.subjectId;
		edcSubjectReference = builder.edcSubjectReference;
		patientCDWReference = builder.patientCDWReference;
		referenceDate = builder.referenceDate;
		consentDate = builder.consentDate;
		studyId = builder.studyId;
		populator = builder.populator;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public StudyId getStudyId() {
		return studyId;
	}

	public UserIdentifier getPopulator() {
		return populator;
	}

	public EventDefinition getEventDefinition() {
		return eventDefinition;
	}

	public ItemQueryMappings getStudyItemQueryMappings() {
		return itemQueryMapping;
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public EDCSubjectReference getEdcSubjectReference() {
		return edcSubjectReference;
	}

	public PatientCDWReference getPatientCDWReference() {
		return patientCDWReference;
	}

	public LocalDate getReferenceDate() {
		return referenceDate;
	}

	public LocalDate getConsentDate() {
		return consentDate;
	}

	public static final class Builder {
		private ItemQueryMappings itemQueryMapping;
		private SubjectId subjectId;
		private EDCSubjectReference edcSubjectReference;
		private PatientCDWReference patientCDWReference;
		private LocalDate referenceDate;
		private LocalDate consentDate;
		private StudyId studyId;
		private UserIdentifier populator;
		private EventDefinition eventDefinition;

		private Builder() {
		}

		public Builder withEventDefinition(EventDefinition eventDefinition) {
			this.eventDefinition = eventDefinition;
			return this;
		}

		public Builder withStudyItemQueryMappings(ItemQueryMappings studyItemQueryMappings) {
			this.itemQueryMapping = studyItemQueryMappings;
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

		public Builder withPatientCDWReference(PatientCDWReference patientCDWReference) {
			this.patientCDWReference = patientCDWReference;

			return this;
		}

		public Builder withReferenceDate(LocalDate referenceDate) {
			this.referenceDate = referenceDate;
			return this;
		}

		public Builder withConsentDate(LocalDate consentDate) {
			this.consentDate = consentDate;
			return this;
		}

		public Builder withStudyId(StudyId studyId) {
			this.studyId = studyId;
			return this;
		}

		public Builder withPopulator(UserIdentifier populator) {
			this.populator = populator;
			return this;
		}

		public PopulationSpecification build() {
			return new PopulationSpecification(this);
		}
	}
}
