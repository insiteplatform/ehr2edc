package com.custodix.insite.mongodb.export.patient.application.command


import com.custodix.insite.mongodb.export.patient.application.api.ActivateSubject
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import javax.validation.ConstraintViolationException

import static com.custodix.insite.mongodb.export.patient.application.api.ActivateSubjectRequestObjectMother.aDefaultActivateSubjectRequestBuilder
import static com.custodix.insite.mongodb.vocabulary.PatientIdentifierObjectMother.aDefaultPatientIdentifierBuilder

class ActivateSubjectSpec extends AbstractSubjectActivationSpec{

    public static final String PATIENT_ID = "567-567"
    public static final String PATIENT_NAMESPACE = "patient id source"
    public static final String SUBJECT_ID = "123-123"
    @Autowired
    ActivateSubject activateSubject

    @Unroll
    def "I can activate a subject" (SubjectId subjectId, PatientId patientId, Namespace patientIdSource) {
        given:'''request with a patientIdentifier with 
                 subject id #subjectId and
                 patient id #patientId and
                 patient id source #patientIdSource
              '''
        def request = createRequest(subjectId, patientId, patientIdSource)
        and: "can handle patient namespace"
        canHandlerPatientNamespace(patientIdSource)

        when: "activate subject"
        activateSubject.activate(request)

        then: "subject is activated"
        def activeSubject = activeSubjectDocumentRepository.findBySubjectId(subjectId)
        activeSubject.isPresent()
        activeSubject.get().subjectId == subjectId
        activeSubject.get().patientId == patientId.getId()
        activeSubject.get().patientIdSource == patientIdSource.getName()

        where:
        subjectId               | patientId                 | patientIdSource
        SubjectId.of(SUBJECT_ID) | PatientId.of(PATIENT_ID) | Namespace.of(PATIENT_NAMESPACE)
    }

    @Unroll
    def "I cannot activate a subject if the patient namespace is unknown" (SubjectId subjectId, PatientId patientId, Namespace patientIdSource) {
        given:'''request with a patientIdentifier with 
                 subject id #subjectId and
                 patient id #patientId and
                 patient id source #patientIdSource
              '''
        def request = createRequest(subjectId, patientId, patientIdSource)
        and: "cannot handle patient namespace"
        cannotHandlerPatientNamespace(patientIdSource)

        when: "activate subject"
        activateSubject.activate(request)

        then: "subject is not activated"
        def activeSubject = activeSubjectDocumentRepository.findBySubjectId(subjectId)
        !activeSubject.isPresent()

        where:
        subjectId               | patientId                 | patientIdSource
        SubjectId.of(SUBJECT_ID) | PatientId.of(PATIENT_ID) | Namespace.of(PATIENT_NAMESPACE)
    }

    @Unroll
    def "An activated subject will stay active when it is reactivated" (SubjectId subjectId, PatientId patientId, Namespace patientIdSource) {
        given:'''request with a patientIdentifier with 
                 subject id #subjectId and
                 patient id #patientId and
                 patient id source #patientIdSource
              '''
        def request = createRequest(subjectId, patientId, patientIdSource)
        and: "can handle patient namespace"
        canHandlerPatientNamespace(patientIdSource)
        and: '''active subject with
                 subject id #subjectId and
                 patient id #patientId and
                 patient id source #patientIdSource 
             '''
        assertActiveSubject(subjectId, patientId, patientIdSource)

        when: "reactivate subject"
        activateSubject.activate(request)

        then: "subject is still active"
        def activeSubject = activeSubjectDocumentRepository.findBySubjectId(subjectId)
        activeSubject.isPresent()
        activeSubject.get().subjectId == subjectId
        activeSubject.get().patientId == patientId.getId()
        activeSubject.get().patientIdSource == patientIdSource.getName()

        where:
        subjectId               | patientId                 | patientIdSource
        SubjectId.of(SUBJECT_ID) | PatientId.of(PATIENT_ID) | Namespace.of(PATIENT_NAMESPACE)
    }

    @Unroll
    def "I cannot not activate subject when the patient identifier not valid" (PatientIdentifier patientIdentifier, String errorMessage) {
        given: "request with a patientIdentifier #patientIdentifier"
        def request = ActivateSubject.Request.newBuilder().withPatientIdentifier(patientIdentifier).build()

        when: "activate subject"
        activateSubject.activate(request)

        then: "exception is thrown with message #errorMessage"
        def exception = thrown(ConstraintViolationException)
        exception.message == errorMessage

        where:
        patientIdentifier   | errorMessage
        null        | "activate.arg0.patientIdentifier: must not be null"
    }

