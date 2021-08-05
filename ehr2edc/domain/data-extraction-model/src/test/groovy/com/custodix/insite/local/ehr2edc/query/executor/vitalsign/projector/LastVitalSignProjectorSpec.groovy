package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector


import spock.lang.Specification

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.vitalsign.VitalSignObjectMother.anEmptyVitalSign
import static com.custodix.insite.local.ehr2edc.query.executor.vitalsign.VitalSignObjectMother.defaultVitalSignBuilder

class LastVitalSignProjectorSpec extends Specification {

    def "Deprecated LastVitalSignProjector act as Identity-function"() {
        expect: "The result of the projector to equal the passed Result"
        vitalSign == new LastVitalSignProjector().project(vitalSign, aProjectionContext())

        where:
        vitalSign << [
                null,
                Optional.empty(),
                Optional.of(anEmptyVitalSign()),
                Optional.of(defaultVitalSignBuilder().build())
        ]
    }
}
