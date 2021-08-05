package com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model;

import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import java.time.LocalDateTime;

import static com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabConceptObjectMother.anInsulinLabConceptBuilder;
import static com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabMeasurementObjectMother.insulinMeasurement;
import static com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabValueInterpretationObjectMother.aDefaultLabValueInterpretation;

public class LabValueDocumentObjectMother {

	private static final LocalDateTime NOW = LocalDateTime.now();
	private static final LocalDateTime YESTERDAY = NOW.minusDays(1);

	private static final SubjectId INSULIN_SUBJECT_ID = SubjectId.of("123456");
	public static final LocalDateTime INSULIN_START_DATE = LocalDateTime.of(2019, 7, 8, 13, 35);
	public static final LocalDateTime INSULIN_END_DATE = LocalDateTime.of(2019, 7, 9, 13, 35);
	public static final String INSULIN_VENDOR = "Lab";

	public static LabValueDocument.Builder insulinLabValueDocumentBuilder() {
		return LabValueDocument.newBuilder()
				.forSubject(INSULIN_SUBJECT_ID)
				.withLabConcept(anInsulinLabConceptBuilder().build())
				.withQuantitativeResult(insulinMeasurement())
				.withStartDate(INSULIN_START_DATE)
				.withEndDate(INSULIN_END_DATE)
				.withVendor(INSULIN_VENDOR);
	}

	public static LabValueDocument.Builder aDefaultLabValueDocumentBuilder() {
		return LabValueDocument.newBuilder()
				.forSubject(SubjectId.of("LabValueDocument.SubjectId"))
				.withStartDate(YESTERDAY)
				.withEndDate(NOW)
				.withLabConcept(LabConceptObjectMother.aDefaultLabConcept())
				.withVendor("vendor")
				.withQualitativeResult(aDefaultLabValueInterpretation())
				.withQuantitativeResult(LabMeasurementObjectMother.aDefaultLabMeasurement());
	}

}