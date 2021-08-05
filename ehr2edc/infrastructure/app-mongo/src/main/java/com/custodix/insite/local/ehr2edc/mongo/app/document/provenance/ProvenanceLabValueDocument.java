package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;

import com.custodix.insite.local.ehr2edc.provenance.model.*;

@TypeAlias("ProvenanceLaboratory")
public final class ProvenanceLabValueDocument implements ProvenanceDataPointDocument {
	private final LabConceptDocument labConcept;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final MeasurementDocument quantitativeResult;
	private final LabValueInterpretationDocument qualitativeResult;
	private final String vendor;

	@PersistenceConstructor
	private ProvenanceLabValueDocument(LabConceptDocument labConcept, LocalDateTime startDate,
			LocalDateTime endDate, MeasurementDocument quantitativeResult,
			LabValueInterpretationDocument qualitativeResult, String vendor) {
		this.labConcept = labConcept;
		this.startDate = startDate;
		this.endDate = endDate;
		this.quantitativeResult = quantitativeResult;
		this.qualitativeResult = qualitativeResult;
		this.vendor = vendor;
	}

	private ProvenanceLabValueDocument(Builder builder) {
		labConcept = builder.labConcept;
		startDate = builder.startDate;
		endDate = builder.endDate;
		quantitativeResult = builder.quantitativeResult;
		qualitativeResult = builder.qualitativeResult;
		vendor = builder.vendor;
	}

	public static ProvenanceLabValueDocument toDocument(ProvenanceLabValue provenanceLabValue) {
		LabConceptDocument labConcept = Optional.ofNullable(provenanceLabValue.getLabConcept())
				.map(LabConceptDocument::toDocument)
				.orElse(null);
		MeasurementDocument quantitativeResult = Optional.ofNullable(provenanceLabValue.getQuantitativeResult())
				.map(MeasurementDocument::toDocument)
				.orElse(null);
		LabValueInterpretationDocument qualitativeResult = Optional.ofNullable(
				provenanceLabValue.getQualitativeResult())
				.map(LabValueInterpretationDocument::toDocument)
				.orElse(null);
		return ProvenanceLabValueDocument.newBuilder()
				.withLabConcept(labConcept)
				.withStartDate(provenanceLabValue.getStartDate())
				.withEndDate(provenanceLabValue.getEndDate())
				.withQuantitativeResult(quantitativeResult)
				.withQualitativeResult(qualitativeResult)
				.withVendor(provenanceLabValue.getVendor())
				.build();
	}

	@Override
	public ProvenanceDataPoint restore() {
		LabConcept restoredConcept = Optional.ofNullable(labConcept)
				.map(LabConceptDocument::restore)
				.orElse(null);
		Measurement restoredQuantitativeResult = Optional.ofNullable(quantitativeResult)
				.map(MeasurementDocument::restore)
				.orElse(null);
		LabValueInterpretation restoredQualitativeResult = Optional.ofNullable(qualitativeResult)
				.map(LabValueInterpretationDocument::restore)
				.orElse(null);
		return ProvenanceLabValue.newBuilder()
				.withLabConcept(restoredConcept)
				.withStartDate(startDate)
				.withEndDate(endDate)
				.withQuantitativeResult(restoredQuantitativeResult)
				.withQualitativeResult(restoredQualitativeResult)
				.withVendor(vendor)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private LabConceptDocument labConcept;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
		private MeasurementDocument quantitativeResult;
		private LabValueInterpretationDocument qualitativeResult;
		private String vendor;

		private Builder() {
		}

		public Builder withLabConcept(LabConceptDocument labConcept) {
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

		public Builder withQuantitativeResult(MeasurementDocument quantitativeResult) {
			this.quantitativeResult = quantitativeResult;
			return this;
		}

		public Builder withQualitativeResult(LabValueInterpretationDocument qualitativeResult) {
			this.qualitativeResult = qualitativeResult;
			return this;
		}

		public Builder withVendor(String vendor) {
			this.vendor = vendor;
			return this;
		}

		public ProvenanceLabValueDocument build() {
			return new ProvenanceLabValueDocument(this);
		}
	}
}
