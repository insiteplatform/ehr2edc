package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;

import com.custodix.insite.local.ehr2edc.provenance.model.*;

@TypeAlias("ReviewedProvenanceLaboratory")
final class ReviewedProvenanceLabValueDocument implements ReviewedProvenanceDataPointDocument {
	private final ReviewedLabConceptDocument labConcept;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final ReviewedMeasurementDocument quantitativeResult;
	private final ReviewedLabValueInterpretationDocument qualitativeResult;
	private final String vendor;

	@PersistenceConstructor
	private ReviewedProvenanceLabValueDocument(ReviewedLabConceptDocument labConcept, LocalDateTime startDate,
			LocalDateTime endDate, ReviewedMeasurementDocument quantitativeResult,
			ReviewedLabValueInterpretationDocument qualitativeResult, String vendor) {
		this.labConcept = labConcept;
		this.startDate = startDate;
		this.endDate = endDate;
		this.quantitativeResult = quantitativeResult;
		this.qualitativeResult = qualitativeResult;
		this.vendor = vendor;
	}

	private ReviewedProvenanceLabValueDocument(Builder builder) {
		labConcept = builder.labConcept;
		startDate = builder.startDate;
		endDate = builder.endDate;
		quantitativeResult = builder.quantitativeResult;
		qualitativeResult = builder.qualitativeResult;
		vendor = builder.vendor;
	}

	@Override
	public ProvenanceDataPoint restore() {
		return ProvenanceLabValue.newBuilder()
				.withLabConcept(getRestoredLabConcept())
				.withStartDate(startDate)
				.withEndDate(endDate)
				.withQuantitativeResult(getRestoredQuantitativeResult())
				.withQualitativeResult(getRestoredQualitativeResult())
				.withVendor(vendor)
				.build();
	}

	private LabValueInterpretation getRestoredQualitativeResult() {
		return Optional.ofNullable(qualitativeResult)
				.map(ReviewedLabValueInterpretationDocument::restore)
				.orElse(null);
	}

	private Measurement getRestoredQuantitativeResult() {
		return Optional.ofNullable(quantitativeResult)
				.map(ReviewedMeasurementDocument::restore)
				.orElse(null);
	}

	private LabConcept getRestoredLabConcept() {
		return Optional.ofNullable(labConcept)
				.map(ReviewedLabConceptDocument::restore)
				.orElse(null);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private ReviewedLabConceptDocument labConcept;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private ReviewedMeasurementDocument quantitativeResult;
		private ReviewedLabValueInterpretationDocument qualitativeResult;
		private String vendor;

		private Builder() {
		}

		public Builder withLabConcept(ReviewedLabConceptDocument labConcept) {
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

		public Builder withQuantitativeResult(ReviewedMeasurementDocument quantitativeResult) {
			this.quantitativeResult = quantitativeResult;
			return this;
		}

		public Builder withQualitativeResult(ReviewedLabValueInterpretationDocument qualitativeResult) {
			this.qualitativeResult = qualitativeResult;
			return this;
		}

		public Builder withVendor(String vendor) {
			this.vendor = vendor;
			return this;
		}

		public ReviewedProvenanceLabValueDocument build() {
			return new ReviewedProvenanceLabValueDocument(this);
		}
	}
}
