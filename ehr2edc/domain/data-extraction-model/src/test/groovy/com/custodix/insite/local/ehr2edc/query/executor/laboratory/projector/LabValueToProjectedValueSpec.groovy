package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.insulinLabValueBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.MeasurementObjectMother.aMeasurement

class LabValueToProjectedValueSpec extends Specification {
    def "Projecting a lab value to its projected value"() {
        given: "A lab value with a measurement"
        LabValue labValue = aLabValue(labMeasurement)

        when: "I project for the projected value"
        Optional<ProjectedValue> measurement = new LabValueToProjectedValue().project(Optional.of(labValue), aProjectionContext())

        then: "The projected value is returned"
        measurement.isPresent()
        with(measurement.get()) {
            value == labMeasurement.value
            unit == labMeasurement.unit
            code == labValue.concept.code
        }

        where:
        labMeasurement = aMeasurement()
    }

    @Unroll
    def "Projecting a lab value with empty measurement"() {
        given: "A lab value with empty measurement"

        when: "I project for the projected value"
        Optional<ProjectedValue> measurement = new LabValueToProjectedValue().project(Optional.ofNullable(labValue), aProjectionContext())

        then: "The projected value value and unit is empty"
        measurement.isPresent()
        with(measurement.get()) {
            value == null
            unit == null
            code == labValue.concept.code
        }

        where:
        labValue                        | _
        aLabValueWithEmptyMeasurement() | _
    }

    @Unroll
    def "Projecting a lab value with empty code"() {
        given: "A lab value with empty code"

        when: "I project for the projected value"
        Optional<ProjectedValue> measurement = new LabValueToProjectedValue().project(Optional.ofNullable(labValue), aProjectionContext())

        then: "The projected value code is empty"
        measurement.isPresent()
        with(measurement.get()) {
            value == labValue.quantitativeResult.value
            unit == labValue.quantitativeResult.unit
            code == null
        }

        where:
        labValue                             | _
        aLabValueWithEmptyLabConcept()       | _
        aLabValueWithEmptyConceptCode()      | _
        aLabValueWithEmptyConceptCodeValue() | _
    }

    @Unroll
    def "Projecting a lab value with empty measurement and code"() {
        given: "A lab value with empty measurement and code"

        when: "I project for the projected value"
        Optional<ProjectedValue> measurement = new LabValueToProjectedValue().project(Optional.ofNullable(labValue), aProjectionContext())

        then: "An empty result is returned"
        !measurement.isPresent()

        where:
        labValue | _
        null     | _
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
                .withLabConcept(LabConcept.newBuilder().withConcept(null).build())
                .build()
    }

    static aLabValueWithEmptyConceptCodeValue() {
        insulinLabValueBuilder()
                .withLabConcept(LabConcept.newBuilder().withConcept(ConceptCode.conceptFor(null)).build())
                .build()
    }

    static aLabValue(Measurement measurement) {
        insulinLabValueBuilder()
                .withQuantitativeResult(measurement)
                .build()
    }
}
