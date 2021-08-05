package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement

import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.MeasurementObjectMother.aMeasurement

class MeasurementToValueProjectorSpec extends Specification {
    def "Projecting a measurement to its value"() {
        given: "A measurement"
        Measurement labMeasurement = aMeasurement()

        when: "I project for the value"
        Optional<BigDecimal> value = new MeasurementToValueProjector().project(Optional.of(labMeasurement), aProjectionContext())

        then: "The value is returned"
        value.isPresent()
        value.get() == labMeasurement.value
    }

    @Unroll
    def "Projecting an empty measurement value"() {
        given: "An empty measurement"

        when: "I project for the value"
        Optional<BigDecimal> value = new MeasurementToValueProjector().project(Optional.ofNullable(labMeasurement), aProjectionContext())

        then: "An empty result is returned"
        !value.isPresent()

        where:
        labMeasurement                   | _
        null                             | _
        Measurement.newBuilder().build() | _
    }
}
