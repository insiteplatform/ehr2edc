package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import static com.custodix.insite.local.ehr2edc.provenance.model.FastingStatus.FASTING;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.custodix.insite.local.ehr2edc.provenance.model.FastingStatus;

public class ReviewedProvenanceLabValueDocumentObjectMother {

	public static final LocalDateTime INSULIN_START_DATE = LocalDateTime.of(2019, 7, 8, 13, 35);
	public static final LocalDateTime INSULIN_END_DATE = LocalDateTime.of(2019, 7, 9, 13, 35);
	public static final String INSULIN_CODE = "INSULIN";
	public static final String INSULIN_COMPONENT = "Component for insulin";
	public static final String INSULIN_SPECIMEN = "Blood";
	public static final String INSULIN_METHOD = "Radioimmunoassay";
	public static final FastingStatus INSULIN_FASTING_STATUS = FASTING;
	public static final String INSULIN_VENDOR = "Lab";
	public static final BigDecimal INSULIN_VALUE = new BigDecimal("85");
	public static final BigDecimal INSULIN_LLN = new BigDecimal("75");
	public static final BigDecimal INSULIN_ULN = new BigDecimal("100");
	public static final String INSULIN_UNIT = "mg/dL";
	public static final Integer INSULIN_QUALITATIVE_PARSED = 1;
	public static final String INSULIN_QUALITATIVE_ORIGINAL = "positive";

	public static ReviewedProvenanceLabValueDocument aDefaultReviewedProvenanceLabValueDocument() {
		return aDefaultReviewedProvenanceLabValueDocumentBuilder()
				.build();
	}

	private static ReviewedProvenanceLabValueDocument.Builder aDefaultReviewedProvenanceLabValueDocumentBuilder() {
		return ReviewedProvenanceLabValueDocument.newBuilder()
				.withLabConcept(ReviewedLabConceptDocument.newBuilder()
						.withConcept(ReviewedConceptCodeDocument.of(INSULIN_CODE))
						.withComponent(INSULIN_COMPONENT)
						.withMethod(INSULIN_METHOD)
						.withFastingStatus(INSULIN_FASTING_STATUS)
						.withSpecimen(INSULIN_SPECIMEN)
						.build())
				.withStartDate(INSULIN_START_DATE)
				.withEndDate(INSULIN_END_DATE)
				.withQuantitativeResult(ReviewedMeasurementDocument.newBuilder()
						.withLowerLimit(INSULIN_LLN)
						.withUpperLimit(INSULIN_ULN)
						.withValue(INSULIN_VALUE)
						.withUnit(INSULIN_UNIT)
						.build())
				.withQualitativeResult(ReviewedLabValueInterpretationDocument.newBuilder()
						.withParsedInterpretation(INSULIN_QUALITATIVE_PARSED)
						.withOriginalInterpretation(INSULIN_QUALITATIVE_ORIGINAL)
						.build())
				.withVendor(INSULIN_VENDOR);
	}
}
