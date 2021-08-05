package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.labConceptBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class LabConceptToSpecimenProjectorSpec extends Specification {
    def "Projecting a concept specimen"() {
        given: "A concept specimen #conceptSpecimen"
        LabConcept labConcept = aLabConcept(conceptSpecimen)

        when: "I project for the concept specimen"
        Optional<String> specimen = new LabConceptToSpecimenProjector().project(Optional.of(labConcept), aProjectionContext())

        then: "The concept specimen #conceptSpecimen is returned"
        specimen.isPresent()
        specimen.get() == conceptSpecimen

        where:
        conceptSpecimen = "Bld"
    }

    @Unroll
    def "Projecting an empty concept specimen"() {
        given: "An empty concept specimen"

        when: "I project for the concept specimen"
        Optional<String> concept = new LabConceptToSpecimenProjector().project(Optional.ofNullable(labConcept), aProjectionContext())

        then: "An empty result is returned"
        !concept.isPresent()

        where:
        labConcept        | _
        null              | _
        aLabConcept(null) | _
    }

    static aLabConcept(String specimen) {
        labConceptBuilder()
                .withSpecimen(specimen)
                .build()
    }
}
