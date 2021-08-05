package com.custodix.insite.local.ehr2edc.provenance.model;

import java.time.LocalDateTime;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;

public final class ProvenanceLabValue implements ProvenanceDataPoint {
	private final LabConcept labConcept;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final Measurement quantitativeResult;
	private final LabValueInterpretation qualitativeResult;
	private final String vendor;

	private ProvenanceLabValue(Builder builder) {
		labConcept = builder.labConcept;
		startDate = builder.startDate;
		endDate = builder.endDate;
		quantitativeResult = builder.quantitativeResult;
		qualitativeResult = builder.qualitativeResult;
		vendor = builder.vendor;
	}

	public static ProvenanceLabValue from(LabValue labValue) {
		LabConcept labConcept = Optional.ofNullable(labValue.getLabConcept())
				.map(LabConcept::from)
				.orElse(null);
		Measurement quantitativeResult = Optional.ofNullable(labValue.getQuantitativeResult())
				.map(Measurement::from)
				.orElse(null);
		LabValueInterpretation qualitativeResult = Optional.ofNullable(labValue.getQualitativeResult())
				.map(LabValueInterpretation::from)
				.orElse(null);
		return ProvenanceLabValue.newBuilder()
				.withLabConcept(labConcept)
				.withStartDate(labValue.getStartDate())
				.withEndDate(labValue.getEndDate())
				.withQuantitativeResult(quantitativeResult)
				.withQualitativeResult(qualitativeResult)
				.withVendor(labValue.getVendor())
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public LabConcept getLabConcept() {
		return labConcept;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public Measurement getQuantitativeResult() {
		return quantitativeResult;
	}

	public LabValueInterpretation getQualitativeResult() {
		return qualitativeResult;
	}

	public String getVendor() {
		return vendor;
	}

	public static final class Builder {
		private LabConcept labConcept;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private Measurement quantitativeResult;
		private LabValueInterpretation qualitativeResult;
		private String vendor;

		private Builder() {
		}

		public Builder withLabConcept(LabConcept labConcept) {
			this.labConcept = labConcept;
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

		public Builder withQuantitativeResult(Measurement quantitativeResult) {
			this.quantitativeResult = quantitativeResult;
			return this;
		}

		public Builder withQualitativeResult(LabValueInterpretation qualitativeResult) {
			this.qualitativeResult = qualitativeResult;
			return this;
		}

		public Builder withVendor(String vendor) {
			this.vendor = vendor;
			return this;
		}

		public ProvenanceLabValue build() {
			return new ProvenanceLabValue(this);
		}
	}
}
