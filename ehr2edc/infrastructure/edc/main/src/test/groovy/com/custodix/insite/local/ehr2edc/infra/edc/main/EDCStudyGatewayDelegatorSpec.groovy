package com.custodix.insite.local.ehr2edc.infra.edc.main

import com.custodix.insite.local.ehr2edc.EDCStudyGateway
import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.StudyConnectionRepository
import com.custodix.insite.local.ehr2edc.infra.edc.openclinica.OpenClinicaEDCStudyGateway
import com.custodix.insite.local.ehr2edc.infra.edc.rave.RaveEDCStudyGateway
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection
import org.spockframework.spring.SpringBean
import org.spockframework.spring.SpringSpy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.StudyObjectMother.aDefaultStudy
import static com.custodix.insite.local.ehr2edc.submitted.SubmittedEventObjectMother.aDefaultSubmittedEventBuilder
import static com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReferenceObjectMother.aRandomEdcSubjectReference
import static com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem.OPEN_CLINICA
import static com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem.RAVE
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionObjectMother.aDefaultStudyConnectionBuilder
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType.*

@ContextConfiguration(classes = EDCConfiguration)
class EDCStudyGatewayDelegatorSpec extends Specification {
    @SpringBean
    private StudyConnectionRepository studyConnectionRepository = Stub()
    @SpringSpy
    private RaveEDCStudyGateway raveEDCStudyGateway
    @SpringSpy
    private OpenClinicaEDCStudyGateway openClinicaEDCStudyGateway
    @Autowired
    private EDCStudyGateway edcStudyGateway

    @Unroll
    def "findRegisteredSubjectIds for a study with active EDC connection having edcSystem #edcSystem is delegated to the #edcSystem EDC gateway implementation"(EDCSystem edcSystem, int raveCalls, int openClinicaCalls) {
        given: "a study"
        Study study = aDefaultStudy()
        and: "the study has an active EDC connection with edcSystem #edcSystem"
        ExternalEDCConnection connection = aDefaultStudyConnectionBuilder(study.studyId, READ_SUBJECTS)
                .withEdcSystem(edcSystem)
                .build()
        studyConnectionRepository.findActive(study.studyId, READ_SUBJECTS) >> Optional.of(connection)
        and: "the study has a registered subject on the EDC"
        EDCSubjectReference edcSubjectReference = aRandomEdcSubjectReference()

        when: "a findRegisteredSubjectIds call for the study"
        def result = edcStudyGateway.findRegisteredSubjectIds(study.studyId)

        then: "The call is delegated to the specific EDC gateway implementation"
        raveCalls * raveEDCStudyGateway.findRegisteredSubjectIds(connection) >> [edcSubjectReference]
        openClinicaCalls * openClinicaEDCStudyGateway.findRegisteredSubjectIds(connection) >> [edcSubjectReference]
        and: "the registered subjects from the EDC are returned"
        result.registeredSubjectReferences == [edcSubjectReference]
        result.fromEDC

        where:
        edcSystem    | raveCalls | openClinicaCalls
        RAVE         | 1         | 0
        OPEN_CLINICA | 0         | 1
    }

    @Unroll
    def "createSubject for a study with active EDC connection having edcSystem #edcSystem is delegated to the #edcSystem EDC gateway implementation"(EDCSystem edcSystem, int raveCalls, int openClinicaCalls) {
        given: "a study"
        Study study = aDefaultStudy()
        and: "the study has an active EDC connection with edcSystem #edcSystem"
        ExternalEDCConnection connection = aDefaultStudyConnectionBuilder(study.studyId, WRITE_SUBJECT)
                .withEdcSystem(edcSystem)
                .build()
        studyConnectionRepository.findActive(study.studyId, WRITE_SUBJECT) >> Optional.of(connection)
        and: "a subject reference"
        EDCSubjectReference edcSubjectReference = EDCSubjectReference.of("subject-1")

        when: "a createSubject call for the study and subject"
        edcStudyGateway.createSubject(study, edcSubjectReference)

        then: "The call is delegated to the specific EDC gateway implementation"
        raveCalls * raveEDCStudyGateway.createSubject(connection, study, edcSubjectReference) >> null
        openClinicaCalls * openClinicaEDCStudyGateway.createSubject(connection, study, edcSubjectReference) >> null

        where:
        edcSystem    | raveCalls | openClinicaCalls
        RAVE         | 1         | 0
        OPEN_CLINICA | 0         | 1
    }

