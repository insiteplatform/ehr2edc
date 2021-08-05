package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;

import com.custodix.insite.local.ehr2edc.provenance.model.Measurement;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceVitalSign;
import com.custodix.insite.local.ehr2edc.provenance.model.VitalSignConcept;

@TypeAlias("ProvenanceVitalSign")
public final class ProvenanceVitalSignDocument implements ProvenanceDataPointDocument {
	private final VitalSignConceptDocument concept;
	private final LocalDateTime effectiveDateTime;
	private final MeasurementDocument measurement;

	@PersistenceConstructor
	private ProvenanceVitalSignDocument(VitalSignConceptDocument concept, LocalDateTime effectiveDateTime,
			MeasurementDocument measurement) {
		this.concept = concept;
		this.effectiveDateTime = effectiveDateTime;
		this.measurement = measurement;
	}

	private ProvenanceVitalSignDocument(Builder builder) {
		concept = builder.concept;
		effectiveDateTime = builder.effectiveDateTime;
		measurement = builder.measurement;
	}

	public static ProvenanceVitalSignDocument toDocument(ProvenanceVitalSign provenanceVitalSign) {
		VitalSignConceptDocument concept = Optional.ofNullable(provenanceVitalSign.getConcept())
				.map(VitalSignConceptDocument::toDocument)
				.orElse(null);
		MeasurementDocument measurement = Optional.ofNullable(provenanceVitalSign.getMeasurement())
				.map(MeasurementDocument::toDocument)
				.orElse(null);
		return ProvenanceVitalSignDocument.newBuilder()
				.withConcept(concept)
				.withEffectiveDateTime(provenanceVitalSign.getEffectiveDateTime())
				.withMeasurement(measurement)
				.build();
	}

	@Override
	public ProvenanceDataPoint restore() {
		VitalSignConcept restoredConcept = Optional.ofNullable(concept)
				.map(VitalSignConceptDocument::restore)
				.orElse(null);
		Measurement restoredMeasurement = Optional.ofNullable(measurement)
				.map(MeasurementDocument::restore)
				.orElse(null);
		return ProvenanceVitalSign.newBuilder()
				.withConcept(restoredConcept)
				.withEffectiveDateTime(effectiveDateTime)
				.withMeasurement(restoredMeasurement)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private VitalSignConceptDocument concept;
		private LocalDateTime effectiveDateTime;
		private MeasurementDocument measurement;

		private Builder() {
		}

		public Builder withConcept(VitalSignConceptDocument concept) {
			this.concept = concept;
			return this;
		}

		public Builder withEffectiveDateTime(LocalDateTime effectiveDateTime) {
			this.effectiveDateTime = effectiveDateTime;
			return this;
		}

		public Builder withMeasurement(MeasurementDocument measurement) {
			this.measurement = measurement;
			return this;
		}

		public ProvenanceVitalSignDocument build() {
			return new ProvenanceVitalSignDocument(this);
		}
	}
}
