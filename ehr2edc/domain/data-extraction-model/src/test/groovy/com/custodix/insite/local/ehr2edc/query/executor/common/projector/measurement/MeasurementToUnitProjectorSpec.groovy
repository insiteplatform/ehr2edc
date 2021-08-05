package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement

import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.MeasurementObjectMother.aMeasurement

class MeasurementToUnitProjectorSpec extends Specification {
    def "Projecting a measurement to its unit"() {
        given: "A measurement"
        Measurement labMeasurement = aMeasurement()

        when: "I project for the unit"
        Optional<String> unit = new MeasurementToUnitProjector().project(Optional.of(labMeasurement), aProjectionContext())

        then: "The unit is returned"
        unit.isPresent()
        unit.get() == labMeasurement.unit
    }

    @Unroll
    def "Projecting an empty measurement unit"() {
        given: "An empty measurement"

        when: "I project for the unit"
        Optional<String> unit = new MeasurementToUnitProjector().project(Optional.ofNullable(labMeasurement), aProjectionContext())

        then: "An empty result is returned"
        !unit.isPresent()

        where:
        labMeasurement                   | _
        null                             | _
        Measurement.newBuilder().build() | _
    }
}
