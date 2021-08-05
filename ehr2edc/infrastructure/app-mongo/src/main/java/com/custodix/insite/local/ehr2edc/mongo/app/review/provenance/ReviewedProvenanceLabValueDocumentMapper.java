package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.provenance.model.*;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;

public final class ReviewedProvenanceLabValueDocumentMapper implements ReviewedProvenanceDataPointMapper {

	@Override
	public boolean supports(final ProvenanceDataPoint provenanceDataPoint) {
		return provenanceDataPoint instanceof ProvenanceLabValue;
	}

	@Override
	public ReviewedProvenanceDataPointDocument map(ProvenanceDataPoint provenanceDataPoint) {
		if (supports(provenanceDataPoint)) {
			return toDocument((ProvenanceLabValue) provenanceDataPoint);
		}
		throw new SystemException("Mapper does not support mapping of " + provenanceDataPoint);
	}

	private  ReviewedProvenanceLabValueDocument toDocument(ProvenanceLabValue provenanceLabValue) {
		return ReviewedProvenanceLabValueDocument.newBuilder()
				.withLabConcept(getLabConcept(provenanceLabValue))
				.withStartDate(provenanceLabValue.getStartDate())
				.withEndDate(provenanceLabValue.getEndDate())
				.withQuantitativeResult(getQuantitativeResult(provenanceLabValue))
				.withQualitativeResult(getQualitativeResult(provenanceLabValue))
				.withVendor(provenanceLabValue.getVendor())
				.build();
	}

	private ReviewedLabValueInterpretationDocument getQualitativeResult(final ProvenanceLabValue provenanceLabValue) {
		return Optional.ofNullable(
				provenanceLabValue.getQualitativeResult())
				.map(this::map)
				.orElse(null);
	}

	private ReviewedMeasurementDocument getQuantitativeResult(final ProvenanceLabValue provenanceLabValue) {
		return Optional.ofNullable(provenanceLabValue.getQuantitativeResult())
				.map(this::map)
				.orElse(null);
	}

	private ReviewedLabConceptDocument getLabConcept(final ProvenanceLabValue provenanceLabValue) {
		return Optional.ofNullable(provenanceLabValue.getLabConcept())
				.map(this::map)
				.orElse(null);
	}

	private ReviewedMeasurementDocument map(Measurement quantitativeResult) {
		return ReviewedMeasurementDocument.newBuilder()
				.withLowerLimit(quantitativeResult.getLowerLimit())
				.withUpperLimit(quantitativeResult.getUpperLimit())
				.withValue(quantitativeResult.getValue())
				.withUnit(quantitativeResult.getUnit())
				.build();
	}

	private ReviewedLabConceptDocument map(LabConcept labConcept) {
		ReviewedConceptCodeDocument concept = Optional.ofNullable(labConcept.getConcept())
				.map(this::map)
				.orElse(null);
		return ReviewedLabConceptDocument.newBuilder()
				.withConcept(concept)
				.withComponent(labConcept.getComponent())
				.withMethod(labConcept.getMethod())
				.withFastingStatus(labConcept.getFastingStatus())
				.withSpecimen(labConcept.getSpecimen())
				.build();
	}

	private ReviewedConceptCodeDocument map(ConceptCode reviewedConceptCode) {
		return ReviewedConceptCodeDocument.of(reviewedConceptCode.getCode());
	}

	private ReviewedLabValueInterpretationDocument map(LabValueInterpretation qualitativeResult) {
		return ReviewedLabValueInterpretationDocument.newBuilder()
				.withParsedInterpretation(qualitativeResult.getParsedInterpretation())
				.withOriginalInterpretation(qualitativeResult.getOriginalInterpretation())
				.build();
	}
}
