package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;

import com.custodix.insite.local.ehr2edc.provenance.model.Measurement;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceVitalSign;
import com.custodix.insite.local.ehr2edc.provenance.model.VitalSignConcept;

@TypeAlias("ReviewedProvenanceVitalSign")
final class ReviewedProvenanceVitalSignDocument implements ReviewedProvenanceDataPointDocument {
	private final ReviewedVitalSignConceptDocument concept;
	private final LocalDateTime effectiveDateTime;
	private final ReviewedMeasurementDocument measurement;

	@PersistenceConstructor
	private ReviewedProvenanceVitalSignDocument(ReviewedVitalSignConceptDocument concept, LocalDateTime effectiveDateTime,
												ReviewedMeasurementDocument measurement) {
		this.concept = concept;
		this.effectiveDateTime = effectiveDateTime;
		this.measurement = measurement;
	}

	private ReviewedProvenanceVitalSignDocument(Builder builder) {
		concept = builder.concept;
		effectiveDateTime = builder.effectiveDateTime;
		measurement = builder.measurement;
	}

	@Override
	public ProvenanceDataPoint restore() {
		return ProvenanceVitalSign.newBuilder()
				.withConcept(getRestoredConcept())
				.withEffectiveDateTime(effectiveDateTime)
				.withMeasurement(getRestoredMeasurement())
				.build();
	}

	private Measurement getRestoredMeasurement() {
		return Optional.ofNullable(measurement)
				.map(ReviewedMeasurementDocument::restore)
				.orElse(null);
	}

	private VitalSignConcept getRestoredConcept() {
		return Optional.ofNullable(concept)
				.map(ReviewedVitalSignConceptDocument::restore)
				.orElse(null);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private ReviewedVitalSignConceptDocument concept;
		private LocalDateTime effectiveDateTime;
		private ReviewedMeasurementDocument measurement;

		private Builder() {
		}

		public Builder withConcept(ReviewedVitalSignConceptDocument concept) {
			this.concept = concept;
			return this;
		}

		public Builder withEffectiveDateTime(LocalDateTime effectiveDateTime) {
			this.effectiveDateTime = effectiveDateTime;
			return this;
		}

		public Builder withMeasurement(ReviewedMeasurementDocument measurement) {
			this.measurement = measurement;
			return this;
		}

		public ReviewedProvenanceVitalSignDocument build() {
			return new ReviewedProvenanceVitalSignDocument(this);
		}
	}
}
