package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement

import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.MeasurementObjectMother.aMeasurement

class MeasurementToLowerLimitProjectorSpec extends Specification {
    def "Projecting a measurement to its lower limit of normal"() {
        given: "A measurement"
        Measurement labMeasurement = aMeasurement()

        when: "I project for the lower limit of normal"
        Optional<BigDecimal> lowerLimit = new MeasurementToLowerLimitProjector().project(Optional.of(labMeasurement), aProjectionContext())

        then: "The lower limit of normal is returned"
        lowerLimit.isPresent()
        lowerLimit.get() == labMeasurement.lowerLimit
    }

    @Unroll
    def "Projecting an empty measurement lower limit of normal"() {
        given: "An empty measurement"

        when: "I project for the lower limit of normal"
        Optional<BigDecimal> lowerLimit = new MeasurementToLowerLimitProjector().project(Optional.ofNullable(labMeasurement), aProjectionContext())

        then: "An empty result is returned"
        !lowerLimit.isPresent()

        where:
        labMeasurement                   | _
        null                             | _
        Measurement.newBuilder().build() | _
    }
}
