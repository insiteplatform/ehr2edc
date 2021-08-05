package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.StudyRepository
import com.custodix.insite.local.ehr2edc.command.DeleteStudy
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException.Type.NOT_EXISTS

class DeleteStudySpec extends AbstractSpecification {
    @Autowired
    DeleteStudy deleteStudy
    @Autowired
    StudyRepository studyRepository

    def "Delete a study"() {
        given: "a study"
        def study = generateKnownStudy(USER_ID_KNOWN)

        when: "I delete the study"
        deleteStudy.delete(createRequest(study.studyId))

        then: "the study is deleted"
        !studyRepository.exists(study.studyId)
    }

    def "Delete a study as an unauthenticated user succeeds"() {
        given: "a study"
        def study = generateKnownStudy(USER_ID_KNOWN)
        and: "I am not authenticated"
        withoutAuthenticatedUser()

        when: "I delete the study"
        deleteStudy.delete(createRequest(study.studyId))

        then: "the study is deleted"
        !studyRepository.exists(study.studyId)
    }

    def "Delete a study that does not exist fails"() {
        when: "I delete a study that does not exist"
        def studyId = StudyId.of("999")
        deleteStudy.delete(createRequest(studyId))

        then: "study deletion fails with an error message indicating that the study does not exist"
        def ex = thrown(UserException)
        ex.message == DomainException.Type.getMessage(NOT_EXISTS, studyId)
    }

    def "Delete a study without a request fails"() {
        when: "I delete a study without a request"
        deleteStudy.delete(null)

        then: "study deletion fails with an error message indicating that the request is invalid"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.each { it.message == "must not be null" }
    }

    def "Delete a study with an empty request fails"() {
        when: "I delete a study with an empty request"
        deleteStudy.delete(DeleteStudy.Request.newBuilder().build())

        then: "study deletion fails with an error message indicating that the request is invalid"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.each { it.message == "must not be null" }
    }

    @Unroll("Delete a study with study id '#studyId' fails because studyId: '#constraint'")
    def "Delete a study with an invalid request fails"() {
        when: "I delete a study with a request with studyId='#studyId'"
        deleteStudy.delete(createRequest(studyId))

        then: "study deletion fails with an error message indicating that the request is invalid"
        def ex = thrown(UseCaseConstraintViolationException)
        and: "StudyId was invalid with message '#constraint'"
        with(ex.constraintViolations) {
            field == Arrays.asList(fieldName)
            message == Arrays.asList(constraint)
        }

        where:
        studyId           | fieldName    | constraint
        StudyId.of(null)  | "studyId.id" | "must not be blank"
        StudyId.of("   ") | "studyId.id" | "must not be blank"
        null              | "studyId"    | "must not be null"
    }

    private DeleteStudy.Request createRequest(StudyId studyId) {
        DeleteStudy.Request.newBuilder()
                .withStudyId(studyId)
                .build()
    }
}
