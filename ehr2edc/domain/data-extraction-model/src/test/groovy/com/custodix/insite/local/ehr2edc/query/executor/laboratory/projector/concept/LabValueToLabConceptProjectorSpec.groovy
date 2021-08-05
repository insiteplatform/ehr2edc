package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode.conceptFor
import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.insulinLabValueBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.labConceptBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class LabValueToLabConceptProjectorSpec extends Specification {
    def "Projecting a lab value to its concept"() {
        given: "A lab value with code INSULIN"
        LabConcept labConcept = labConceptBuilder()
                .withConcept(conceptFor("INSULIN"))
                .build()
        LabValue labValue = aLabValue(labConcept)

        when: "I project for the concept"
        Optional<LabConcept> concept = new LabValueToLabConceptProjector().project(Optional.of(labValue), aProjectionContext())

        then: "The concept INSULIN is returned"
        concept.isPresent()
        concept.get().concept == conceptFor("INSULIN")
    }

    @Unroll
    def "Projecting an empty lab value"() {
        given: "An empty lab value"

        when: "I project for the concept"
        Optional<LabConcept> concept = new LabValueToLabConceptProjector().project(Optional.ofNullable(labValue), aProjectionContext())

        then: "An empty result is returned"
        !concept.isPresent()

        where:
        labValue        | _
        null            | _
        aLabValue(null) | _
    }

    static aLabValue(LabConcept labConcept) {
        insulinLabValueBuilder()
                .withLabConcept(labConcept)
                .build()
    }
}
