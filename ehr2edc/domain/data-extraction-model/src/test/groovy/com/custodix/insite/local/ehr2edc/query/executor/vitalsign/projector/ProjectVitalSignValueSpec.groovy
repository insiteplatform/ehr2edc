package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.MeasurementObjectMother.*
import static com.custodix.insite.local.ehr2edc.query.executor.vitalsign.VitalSignObjectMother.*
import static com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.VitalSignField.*

class ProjectVitalSignValueSpec extends Specification {
    @Unroll
    def "Projecting the '#field' field of a vital sign to its projected value"() {
        given: "A diastolic blood pressure vital sign"
        def vitalSign = diastolicBloodPressure()

        when: "I project for the value from field '#field'"
        Optional<ProjectedValue> result = new ProjectVitalSignValue(field).project(Optional.of(vitalSign), aProjectionContext())

        then: "The value '#expectedValue' is projected"
        result.isPresent()
        with(result.get()) {
            it.value == expectedValue
            it.unit == DIASTOLIC_BLOOD_PRESSURE_UNIT
            it.code == DIASTOLIC_BLOOD_PRESSURE_CODE
        }

        where:
        field       || expectedValue
        SUBJECT     || SUBJECT_ID.id
        DATE        || TIMESTAMP
        CODE        || DIASTOLIC_BLOOD_PRESSURE_CODE
        VALUE       || DIASTOLIC_BLOOD_PRESSURE_VALUE
        UNIT        || DIASTOLIC_BLOOD_PRESSURE_UNIT
        LOWER_LIMIT || DIASTOLIC_BLOOD_PRESSURE_LLN
        UPPER_LIMIT || DIASTOLIC_BLOOD_PRESSURE_ULN
        LOCATION    || LOCATION_ARM
        LATERALITY  || LATERALITY_LEFT
        POSITION    || POSITION_SITTING
    }

    @Unroll
    def "Projecting the '#field' field of a vital sign to its projected value which is empty"() {
        given: "A vital sign with an empty value for the projected field '#field'"

        when: "I project for the value from field '#field'"
        Optional<ProjectedValue> result = new ProjectVitalSignValue(field).project(Optional.ofNullable(vitalSign), aProjectionContext())

        then: "The result is empty"
        !result.isPresent()

        where:
        field       || vitalSign
        SUBJECT     || null
        SUBJECT     || aVitalSignWithEmptySubject()
        DATE        || aVitalSignWithEmptyDate()
        CODE        || aVitalSignWithEmptyLabConcept()
        CODE        || aVitalSignWithEmptyConceptCode()
        CODE        || aVitalSignWithEmptyConceptCodeValue()
        VALUE       || aVitalSignWithEmptyMeasurement()
        VALUE       || aVitalSignWithEmptyMeasurementValue()
        UNIT        || aVitalSignWithEmptyMeasurement()
        UNIT        || aVitalSignWithEmptyMeasurementUnit()
        LOWER_LIMIT || aVitalSignWithEmptyMeasurement()
        LOWER_LIMIT || aVitalSignWithEmptyMeasurementLLN()
        UPPER_LIMIT || aVitalSignWithEmptyMeasurement()
        UPPER_LIMIT || aVitalSignWithEmptyMeasurementULN()
        LOCATION    || aVitalSignWithEmptyLabConcept()
        LOCATION    || aVitalSignWithEmptyLocation()
        LATERALITY  || aVitalSignWithEmptyLabConcept()
        LATERALITY  || aVitalSignWithEmptyLaterality()
        POSITION    || aVitalSignWithEmptyLabConcept()
        POSITION    || aVitalSignWithEmptyPosition()
    }

    @Unroll
    def "Projecting a vital sign with empty unit"() {
        given: "A vital sign with empty unit"

        when: "I project for the value from field 'SUBJECT'"
        Optional<ProjectedValue> measurement = new ProjectVitalSignValue(SUBJECT).project(Optional.ofNullable(vitalSign), aProjectionContext())

        then: "The projected unit is empty"
        measurement.isPresent()
        with(measurement.get()) {
            value == vitalSign.subjectId.id
            unit == null
            code == vitalSign.concept.code
        }

        where:
        vitalSign                            | _
        aVitalSignWithEmptyMeasurement()     | _
        aVitalSignWithEmptyMeasurementUnit() | _
    }

    @Unroll
    def "Projecting a vital sign with empty code"() {
        given: "A vital sign with empty code"

        when: "I project for the value from field 'SUBJECT'"
        Optional<ProjectedValue> measurement = new ProjectVitalSignValue(SUBJECT).project(Optional.ofNullable(vitalSign), aProjectionContext())

        then: "The projected code is empty"
        measurement.isPresent()
        with(measurement.get()) {
            value == vitalSign.subjectId.id
            unit == vitalSign.measurement.unit
            code == null
        }

        where:
        vitalSign                             | _
        aVitalSignWithEmptyLabConcept()       | _
        aVitalSignWithEmptyConceptCode()      | _
        aVitalSignWithEmptyConceptCodeValue() | _
    }

    static aVitalSignWithEmptySubject() {
        defaultVitalSignBuilder()
                .withSubjectId(null)
                .build()
    }

    static aVitalSignWithEmptyDate() {
        defaultVitalSignBuilder()
                .withEffectiveDateTime(null)
                .build()
    }

    static aVitalSignWithEmptyMeasurement() {
        defaultVitalSignBuilder()
                .withMeasurement(null)
                .build()
    }

    static aVitalSignWithEmptyLabConcept() {
        defaultVitalSignBuilder()
                .withConcept(null)
                .build()
    }

    static aVitalSignWithEmptyConceptCode() {
        defaultVitalSignBuilder()
                .withConcept(VitalSignConcept.newBuilder().withConcept(null).build())
                .build()
    }

    static aVitalSignWithEmptyConceptCodeValue() {
        defaultVitalSignBuilder()
                .withConcept(VitalSignConcept.newBuilder().withConcept(ConceptCode.conceptFor(null)).build())
                .build()
    }

    static aVitalSignWithEmptyMeasurementValue() {
        defaultVitalSignBuilder()
                .withMeasurement(defaultMeasurementBuilder().withValue(null).build())
                .build()
    }

    static aVitalSignWithEmptyMeasurementUnit() {
        defaultVitalSignBuilder()
                .withMeasurement(defaultMeasurementBuilder().withUnit(null).build())
                .build()
    }

    static aVitalSignWithEmptyMeasurementLLN() {
        defaultVitalSignBuilder()
                .withMeasurement(defaultMeasurementBuilder().withLowerLimit(null).build())
                .build()
    }

    static aVitalSignWithEmptyMeasurementULN() {
        defaultVitalSignBuilder()
                .withMeasurement(defaultMeasurementBuilder().withUpperLimit(null).build())
                .build()
    }

    static aVitalSignWithEmptyLocation() {
        defaultVitalSignBuilder()
                .withConcept(defaultVitalSignConceptBuilder().withLocation(null).build())
                .build()
    }

    static aVitalSignWithEmptyLaterality() {
        defaultVitalSignBuilder()
                .withConcept(defaultVitalSignConceptBuilder().withLaterality(null).build())
                .build()
    }

    static aVitalSignWithEmptyPosition() {
        defaultVitalSignBuilder()
                .withConcept(defaultVitalSignConceptBuilder().withPosition(null).build())
                .build()
    }
}
