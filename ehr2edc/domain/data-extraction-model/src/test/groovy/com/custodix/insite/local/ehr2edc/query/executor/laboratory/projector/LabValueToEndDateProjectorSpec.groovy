package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.insulinLabValueBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class LabValueToEndDateProjectorSpec extends Specification {
    def "Projecting a end date"() {
        given: "A lab value with an end date of 01-01-2010"
        LabValue labValue = aLabValue(LocalDateTime.of(2010, 1, 1, 0, 0))

        when: "I project for the end date"
        Optional<LocalDateTime> endDate = new LabValueToEndDateProjector().project(Optional.of(labValue), aProjectionContext())

        then: "01-01-2010 gets returned"
        endDate.isPresent()
        endDate.get() == LocalDateTime.of(2010, 1, 1, 0, 0)
    }

    @Unroll
    def "Projecting an empty end date"() {
        given: "An empty end date"

        when: "I project for the end date"
        Optional<LocalDateTime> endDate = new LabValueToEndDateProjector().project(Optional.ofNullable(labValue), aProjectionContext())

        then: "An empty result is returned"
        !endDate.isPresent()

        where:
        labValue        | _
        null            | _
        aLabValue(null) | _
    }

    static aLabValue(LocalDateTime endDate) {
        return insulinLabValueBuilder()
                .withEndDate(endDate)
                .build()
    }
}
