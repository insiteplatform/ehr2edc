package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.MeasurementObjectMother.aMeasurement
import static com.custodix.insite.local.ehr2edc.query.executor.vitalsign.VitalSignObjectMother.defaultVitalSignBuilder

class VitalSignToProjectedValueSpec extends Specification {
    def "Projecting a vital sign to its projected value"() {
        given: "A vital sign with a measurement"
        VitalSign vitalSign = aVitalSign(vitalSignMeasurement)

        when: "I project for the projected value"
        Optional<ProjectedValue> measurement = new VitalSignToProjectedValue().project(Optional.of(vitalSign), aProjectionContext())

        then: "The projected value is returned"
        measurement.isPresent()
        with(measurement.get()) {
            value == vitalSign.measurement.value
            unit == vitalSign.measurement.unit
            code == vitalSign.concept.code
        }

        where:
        vitalSignMeasurement = aMeasurement()
    }

    @Unroll
    def "Projecting a vital sign with empty measurement"() {
        given: "A vital sign with empty measurement"

        when: "I project for the projected value"
        Optional<ProjectedValue> measurement = new VitalSignToProjectedValue().project(Optional.ofNullable(vitalSign), aProjectionContext())

        then: "The projected value value and unit is empty"
        measurement.isPresent()
        with(measurement.get()) {
            value == null
            unit == null
            code == vitalSign.concept.code
        }

        where:
        vitalSign                        | _
        aVitalSignWithEmptyMeasurement() | _
    }

    @Unroll
    def "Projecting a vital sign with empty code"() {
        given: "A vital sign with empty code"

        when: "I project for the projected value"
        Optional<ProjectedValue> measurement = new VitalSignToProjectedValue().project(Optional.ofNullable(vitalSign), aProjectionContext())

        then: "The projected value code is empty"
        measurement.isPresent()
        with(measurement.get()) {
            value == vitalSign.measurement.value
            unit == vitalSign.measurement.unit
            code == null
        }

        where:
        vitalSign                             | _
        aVitalSignWithEmptyLabConcept()       | _
        aVitalSignWithEmptyConceptCode()      | _
        aVitalSignWithEmptyConceptCodeValue() | _
    }

    @Unroll
    def "Projecting a vital sign with empty measurement and code"() {
        given: "A vital sign with empty measurement and code"

        when: "I project for the projected value"
        Optional<ProjectedValue> measurement = new VitalSignToProjectedValue().project(Optional.ofNullable(vitalSign), aProjectionContext())

        then: "An empty result is returned"
        !measurement.isPresent()

        where:
        vitalSign | _
        null      | _
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

    static aVitalSign(Measurement measurement) {
        defaultVitalSignBuilder()
                .withMeasurement(measurement)
                .build()
    }
}
