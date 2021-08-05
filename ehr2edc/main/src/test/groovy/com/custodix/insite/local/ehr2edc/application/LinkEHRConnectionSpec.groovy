package com.custodix.insite.local.ehr2edc.application


import com.custodix.insite.local.ehr2edc.command.LinkEHRConnection
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.vocabulary.EHRSystem
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

class LinkEHRConnectionSpec extends AbstractSpecification {
    private static final URI CONNECTION_URI = URI.create("http://localhost:8080/fhir")
    private static final EHRSystem CONNECTION_SYSTEM = EHRSystem.FHIR

    @Autowired
    LinkEHRConnection linkEHRConnection

    def "Linking an EHR connection to a study"() {
        given: "a study"
        def study = generateKnownStudy(USER_ID_KNOWN)

        when: "I link an EHR connection to the study"
        linkEHRConnection.link(createRequest(study.studyId))

        then: "the EHR connection is linked to the study"
        with(ehrConnectionRepository.getByStudyId(study.studyId)) {
            studyId == study.studyId
            uri == CONNECTION_URI
            system == CONNECTION_SYSTEM
        }
    }

    def "Linking an EHR connection to a study as an unauthenticated user succeeds"() {
        given: "a study"
        def study = generateKnownStudy(USER_ID_KNOWN)
        and: "I am not authenticated"
        withoutAuthenticatedUser()

        when: "I link an EHR connection to the study"
        linkEHRConnection.link(createRequest(study.studyId))

        then: "the EHR connection is linked to the study"
        with(ehrConnectionRepository.getByStudyId(study.studyId)) {
            studyId == study.studyId
            uri == CONNECTION_URI
            system == CONNECTION_SYSTEM
        }
    }

    def "Linking an EHR connection to a study that does not exist fails"() {
        when: "I link an EHR connection to a study that does not exist"
        def studyId = StudyId.of("999")
        linkEHRConnection.link(createRequest(studyId))

        then: "I receive an error message indicating that the study does not exist"
        def ex = thrown(UserException)
        ex.message == DomainException.Type.getMessage(DomainException.Type.NOT_EXISTS, studyId)
    }

    def "Linking an EHR connection to a study without a request fails"() {
        when: "I link an EHR connection to a study without a request"
        linkEHRConnection.link(null)

        then: "I receive an error message indicating that the request is invalid"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.each { it.message == "must not be null" }
    }

    def "Linking an EHR connection to a study with an empty request fails"() {
        when: "I link an EHR connection to a study without a request"
        linkEHRConnection.link(LinkEHRConnection.Request.newBuilder().build())

        then: "I receive an error message indicating that the request is invalid"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.each { it.message == "must not be null" }
    }

    @Unroll("Linking an EHR connection to a study with study id '#studyId', system '#system', uri '#uri' fails because #fieldName: '#constraint'")
    def "Linking an EHR connection to a study with an invalid request fails"() {
        when: "I link an EHR connection to a study with study id '#studyId', system '#system', uri '#uri'"
        LinkEHRConnection.Request request = LinkEHRConnection.Request.newBuilder()
                .withStudyId(studyId)
                .withUri(uri)
                .withSystem(system).build()
        linkEHRConnection.link(request)

        then: "I receive an error message indicating that #fieldName #constraint"
        def ex = thrown(UseCaseConstraintViolationException)
        with(ex.constraintViolations) {
            field == Arrays.asList(fieldName)
            message == Arrays.asList(constraint)
        }

        where:
        studyId                              | uri            | system            | fieldName    | constraint
        StudyId.of(null)                     | CONNECTION_URI | CONNECTION_SYSTEM | "studyId.id" | "must not be blank"
        StudyId.of("   ")                    | CONNECTION_URI | CONNECTION_SYSTEM | "studyId.id" | "must not be blank"
        null                                 | CONNECTION_URI | CONNECTION_SYSTEM | "studyId"    | "must not be null"
        StudyIdObjectMother.aRandomStudyId() | null           | CONNECTION_SYSTEM | "uri"        | "must not be null"
        StudyIdObjectMother.aRandomStudyId() | CONNECTION_URI | null              | "system"     | "must not be null"
    }

    private LinkEHRConnection.Request createRequest(StudyId studyId) {
        return LinkEHRConnection.Request.newBuilder()
                .withStudyId(studyId)
                .withUri(CONNECTION_URI)
                .withSystem(CONNECTION_SYSTEM).build()
    }
}