    @Unroll
    def "I cannot not activate subject when the subject id not valid" (SubjectId subjectId, String propertyPath, String errorMessage) {
        given: "request with a subject #subjectId"
        def request = createRequest(subjectId)

        when: "activate subject"
        activateSubject.activate(request)

        then: "exception is thrown with message #errorMessage"
        ConstraintViolationException exception = thrown(ConstraintViolationException)
        exception.constraintViolations.stream().anyMatch{
            c ->
                c.propertyPath.toString() == propertyPath &&
                c.getMessage() == errorMessage

        }

        where:
        subjectId            | propertyPath                                     | errorMessage
        null                 | "activate.arg0.patientIdentifier.subjectId"      | "must not be null"
        SubjectId.of(null)   | "activate.arg0.patientIdentifier.subjectId.id"   | "must not be blank"
        SubjectId.of("")     | "activate.arg0.patientIdentifier.subjectId.id"   | "must not be blank"
        SubjectId.of("  ")   | "activate.arg0.patientIdentifier.subjectId.id"   | "must not be blank"
    }

    @Unroll
    def "I cannot not activate subject when the patient id not valid" (PatientId patientId, String propertyPath, String errorMessage) {
        given: "request with a patient id  #patientId"
        def request = createRequest(patientId)

        when: "activate subject"
        activateSubject.activate(request)

        then: "exception is thrown with message #errorMessage"
        ConstraintViolationException exception = thrown(ConstraintViolationException)
        exception.constraintViolations.stream().anyMatch{
            c ->
                c.propertyPath.toString() == propertyPath &&
                        c.getMessage() == errorMessage

        }

        where:
        patientId            | propertyPath                                     | errorMessage
        null                 | "activate.arg0.patientIdentifier.patientId"      | "must not be null"
        PatientId.of(null)   | "activate.arg0.patientIdentifier.patientId.id"   | "must not be blank"
        PatientId.of("")     | "activate.arg0.patientIdentifier.patientId.id"   | "must not be blank"
        PatientId.of("  ")   | "activate.arg0.patientIdentifier.patientId.id"   | "must not be blank"
    }

    @Unroll
    def "I cannot not activate subject when the patient id source not valid" (Namespace patientIdSource, String propertyPath, String errorMessage) {
        given: "request with a patient id source #patientIdSource"
        def request = createRequest(patientIdSource)

        when: "activate subject"
        activateSubject.activate(request)

        then: "exception is thrown with message #errorMessage"
        ConstraintViolationException exception = thrown(ConstraintViolationException)
        exception.constraintViolations.stream().anyMatch{
            c ->
                c.propertyPath.toString() == propertyPath &&
                        c.getMessage() == errorMessage

        }

        where:
        patientIdSource            | propertyPath                                     | errorMessage
        null                 | "activate.arg0.patientIdentifier.namespace"      | "must not be null"
        Namespace.of(null)   | "activate.arg0.patientIdentifier.namespace.name"   | "must not be blank"
        Namespace.of("")     | "activate.arg0.patientIdentifier.namespace.name"   | "must not be blank"
        Namespace.of("  ")   | "activate.arg0.patientIdentifier.namespace.name"   | "must not be blank"
    }

    private createRequest(SubjectId subjectId, PatientId patientId, Namespace patientIdSource) {
        ActivateSubject.Request.newBuilder().withPatientIdentifier(PatientIdentifier.newBuilder().withPatientId(patientId).withNamespace(patientIdSource).withSubjectId(subjectId).build()).build()
    }

    private createRequest(SubjectId subjectId) {
        aDefaultActivateSubjectRequestBuilder().withPatientIdentifier(aDefaultPatientIdentifierBuilder().withSubjectId(subjectId).build()).build()
    }

    private createRequest(PatientId patientId) {
        aDefaultActivateSubjectRequestBuilder().withPatientIdentifier(aDefaultPatientIdentifierBuilder().withPatientId(patientId).build()).build()
    }

    private createRequest(Namespace patientIdSource) {
        aDefaultActivateSubjectRequestBuilder().withPatientIdentifier(aDefaultPatientIdentifierBuilder().withNamespace(patientIdSource).build()).build()
    }

}
