package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector

import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign
import spock.lang.Specification

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.MeasurementObjectMother.*
import static com.custodix.insite.local.ehr2edc.query.executor.vitalsign.VitalSignObjectMother.anEmptyVitalSign
import static com.custodix.insite.local.ehr2edc.query.executor.vitalsign.VitalSignObjectMother.diastolicBloodPressure

class VitalSignToMeasurementProjectorSpec extends Specification {
    def "Projecting a vital sign to its measurement"() {
        given: "A diastolic blood pressure finding"
        VitalSign vitalSign = diastolicBloodPressure()

        when: "I project to the measurement"
        Optional<Measurement> result = new VitalSignToMeasurementProjector().project(Optional.of(vitalSign), aProjectionContext())

        then: "The measurement is returned"
        Measurement measurement = result.get()
        and: "with value #value"
        measurement.value == value
        and: "with lower limit of normal #lowerLimit"
        measurement.lowerLimit == lowerLimit
        and: "with upper limit of normal #upperLimit"
        measurement.upperLimit == upperLimit
        and: "with unit #unit"
        measurement.unit == unit

        where:
        value = DIASTOLIC_BLOOD_PRESSURE_VALUE
        lowerLimit = DIASTOLIC_BLOOD_PRESSURE_LLN
        upperLimit = DIASTOLIC_BLOOD_PRESSURE_ULN
        unit = DIASTOLIC_BLOOD_PRESSURE_UNIT
    }

    def "Projecting an empty vital sign"() {
        given: "An empty vital sign"

        when: "I project to the measurement date"
        Optional<Measurement> result = new VitalSignToMeasurementProjector().project(Optional.ofNullable(vitalSign), aProjectionContext())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        vitalSign          | _
        null               | _
        anEmptyVitalSign() | _
    }
}
