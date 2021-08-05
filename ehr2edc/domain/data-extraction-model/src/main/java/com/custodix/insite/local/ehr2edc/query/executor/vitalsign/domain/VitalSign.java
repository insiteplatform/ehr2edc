package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain;

import java.time.LocalDateTime;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.domain.HasConcept;
import com.custodix.insite.local.ehr2edc.query.executor.common.domain.HasSubjectId;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public final class VitalSign implements DataPoint, HasConcept, HasSubjectId {
	private final VitalSignConcept concept;
	private final SubjectId subjectId;
	private final LocalDateTime effectiveDateTime;
	private final Measurement measurement;

	private VitalSign(Builder builder) {
		concept = builder.concept;
		subjectId = builder.subjectId;
		effectiveDateTime = builder.effectiveDateTime;
		measurement = builder.measurement;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public ConceptCode getConcept() {
		return concept.getConcept();
	}

	public VitalSignConcept getVitalSignConcept() {
		return concept;
	}

	public LocalDateTime getEffectiveDateTime() {
		return effectiveDateTime;
	}

	public Measurement getMeasurement() {
		return measurement;
	}

	@Override
	public SubjectId getSubjectId() {
		return subjectId;
	}

	public static final class Builder {
		private VitalSignConcept concept;
		private SubjectId subjectId;
		private LocalDateTime effectiveDateTime;
		private Measurement measurement;

		private Builder() {
		}

		public Builder withConcept(VitalSignConcept concept) {
			this.concept = concept;
			return this;
		}

		public Builder withSubjectId(SubjectId subjectId) {
			this.subjectId = subjectId;
			return this;
		}

		public Builder withEffectiveDateTime(LocalDateTime effectiveDateTime) {
			this.effectiveDateTime = effectiveDateTime;
			return this;
		}

		public Builder withMeasurement(Measurement measurement) {
			this.measurement = measurement;
			return this;
		}

		public VitalSign build() {
			return new VitalSign(this);
		}
	}
}
