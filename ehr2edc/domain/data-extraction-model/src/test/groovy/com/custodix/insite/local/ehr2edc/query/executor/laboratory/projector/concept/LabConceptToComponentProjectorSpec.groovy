package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.labConceptBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class LabConceptToComponentProjectorSpec extends Specification {
    def "Projecting a concept component"() {
        given: "A concept component #conceptComponent"
        LabConcept labConcept = aLabConcept(conceptComponent)

        when: "I project for the concept component"
        Optional<String> component = new LabConceptToComponentProjector().project(Optional.of(labConcept), aProjectionContext())

        then: "The concept component #conceptComponent is returned"
        component.isPresent()
        component.get() == conceptComponent

        where:
        conceptComponent = "Component for insulin"
    }

    @Unroll
    def "Projecting an empty concept component"() {
        given: "An empty concept component"

        when: "I project for the concept component"
        Optional<String> concept = new LabConceptToComponentProjector().project(Optional.ofNullable(labConcept), aProjectionContext())

        then: "An empty result is returned"
        !concept.isPresent()

        where:
        labConcept        | _
        null              | _
        aLabConcept(null) | _
    }

    static aLabConcept(String component) {
        labConceptBuilder()
                .withComponent(component)
                .build()
    }
}
