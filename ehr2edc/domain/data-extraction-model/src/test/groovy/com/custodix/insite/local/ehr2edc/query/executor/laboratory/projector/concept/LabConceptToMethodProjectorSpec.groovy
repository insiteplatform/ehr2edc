package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.labConceptBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class LabConceptToMethodProjectorSpec extends Specification {
    def "Projecting a concept method"() {
        given: "A concept method #conceptMethod"
        LabConcept labConcept = aLabConcept(conceptMethod)

        when: "I project for the concept method"
        Optional<String> method = new LabConceptToMethodProjector().project(Optional.of(labConcept), aProjectionContext())

        then: "The concept method #conceptMethod is returned"
        method.isPresent()
        method.get() == conceptMethod

        where:
        conceptMethod = "Radioimmunoassay"
    }

    @Unroll
    def "Projecting an empty concept method"() {
        given: "An empty concept method"

        when: "I project for the concept method"
        Optional<String> concept = new LabConceptToMethodProjector().project(Optional.ofNullable(labConcept), aProjectionContext())

        then: "An empty result is returned"
        !concept.isPresent()

        where:
        labConcept        | _
        null              | _
        aLabConcept(null) | _
    }

    static aLabConcept(String method) {
        labConceptBuilder()
                .withMethod(method)
                .build()
    }
}
