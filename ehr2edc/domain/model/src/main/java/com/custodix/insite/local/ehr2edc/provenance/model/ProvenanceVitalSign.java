package com.custodix.insite.local.ehr2edc.provenance.model;

import java.time.LocalDateTime;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;

public final class ProvenanceVitalSign implements ProvenanceDataPoint {
	private final VitalSignConcept concept;
	private final LocalDateTime effectiveDateTime;
	private final Measurement measurement;

	private ProvenanceVitalSign(Builder builder) {
		concept = builder.concept;
		effectiveDateTime = builder.effectiveDateTime;
		measurement = builder.measurement;
	}

	public static ProvenanceVitalSign from(VitalSign vitalSign) {
		VitalSignConcept vitalSignConcept = Optional.ofNullable(vitalSign.getVitalSignConcept())
				.map(VitalSignConcept::from)
				.orElse(null);
		Measurement measurement = Optional.ofNullable(vitalSign.getMeasurement())
				.map(Measurement::from)
				.orElse(null);
		return ProvenanceVitalSign.newBuilder()
				.withConcept(vitalSignConcept)
				.withEffectiveDateTime(vitalSign.getEffectiveDateTime())
				.withMeasurement(measurement)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public VitalSignConcept getConcept() {
		return concept;
	}

	public LocalDateTime getEffectiveDateTime() {
		return effectiveDateTime;
	}

	public Measurement getMeasurement() {
		return measurement;
	}

	public static final class Builder {
		private VitalSignConcept concept;
		private LocalDateTime effectiveDateTime;
		private Measurement measurement;

		private Builder() {
		}

		public Builder withConcept(VitalSignConcept concept) {
			this.concept = concept;
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

		public ProvenanceVitalSign build() {
			return new ProvenanceVitalSign(this);
		}
	}
}
