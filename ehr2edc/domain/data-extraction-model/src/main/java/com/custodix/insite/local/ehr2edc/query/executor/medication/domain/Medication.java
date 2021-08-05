package com.custodix.insite.local.ehr2edc.query.executor.medication.domain;

import java.time.LocalDateTime;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.domain.HasConcept;
import com.custodix.insite.local.ehr2edc.query.executor.common.domain.HasSubjectId;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public final class Medication implements DataPoint, HasConcept, HasSubjectId {
	private final SubjectId subjectId;
	private final MedicationConcept concept;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final Dosage dosage;
	private final String administrationRoute;
	private final String doseForm;
	private final String dosingFrequency;
	private final EventType eventType;

	private Medication(Builder builder) {
		subjectId = builder.subjectId;
		concept = builder.concept;
		startDate = builder.startDate;
		endDate = builder.endDate;
		dosage = builder.dosage;
		administrationRoute = builder.administrationRoute;
		doseForm = builder.doseForm;
		dosingFrequency = builder.dosingFrequency;
		eventType = builder.eventType;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public ConceptCode getConcept() {
		return concept.getConcept();
	}

	public MedicationConcept getMedicationConcept() {
		return concept;
	}

	public Dosage getDosage() {
		return dosage;
	}

	@Override
	public SubjectId getSubjectId() {
		return subjectId;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public String getAdministrationRoute() {
		return administrationRoute;
	}

	public String getDoseForm() {
		return doseForm;
	}

	public String getDosingFrequency() {
		return dosingFrequency;
	}

	public EventType getEventType() {
		return eventType;
	}

	public static final class Builder {
		private SubjectId subjectId;
		private MedicationConcept concept;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private Dosage dosage;
		private String administrationRoute;
		private String doseForm;
		private String dosingFrequency;
		private EventType eventType;

		private Builder() {
		}

		public Builder withSubjectId(SubjectId subjectId) {
			this.subjectId = subjectId;
			return this;
		}

		public Builder withConcept(MedicationConcept concept) {
			this.concept = concept;
			return this;
		}

		public Builder withStartDate(LocalDateTime startDate) {
			this.startDate = startDate;
			return this;
		}

		public Builder withEndDate(LocalDateTime endDate) {
			this.endDate = endDate;
			return this;
		}

		public Builder withDosage(Dosage dosage) {
			this.dosage = dosage;
			return this;
		}

		public Builder withAdministrationRoute(String administrationRoute) {
			this.administrationRoute = administrationRoute;
			return this;
		}

		public Builder withDoseForm(String doseForm) {
			this.doseForm = doseForm;
			return this;
		}

		public Builder withDosingFrequency(String dosingFrequency) {
			this.dosingFrequency = dosingFrequency;
			return this;
		}

		public Builder withEventType(EventType eventType) {
			this.eventType = eventType;
			return this;
		}

		public Medication build() {
			return new Medication(this);
		}
	}
}