    @Unroll
    def "submitReviewedEvent for a study with active EDC connection having edcSystem #edcSystem is delegated to the #edcSystem EDC gateway implementation"(EDCSystem edcSystem, int raveCalls, int openClinicaCalls) {
        given: "a study"
        Study study = aDefaultStudy()
        and: "the study has an active EDC connection with edcSystem #edcSystem"
        ExternalEDCConnection connection = aDefaultStudyConnectionBuilder(study.studyId, SUBMIT_EVENT)
                .withEdcSystem(edcSystem)
                .build()
        studyConnectionRepository.getActive(study.studyId, SUBMIT_EVENT) >> connection
        and: "a submitted event"
        SubmittedEvent submittedEvent = aDefaultSubmittedEventBuilder().withStudyId(study.studyId).build()

        when: "a submitReviewedEvent call for the study and event"
        edcStudyGateway.submitReviewedEvent(submittedEvent, study)

        then: "The call is delegated to the specific EDC gateway implementation"
        raveCalls * raveEDCStudyGateway.submitReviewedEvent(connection, submittedEvent, study) >> null
        openClinicaCalls * openClinicaEDCStudyGateway.submitReviewedEvent(connection, submittedEvent, study) >> null

        where:
        edcSystem    | raveCalls | openClinicaCalls
        RAVE         | 1         | 0
        OPEN_CLINICA | 0         | 1
    }

    @Unroll
    def "isRegisteredSubject for a study with active EDC connection having edcSystem #edcSystem is delegated to the #edcSystem EDC gateway implementation"(EDCSystem edcSystem, int raveCalls, int openClinicaCalls) {
        given: "a study"
        Study study = aDefaultStudy()
        and: "the study has an active EDC connection with edcSystem #edcSystem"
        ExternalEDCConnection connection = aDefaultStudyConnectionBuilder(study.studyId, READ_SUBJECTS)
                .withEdcSystem(edcSystem)
                .build()
        studyConnectionRepository.findActive(study.studyId, READ_SUBJECTS) >> Optional.of(connection)
        and: "a subject reference"
        EDCSubjectReference edcSubjectReference = EDCSubjectReference.of("subject-1")

        when: "a isRegisteredSubject call for the study and subject and runnable"
        edcStudyGateway.isRegisteredSubject(study.studyId, edcSubjectReference)

        then: "The call is delegated to the specific EDC gateway implementation"
        raveCalls * raveEDCStudyGateway.isRegisteredSubject(connection, edcSubjectReference) >> null
        openClinicaCalls * openClinicaEDCStudyGateway.isRegisteredSubject(connection, edcSubjectReference) >> null

        where:
        edcSystem    | raveCalls | openClinicaCalls
        RAVE         | 1         | 0
        OPEN_CLINICA | 0         | 1
    }

    def "findRegisteredSubjectIds for a study without active EDC connection returns an empty list of subjects"() {
        given: "a study"
        Study study = aDefaultStudy()
        and: "the study does not have an active EDC connection"
        studyConnectionRepository.findActive(study.studyId, READ_SUBJECTS) >> Optional.empty()

        when: "a findRegisteredSubjectIds call for the study"
        def result = edcStudyGateway.findRegisteredSubjectIds(study.studyId)

        then: "an empty list of subjects is returned"
        result.registeredSubjectReferences.size() == 0
        !result.fromEDC
    }

    def "createSubject for a study without active EDC connection does nothing"() {
        given: "a study"
        Study study = aDefaultStudy()
        and: "the study does not have an active EDC connection"
        studyConnectionRepository.findActive(study.studyId, WRITE_SUBJECT) >> Optional.empty()
        and: "a subject reference"
        EDCSubjectReference edcSubjectReference = EDCSubjectReference.of("subject-1")

        when: "a createSubject call for the study and subject"
        edcStudyGateway.createSubject(study, edcSubjectReference)

        then: "the specific EDC gateway is not called"
        0 * raveEDCStudyGateway._
    }

    def "isRegisteredSubject for a study without active EDC connection returns empty result"() {
        given: "a study"
        Study study = aDefaultStudy()
        and: "the study does not have an active EDC connection"
        studyConnectionRepository.findActive(study.studyId, READ_SUBJECTS) >> Optional.empty()
        and: "a subject reference"
        EDCSubjectReference edcSubjectReference = EDCSubjectReference.of("subject-1")

        when: "a isRegisteredSubject call for the study and subject"
        def result = edcStudyGateway.isRegisteredSubject(study.studyId, edcSubjectReference)

        then: "the specific EDC gateway is not called"
        0 * raveEDCStudyGateway._
        0 * openClinicaEDCStudyGateway._
        and: "an empty result is returned"
        !result.isPresent()
    }
}
