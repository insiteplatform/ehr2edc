package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode.conceptFor
import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.labConceptBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class LabConceptToCodeProjectorSpec extends Specification {
    def "Projecting a concept code"() {
        given: "A concept code #conceptCode"
        LabConcept labConcept = aLabConcept(conceptFor(conceptCode))

        when: "I project for the concept code"
        Optional<String> code = new LabConceptToCodeProjector().project(Optional.of(labConcept), aProjectionContext())

        then: "The concept code #conceptCode is returned"
        code.isPresent()
        code.get() == conceptCode

        where:
        conceptCode = "INSULIN"
    }

    @Unroll
    def "Projecting an empty concept code"() {
        given: "An empty concept code"

        when: "I project for the concept code"
        Optional<String> code = new LabConceptToCodeProjector().project(Optional.ofNullable(labConcept), aProjectionContext())

        then: "An empty result is returned"
        !code.isPresent()

        where:
        labConcept                    | _
        null                          | _
        aLabConcept(conceptFor(null)) | _
        aLabConcept(null)             | _
    }

    static aLabConcept(ConceptCode code) {
        labConceptBuilder()
                .withConcept(code)
                .build()
    }
}
