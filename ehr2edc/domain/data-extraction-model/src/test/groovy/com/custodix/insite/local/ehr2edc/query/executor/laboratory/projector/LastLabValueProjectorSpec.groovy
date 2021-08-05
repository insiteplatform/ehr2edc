package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import spock.lang.Specification

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.insulin
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class LastLabValueProjectorSpec extends Specification {

    def "Deprecated LastLabValueProjector act as Identity-function"() {
        expect: "The result of the projector to equal the passed Result"
        labValue == new LastLabValueProjector().project(labValue, aProjectionContext())

        where:
        labValue << [
                null,
                Optional.empty(),
                Optional.of(insulin()),
                Optional.of(LabValue.newBuilder().build())
        ]
    }
}
