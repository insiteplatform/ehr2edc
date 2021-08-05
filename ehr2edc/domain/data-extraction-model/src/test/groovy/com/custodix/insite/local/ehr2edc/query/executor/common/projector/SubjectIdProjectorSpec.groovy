package com.custodix.insite.local.ehr2edc.query.executor.common.projector


import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.insulinLabValueBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContextWithIdAndRef

class SubjectIdProjectorSpec extends Specification {
    def "Projecting a subject id"() {
        given: "A lab value associated with subject identifier #idValue and edc subject reference #refValue"
        SubjectId id = SubjectId.of(idValue)
        EDCSubjectReference ref = EDCSubjectReference.of(refValue)
        LabValue labValue = insulinLabValueBuilder()
                .forSubject(id)
                .build()

        when: "I project for the subject id"
        Optional<String> subject = new SubjectIdProjector().project(Optional.of(labValue), aProjectionContextWithIdAndRef(id, ref))

        then: "#refValue gets returned"
        subject.isPresent()
        subject.get() == refValue

        where:
        idValue = "123456"
        refValue = "654321"
    }

    def "Projecting a subject id without edc reference"() {
        given: "A lab value associated with subject identifier #idValue and no edc subject reference"
        SubjectId id = SubjectId.of(idValue)
        LabValue labValue = insulinLabValueBuilder()
                .forSubject(id)
                .build()

        when: "I project for the subject id"
        Optional<String> subject = new SubjectIdProjector().project(Optional.of(labValue), aProjectionContextWithIdAndRef(id, null))

        then: "No subject present"
        !subject.isPresent()

        where:
        idValue = "123456"
    }

    @Unroll
    def "Projecting an empty subject id"() {
        given: "A lab value associated with subject identifier #value"
        LabValue labValue = insulinLabValueBuilder()
                .forSubject(value)
                .build()

        when: "I project for the subject id"
        Optional<String> subject = new SubjectIdProjector().project(Optional.of(labValue), aProjectionContext())

        then: "An empty result is returned"
        !subject.isPresent()

        where:
        value              | _
        null               | _
        SubjectId.of(null) | _
    }
}
