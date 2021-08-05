package com.custodix.insite.mongodb.export.patient.application.command


import com.custodix.insite.mongodb.export.patient.application.api.DeactivateSubject
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import javax.validation.ConstraintViolationException

class DeactivateSubjectSpec extends AbstractSubjectActivationSpec {

    @Autowired
    DeactivateSubject deactivateSubject

    @Unroll
    def "I can deactivate a subject" (SubjectId subjectId, PatientId patientId, Namespace patientIdSource) {
        given: '''An active subject with
                 subject id #subjectId and
                 patient id #patientId and
                 patient id source #patientIdSource 
             '''
        assertActiveSubject(subjectId, patientId, patientIdSource)

        when: "deactivate subject"
        def request = createRequest(subjectId)
        deactivateSubject.deactivate(request)

        then: "subject is deactivated"
        def activeSubject = activeSubjectDocumentRepository.findBySubjectId(subjectId)
        !activeSubject.isPresent()

        where:
        subjectId               | patientId                 | patientIdSource
        SubjectId.of("123-123") | PatientId.of("567-567")   | Namespace.of("patient id source")
    }

    @Unroll
    def "I can deactivate subject that does not exist" (SubjectId subjectId) {
        given: "That there is no active subject with subject id #subjectId "
        assertNoActiveSubject(subjectId)

        when: "deactivate subject"
        def request = createRequest(subjectId)
        deactivateSubject.deactivate(request)

        then: "subject is still deactivated"
        def activeSubject = activeSubjectDocumentRepository.findBySubjectId(subjectId)
        !activeSubject.isPresent()

        where:
        subjectId << SubjectId.of("123-123")
    }

    @Unroll
    def "I cannot deactivate subject when the subject id not valid" (SubjectId subjectId, String propertyPath, String errorMessage) {
        given: "A request with a invalid subject with id #subjectId"
        def request = createRequest(subjectId)

        when: "activate subject"
        deactivateSubject.deactivate(request)

        then: "exception is thrown with message #errorMessage"
        ConstraintViolationException exception = thrown(ConstraintViolationException)
        exception.constraintViolations.stream().anyMatch{
            c ->
                c.propertyPath.toString() == propertyPath &&
                c.getMessage() == errorMessage

        }

        where:
        subjectId            | propertyPath                                     | errorMessage
        null                 | "deactivate.arg0.subjectId"      | "must not be null"
        SubjectId.of(null)   | "deactivate.arg0.subjectId.id"   | "must not be blank"
        SubjectId.of("")     | "deactivate.arg0.subjectId.id"   | "must not be blank"
        SubjectId.of("  ")   | "deactivate.arg0.subjectId.id"   | "must not be blank"
    }

    private createRequest(SubjectId subjectId) {
        DeactivateSubject.Request.newBuilder().withSubjectId(subjectId).build()
    }
}
