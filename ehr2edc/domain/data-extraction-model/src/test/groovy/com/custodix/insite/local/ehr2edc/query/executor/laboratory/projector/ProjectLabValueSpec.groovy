package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.*
import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.LabValueField.*
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.MeasurementObjectMother.*

class ProjectLabValueSpec extends Specification {
    @Unroll
    def "Projecting the '#field' field of a lab value to its projected value"() {
        given: "An insulin lab value"
        def labValue = insulin()

        when: "I project for the value from field '#field'"
        Optional<ProjectedValue> result = new ProjectLabValue(field).project(Optional.of(labValue), aProjectionContext())

        then: "The value '#expectedValue' is projected"
        result.isPresent()
        with(result.get()) {
            it.value == expectedValue
            it.unit == INSULIN_UNIT
            it.code == INSULIN_CODE
        }

        where:
        field          || expectedValue
        SUBJECT        || INSULIN_SUBJECT_ID.id
        START_DATE     || INSULIN_START_DATE
        END_DATE       || INSULIN_END_DATE
        CODE           || INSULIN_CODE
        VALUE          || INSULIN_VALUE
        UNIT           || INSULIN_UNIT
        LOWER_LIMIT    || INSULIN_LLN
        UPPER_LIMIT    || INSULIN_ULN
        METHOD         || INSULIN_METHOD
        FASTING_STATUS || INSULIN_FASTING_STATUS
        SPECIMEN       || INSULIN_SPECIMEN
        VENDOR         || INSULIN_VENDOR
    }

    @Unroll
    def "Projecting the '#field' field of a lab value to its projected value which is empty"() {
        given: "A lab value with an empty value for the projected field '#field'"

        when: "I project for the value from field '#field'"
        Optional<ProjectedValue> result = new ProjectLabValue(field).project(Optional.ofNullable(labValue), aProjectionContext())

        then: "The result is empty"
        !result.isPresent()

        where:
        field          || labValue
        SUBJECT        || null
        SUBJECT        || aLabValueWithEmptySubject()
        START_DATE     || aLabValueWithEmptyStartDate()
        END_DATE       || aLabValueWithEmptyEndDate()
        CODE           || aLabValueWithEmptyLabConcept()
        CODE           || aLabValueWithEmptyConceptCode()
        CODE           || aLabValueWithEmptyConceptCodeValue()
        VALUE          || aLabValueWithEmptyMeasurement()
        VALUE          || aLabValueWithEmptyMeasurementValue()
        UNIT           || aLabValueWithEmptyMeasurement()
        UNIT           || aLabValueWithEmptyMeasurementUnit()
        LOWER_LIMIT    || aLabValueWithEmptyMeasurement()
        LOWER_LIMIT    || aLabValueWithEmptyMeasurementLLN()
        UPPER_LIMIT    || aLabValueWithEmptyMeasurement()
        UPPER_LIMIT    || aLabValueWithEmptyMeasurementULN()
        METHOD         || aLabValueWithEmptyLabConcept()
        METHOD         || aLabValueWithEmptyMethod()
        FASTING_STATUS || aLabValueWithEmptyLabConcept()
        FASTING_STATUS || aLabValueWithEmptyFastingStatus()
        SPECIMEN       || aLabValueWithEmptyLabConcept()
        SPECIMEN       || aLabValueWithEmptySpecimen()
        VENDOR         || aLabValueWithEmptyVendor()
    }

    @Unroll
    def "Projecting a lab value with empty unit"() {
        given: "A lab value with empty unit"

        when: "I project for the value from field 'SUBJECT'"
        Optional<ProjectedValue> measurement = new ProjectLabValue(SUBJECT).project(Optional.ofNullable(labValue), aProjectionContext())

        then: "The projected unit is empty"
        measurement.isPresent()
        with(measurement.get()) {
            value == labValue.subjectId.id
            unit == null
            code == labValue.concept.code
        }

        where:
        labValue                            | _
        aLabValueWithEmptyMeasurement()     | _
        aLabValueWithEmptyMeasurementUnit() | _
    }

    @Unroll
    def "Projecting a lab value with empty code"() {
        given: "A lab value with empty code"

        when: "I project for the value from field 'SUBJECT'"
        Optional<ProjectedValue> measurement = new ProjectLabValue(SUBJECT).project(Optional.ofNullable(labValue), aProjectionContext())

        then: "The projected code is empty"
        measurement.isPresent()
        with(measurement.get()) {
            value == labValue.subjectId.id
            unit == labValue.quantitativeResult.unit
            code == null
        }

        where:
        labValue                             | _
        aLabValueWithEmptyLabConcept()       | _
        aLabValueWithEmptyConceptCode()      | _
        aLabValueWithEmptyConceptCodeValue() | _
    }

    static aLabValueWithEmptySubject() {
        insulinLabValueBuilder()
                .forSubject(null)
                .build()
    }

    static aLabValueWithEmptyStartDate() {
        insulinLabValueBuilder()
                .withStartDate(null)
                .build()
    }

    static aLabValueWithEmptyEndDate() {
        insulinLabValueBuilder()
                .withEndDate(null)
                .build()
    }

    static aLabValueWithEmptyMeasurement() {
        insulinLabValueBuilder()
                .withQuantitativeResult(null)
                .build()
    }

    static aLabValueWithEmptyLabConcept() {
        insulinLabValueBuilder()
                .withLabConcept(null)
                .build()
    }

    static aLabValueWithEmptyConceptCode() {
        insulinLabValueBuilder()
                .withLabConcept(labConceptBuilder().withConcept(null).build())
                .build()
    }

    static aLabValueWithEmptyConceptCodeValue() {
        insulinLabValueBuilder()
                .withLabConcept(labConceptBuilder().withConcept(ConceptCode.conceptFor(null)).build())
                .build()
    }

    static aLabValueWithEmptyMeasurementValue() {
        insulinLabValueBuilder()
                .withQuantitativeResult(defaultMeasurementBuilder().withValue(null).build())
                .build()
    }

    static aLabValueWithEmptyMeasurementUnit() {
        insulinLabValueBuilder()
                .withQuantitativeResult(defaultMeasurementBuilder().withUnit(null).build())
                .build()
    }

    static aLabValueWithEmptyMeasurementLLN() {
        insulinLabValueBuilder()
                .withQuantitativeResult(defaultMeasurementBuilder().withLowerLimit(null).build())
                .build()
    }

    static aLabValueWithEmptyMeasurementULN() {
        insulinLabValueBuilder()
                .withQuantitativeResult(defaultMeasurementBuilder().withUpperLimit(null).build())
                .build()
    }

    static aLabValueWithEmptyMethod() {
        insulinLabValueBuilder()
                .withLabConcept(labConceptBuilder().withMethod(null).build())
                .build()
    }

    static aLabValueWithEmptyFastingStatus() {
        insulinLabValueBuilder()
                .withLabConcept(labConceptBuilder().withFastingStatus(null).build())
                .build()
    }

    static aLabValueWithEmptySpecimen() {
        insulinLabValueBuilder()
                .withLabConcept(labConceptBuilder().withSpecimen(null).build())
                .build()
    }

    static aLabValueWithEmptyVendor() {
        insulinLabValueBuilder()
                .withVendor(null)
                .build()
    }
}
