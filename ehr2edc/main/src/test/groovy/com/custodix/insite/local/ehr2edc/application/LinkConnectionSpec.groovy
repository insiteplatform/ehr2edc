package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.command.LinkConnection
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalSiteId
import com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem.RAVE
import static com.custodix.insite.local.ehr2edc.vocabulary.ExternalSiteIdObjectMother.aRandomExternalSiteId
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType.READ_LABNAMES
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId

@Title("Link an external connection to a study")
class LinkConnectionSpec extends AbstractSpecification {

    @Autowired
    LinkConnection linkConnection

    def "Link an external connection to a Study without a request"() {
        when: "Linking a connection without a request"
        linkConnection.link(null)

        then: "An error should be shown that parameters have to be provided"
        def ex = thrown(UseCaseConstraintViolationException.class)
        ex
    }

    def "Link an external connection to an unknown Study"() {
        given: "A valid request for an unknown study"
        def req = validRequestBuilder()
                .withStudyId(aRandomStudyId())
                .build()

        when: "Linking the connection to the unknown study"
        linkConnection.link(req)

        then: "An error should be shown that the study does not exist"
        def ex = thrown(UserException)
        ex.message == DomainException.Type.getMessage(DomainException.Type.NOT_EXISTS, req.studyId)
    }

    def "Link an external connection to a known Study"() {
        given: "A valid request for a known study"
        def knownStudy = generateKnownStudy(USER_ID_KNOWN)
        def request = validRequestBuilder()
                .withStudyId(knownStudy.studyId)
                .build()

        when: "Linking the external EDC to the Study"
        linkConnection.link(request)

        then: "An EDC connection was created"
        with(studyConnectionRepository.getStudyConnectionByIdAndType(request.studyId, request.type)) {
            studyId == request.studyId
            connectionType == request.type
            edcSystem == request.edcSystem
            externalSiteId == request.externalSiteId
            clinicalDataURI == request.clinicalDataURI
            username == request.username
            password == request.password
            enabled == request.enabled
        }
    }

    def "Link a connection to a known Study with a StudyId override"() {
        given: "A valid request with StudyId override for a known study"
        def knownStudy = generateKnownStudy(USER_ID_KNOWN)
        def request = validRequestBuilder()
                .withStudyId(knownStudy.studyId)
                .withStudyIdOverride(StudyId.of("overrideStudyId"))
                .build()

        when: "Linking the EDC with overridden studyId to the Study"
        linkConnection.link(request)

        then: "The StudyId override is persisted"
        with(studyConnectionRepository.getStudyConnectionByIdAndType(knownStudy.studyId, READ_LABNAMES)) {
            studyIdOverride.isPresent()
            studyIdOverride.get().id == "overrideStudyId"
        }
    }

    def "Link a disabled external connection to a known Study"() {
        given: "A valid request with connection disabled for a known study"
        def knownStudy = generateKnownStudy(USER_ID_KNOWN)
        def request = validRequestBuilder()
                .withStudyId(knownStudy.studyId)
                .withEnabled(false)
                .build()

        when: "Linking the disabled external EDC to the Study"
        linkConnection.link(request)

        then: "The EDC connection is persisted as disabled"
        with(studyConnectionRepository.getStudyConnectionByIdAndType(knownStudy.studyId, READ_LABNAMES)) {
            !enabled
        }
    }

    @Unroll
    def "Link an EDC connection to a study with edcSystem '#edcSystem'"(EDCSystem edcSystem) {
        given: "A valid request with edcSystem '#edcSystem'"
        def knownStudy = generateKnownStudy(USER_ID_KNOWN)
        def request = validRequestBuilder()
                .withStudyId(knownStudy.studyId)
                .withEdcSystem(edcSystem)
                .build()

        when: "Linking the external EDC to the Study "
        linkConnection.link(request)

        then: "An EDC connection was created with edcSystem '#edcSystem'"
        with(studyConnectionRepository.getStudyConnectionByIdAndType(request.studyId, request.type)) {
            it.edcSystem == edcSystem
        }

        where:
        edcSystem << EDCSystem.values()
    }

