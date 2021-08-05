package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement

import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.MeasurementObjectMother.aMeasurement

class MeasurementToUpperLimitProjectorSpec extends Specification {
    def "Projecting a measurement to its upper limit of normal"() {
        given: "A measurement"
        Measurement labMeasurement = aMeasurement()

        when: "I project for the upper limit of normal"
        Optional<BigDecimal> upperLimit = new MeasurementToUpperLimitProjector().project(Optional.of(labMeasurement), aProjectionContext())

        then: "The upper limit of normal is returned"
        upperLimit.isPresent()
        upperLimit.get() == labMeasurement.upperLimit
    }

    @Unroll
    def "Projecting an empty measurement upper limit of normal"() {
        given: "An empty measurement"

        when: "I project for the upper limit of normal"
        Optional<BigDecimal> upperLimit = new MeasurementToUpperLimitProjector().project(Optional.ofNullable(labMeasurement), aProjectionContext())

        then: "An empty result is returned"
        !upperLimit.isPresent()

        where:
        labMeasurement                   | _
        null                             | _
        Measurement.newBuilder().build() | _
    }
}
