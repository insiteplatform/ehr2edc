package com.custodix.insite.local.ehr2edc.query.executor.common.projector

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import spock.lang.Specification

import java.time.LocalDate

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.insulinLabValueBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContextWithIdAndRef
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContextWithIdRefAndConsentDate

class GetConsentDateSpec extends Specification {
    def "Projecting a consent date"() {
        given: "A lab value associated with subject identifier #idValue and edc subject reference #refValue"
        SubjectId id = SubjectId.of(idValue)
        EDCSubjectReference ref = EDCSubjectReference.of(refValue)
        LocalDate consentDate = LocalDate.now();
        LabValue labValue = insulinLabValueBuilder()
                .forSubject(id)
                .build()

        when: "I project for the consent date"
        Optional<LocalDate> projectedDate = new GetConsentDate().project(Optional.of(labValue), aProjectionContextWithIdRefAndConsentDate(id, ref, consentDate))

        then: "the consent date gets returned"
        projectedDate.isPresent()
        projectedDate.get() == consentDate

        where:
        idValue = "123456"
        refValue = "654321"
    }

    def "Projecting a subject id without consent date"() {
        given: "A lab value associated with subject identifier #idValue and edc subject reference #refValue"
        SubjectId id = SubjectId.of(idValue)
        EDCSubjectReference ref = EDCSubjectReference.of(refValue)
        LabValue labValue = insulinLabValueBuilder()
                .forSubject(id)
                .build()

        when: "I project for the consent date"
        Optional<LocalDate> projectedDate = new GetConsentDate().project(Optional.of(labValue), aProjectionContextWithIdAndRef(id, ref))

        then: "No consent date is present"
        !projectedDate.isPresent()

        where:
        idValue = "123456"
        refValue = "654321"
    }
}
