package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.vitalsign.VitalSignObjectMother.defaultVitalSignBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.vitalsign.VitalSignObjectMother.diastolicBloodPressure
import static java.lang.Boolean.FALSE
import static java.lang.Boolean.TRUE

class IsVitalSignAvailableSpec extends Specification {

    @Unroll
    def "Projecting a vital sign to its availability"() {
        when: "I project a #description finding to its availability"
        Optional<ProjectedValue> result = new IsVitalSignAvailable().project(Optional.of(vitalSign), aProjectionContext())

        then: "A ProjectedValue #expectedValue is returned"
        result.isPresent()
        result.ifPresent {
            assert it.value == expectedValue
            assert it.unit == null
            assert it.code == vitalSign.concept.code
        }

        where:
        description                | vitalSign                                                                           || expectedValue
        "diastolic blood pressure" | diastolicBloodPressure()                                                            || TRUE
        "empty measurement"        | defaultVitalSignBuilder().withMeasurement(null).build()                             || FALSE
        "empty measurement value"  | defaultVitalSignBuilder().withMeasurement(Measurement.newBuilder().build()).build() || FALSE
    }

    def "Projecting a missing vital sign to its availability"() {
        given: "A missing vital sign"
        def vitalSign = null

        when: "I project to the measurement date"
        Optional<Boolean> result = new IsVitalSignAvailable().project(Optional.ofNullable(vitalSign), aProjectionContext())

        then: "False is returned"
        result.isPresent() == FALSE
    }
}
