package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector

import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.insulinLabValueBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.MeasurementObjectMother.aMeasurement

class LabValueToMeasurementProjectorSpec extends Specification {
    def "Projecting a lab value to its measurement"() {
        given: "A lab value with a measurement"
        LabValue labValue = insulinLabValueBuilder()
                .withQuantitativeResult(labMeasurement)
                .build()

        when: "I project for the measurement"
        Optional<Measurement> measurement = new LabValueToMeasurementProjector().project(Optional.of(labValue), aProjectionContext())

        then: "The measurement is returned"
        measurement.isPresent()
        with(measurement.get()) {
            value == labMeasurement.value
            unit == labMeasurement.unit
            lowerLimit == labMeasurement.lowerLimit
            upperLimit == labMeasurement.upperLimit
        }

        where:
        labMeasurement = aMeasurement()
    }

    @Unroll
    def "Projecting an empty lab value"() {
        given: "An empty lab value"

        when: "I project for the measurement"
        Optional<Measurement> measurement = new LabValueToMeasurementProjector().project(Optional.ofNullable(labValue), aProjectionContext())

        then: "An empty result is returned"
        !measurement.isPresent()

        where:
        labValue        | _
        null            | _
        aLabValue(null) | _
    }

    static aLabValue(Measurement measurement) {
        insulinLabValueBuilder()
                .withQuantitativeResult(measurement)
                .build()
    }
}
