package com.custodix.insite.local.ehr2edc.query.executor.laboratory

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.FastingStatus
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValueInterpretation
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId

import java.time.LocalDateTime

import static com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode.conceptFor
import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.FastingStatus.FASTING
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.MeasurementObjectMother.insulinMeasurement

class LabValueObjectMother {
    public static final SubjectId INSULIN_SUBJECT_ID = SubjectId.of("123456")
    public static final LocalDateTime INSULIN_START_DATE = LocalDateTime.of(2019, 7, 8, 13, 35)
    public static final LocalDateTime INSULIN_END_DATE = LocalDateTime.of(2019, 7, 9, 13, 35)
    public static final String INSULIN_CODE = "INSULIN"
    public static final String INSULIN_COMPONENT = "Component for insulin"
    public static final String INSULIN_SPECIMEN = "Blood"
    public static final String INSULIN_METHOD = "Radioimmunoassay"
    public static final FastingStatus INSULIN_FASTING_STATUS = FASTING
    public static final String INSULIN_VENDOR = "Lab"

    static LabValue insulin() {
        insulinLabValueBuilder().build()
    }

    static LabValue.Builder insulinLabValueBuilder() {
        LabConcept labConcept = labConceptBuilder().build()
        LabValue.newBuilder()
                .forSubject(INSULIN_SUBJECT_ID)
                .withLabConcept(labConcept)
                .withQuantitativeResult(insulinMeasurement())
                .withStartDate(INSULIN_START_DATE)
                .withEndDate(INSULIN_END_DATE)
                .withVendor(INSULIN_VENDOR)
    }

    static LabConcept.Builder labConceptBuilder() {
        LabConcept.newBuilder()
                .withConcept(conceptFor(INSULIN_CODE))
                .withComponent(INSULIN_COMPONENT)
                .withSpecimen(INSULIN_SPECIMEN)
                .withMethod(INSULIN_METHOD)
                .withFastingStatus(INSULIN_FASTING_STATUS)
    }

    static LabValueInterpretation anInterpretation() {
        return LabValueInterpretation.newBuilder()
                .withOriginalInterpretation("positive")
                .withParsedInterpretation(1)
                .build()
    }
}
