package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.insulinLabValueBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class LabValueToStartDateProjectorSpec extends Specification {
    def "Projecting a start date"() {
        given: "A lab value with an start date of 01-01-2000"
        LabValue labValue = aLabValue(LocalDateTime.of(2000, 1, 1, 0, 0))

        when: "I project for the start date"
        Optional<LocalDateTime> startDate = new LabValueToStartDateProjector().project(Optional.of(labValue), aProjectionContext())

        then: "01-01-2000 gets returned"
        startDate.isPresent()
        startDate.get() == LocalDateTime.of(2000, 1, 1, 0, 0)
    }

    @Unroll
    def "Projecting an empty start date"() {
        given: "An empty start date"

        when: "I project for the start date"
        Optional<LocalDateTime> startDate = new LabValueToStartDateProjector().project(Optional.ofNullable(labValue), aProjectionContext())

        then: "An empty result is returned"
        !startDate.isPresent()

        where:
        labValue        | _
        null            | _
        aLabValue(null) | _
    }

    static aLabValue(LocalDateTime startDate) {
        return insulinLabValueBuilder()
                .withStartDate(startDate)
                .build()
    }
}