    @Unroll
    def "Link an external connection to a Study with invalid request parameters"(
            StudyId studyId, StudyConnectionType type, EDCSystem edcSystem, ExternalSiteId externalSiteId, URI clinicalDataURI,
            String username, String password, Boolean enabled, String typeOfError, String errorMessage) {
        given: "A request with an #typeOfError"
        def request = LinkConnection.Request.newBuilder()
                .withStudyId(studyId)
                .withType(type)
                .withEdcSystem(edcSystem)
                .withExternalSiteId(externalSiteId)
                .withClinicalDataURI(clinicalDataURI)
                .withUsername(username)
                .withPassword(password)
                .withEnabled(enabled)
                .build()

        when: "Linking the external connection to the Study"
        linkConnection.link(request)

        then: "An error on invalid parameters #errorMessage should appear"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.first().message == errorMessage

        where:
        studyId          | type          | edcSystem | externalSiteId          | clinicalDataURI                      | username | password | enabled | typeOfError            | errorMessage
        null             | READ_LABNAMES | RAVE      | aRandomExternalSiteId() | new URI("/studies/SID_001/Subjects") | "usr"    | "pwd"    | true    | "null study id"        | "must not be null"
        StudyId.of(null) | READ_LABNAMES | RAVE      | aRandomExternalSiteId() | new URI("/studies/SID_001/Subjects") | "usr"    | "pwd"    | true    | "null study id"        | "must not be blank"
        aRandomStudyId() | null          | RAVE      | aRandomExternalSiteId() | new URI("/studies/SID_001/Subjects") | "usr"    | "pwd"    | true    | "null connection type" | "must not be null"
        aRandomStudyId() | READ_LABNAMES | null      | aRandomExternalSiteId() | new URI("/studies/SID_001/Subjects") | "usr"    | "pwd"    | true    | "null edcSystem"       | "must not be null"
        aRandomStudyId() | READ_LABNAMES | RAVE      | null                    | new URI("/studies/SID_001/Subjects") | "usr"    | "pwd"    | true    | "null site reference"  | "must not be null"
        aRandomStudyId() | READ_LABNAMES | RAVE      | ExternalSiteId.of(null) | new URI("/studies/SID_001/Subjects") | "usr"    | "pwd"    | true    | "null site reference"  | "must not be blank"
        aRandomStudyId() | READ_LABNAMES | RAVE      | aRandomExternalSiteId() | null                                 | "usr"    | "pwd"    | true    | "null clinicalDataURI" | "must not be null"
        aRandomStudyId() | READ_LABNAMES | RAVE      | aRandomExternalSiteId() | new URI("/studies/SID_001/Subjects") | null     | "pwd"    | true    | "null username"        | "must not be blank"
        aRandomStudyId() | READ_LABNAMES | RAVE      | aRandomExternalSiteId() | new URI("/studies/SID_001/Subjects") | ""       | "pwd"    | true    | "blank username"       | "must not be blank"
        aRandomStudyId() | READ_LABNAMES | RAVE      | aRandomExternalSiteId() | new URI("/studies/SID_001/Subjects") | "usr"    | null     | true    | "null password"        | "must not be blank"
        aRandomStudyId() | READ_LABNAMES | RAVE      | aRandomExternalSiteId() | new URI("/studies/SID_001/Subjects") | "usr"    | ""       | true    | "blank password"       | "must not be blank"
        aRandomStudyId() | READ_LABNAMES | RAVE      | aRandomExternalSiteId() | new URI("/studies/SID_001/Subjects") | "usr"    | "pwd"    | null    | "null enabled"         | "must not be null"
    }

    def "Link an external connection should succeed for an unauthenticated user"() {
        given: "A valid request for a known study"
        def knownStudy = generateKnownStudy(USER_ID_KNOWN)
        def request = validRequestBuilder()
                .withStudyId(knownStudy.studyId)
                .build()
        and: "The user is not authenticated"
        withoutAuthenticatedUser()

        when: "Linking the external EDC to the Study"
        linkConnection.link(request)

        then: "The request should succeed"
        noExceptionThrown()
    }

    def validRequestBuilder() {
        LinkConnection.Request.newBuilder()
                .withType(READ_LABNAMES)
                .withEdcSystem(RAVE)
                .withExternalSiteId(aRandomExternalSiteId())
                .withClinicalDataURI(new URI("/studies/SID_001/Subjects"))
                .withUsername("usr")
                .withPassword("pwd")
                .withEnabled(true)
    }
}
