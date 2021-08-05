package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.EDCStudyGateway
import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.command.CreateSubjectInEDC
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference
import com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import spock.lang.Title
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReferenceObjectMother.aRandomEdcSubjectReference
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.BDDMockito.given
import static org.mockito.Mockito.never
import static org.mockito.Mockito.verify

@Title("Create subject in EDC")
class CreateSubjectInEDCSpec extends AbstractSpecification {

    @Autowired
    CreateSubjectInEDC createSubjectInEDC

    @MockBean
    private EDCStudyGateway edcStudyGateway

    def "Create subject without a Request"() {
        when: "Creating a subject without a request"
        createSubjectInEDC.create(null)

        then: "An error indicating a request is needed is thrown"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.getMessage().contains("must not be null")
    }

    def "Create subject with an empty Request"() {
        when: "Creating a subject with an empty request"
        createSubjectInEDC.create(CreateSubjectInEDC.Request.newBuilder().build())

        then: "An error indicating all parameters are required is thrown"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.size() == 2
    }

    def "Create subject for unknown study"() {
        given: "An unknown studyId"
        def studyId = aRandomStudyId()
        and: "A valid request for that study"
        def request = CreateSubjectInEDC.Request.newBuilder()
                .withStudyId(studyId)
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .build()
        given(edcStudyGateway.createSubject(any(Study.class),
                eq(request.edcSubjectReference))).willThrow(new SystemException("Could not register subject with edc"))

        when: "Creating a subject in the EDC system"
        createSubjectInEDC.create(request)

        then: "The EDCGateway is not called"
        verify(edcStudyGateway, never()).createSubject(Mockito.any(), Mockito.any())
        and: "An error indicates that the study does not exist"
        def ex = thrown(UserException)
        ex.message == DomainException.Type.getMessage(DomainException.Type.NOT_EXISTS, studyId)
    }

    def "Create subject with a valid Request"() {
        given: "A random study"
        def study = generateKnownStudy(USER_ID_KNOWN)
        and: "a valid request"
        def request = CreateSubjectInEDC.Request.newBuilder()
                .withStudyId(study.studyId)
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .build()

        when: "Creating a subject in the EDC system"
        createSubjectInEDC.create(request)

        then: "The EDCGateway is called with the appropriate parameters"
        verify(edcStudyGateway).createSubject(any(Study.class),
                eq(request.edcSubjectReference))
    }

    def "Valid create subject yet error while communicating with EDC system then create is interrupted"() {
        given: "A random study"
        def study = generateKnownStudy(USER_ID_KNOWN)
        and: "a valid request"
        def request = CreateSubjectInEDC.Request.newBuilder()
                .withStudyId(study.studyId)
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .build()
        and: "External EDC call will fail"
        given(edcStudyGateway.createSubject(any(Study.class), eq(request.edcSubjectReference)))
                .willThrow(new SystemException("Could not register subject with edc"))

        when: "Creating a subject in the EDC system"
        createSubjectInEDC.create(request)

        then: "The EDCGateway is called with the appropriate parameters"
        verify(edcStudyGateway).createSubject(any(Study.class),
                eq(request.edcSubjectReference))

        then: "Expect a SystemException"
        def ex = thrown(SystemException)
        ex.message == "Could not register subject with edc"
    }

    @Unroll
    def "Create subject with invalid request parameters"() {
        given: "A request with invalid parameters"
        if (connection != null) {
            saveInStudyConnectionSnapshotRepository(connection)
        }
        def request = CreateSubjectInEDC.Request.newBuilder()
                .withStudyId(studyId)
                .withEdcSubjectReference(edcReference)
                .build()

        when: "Creating a subject in the EDC system"
        createSubjectInEDC.create(request)

        then: "An error explaining the invalid parameter is thrown"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.first().message == message

        where:
        studyId           | connection                  | edcReference                 || message
        null              | null | aRandomEdcSubjectReference() || "must not be null"
        StudyId.of("   ") | generateEDCConnectionData(studyId, StudyConnectionType.READ_SUBJECTS) | aRandomEdcSubjectReference() || "must not be blank"
        aRandomStudyId()  | generateEDCConnectionData(studyId, StudyConnectionType.READ_SUBJECTS) | null                         || "must not be null"
        aRandomStudyId()  | generateEDCConnectionData(studyId, StudyConnectionType.READ_SUBJECTS) | EDCSubjectReference.of(" ")  || "must not be blank"
    }

    def "Create subject should succeed for an unauthenticated user"() {
        given: "A random study"
        def study = generateKnownStudy(USER_ID_KNOWN)
        and: "a valid request"
        def request = CreateSubjectInEDC.Request.newBuilder()
                .withStudyId(study.studyId)
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .build()
        and: "The user is not authenticated"
        withoutAuthenticatedUser()

        when: "Creating a subject in the EDC system"
        createSubjectInEDC.create(request)

        then: "The request should succeed"
        noExceptionThrown()
    }
}
