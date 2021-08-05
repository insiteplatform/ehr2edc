package com.custodix.insite.local.ehr2edc.application


import com.custodix.insite.local.ehr2edc.command.UpdateStudyMetadata
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.StudyODM
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.util.FileCopyUtils
import spock.lang.Title
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException.Type.NOT_EXISTS
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId

@Title("Update Study Metadata")
class UpdateStudyMetadataSpec extends AbstractSpecification {
    private static final Resource ODM_SAMPLE = new ClassPathResource("samples/min-sample-study.xml")
    private static final String META_DATA_DEFINITION_ID = "2788"
    private static final String STUDY_NAME = "Minimal Study"
    private static final String STUDY_DESCRIPTION = "EHR2EDC Description"

    @Autowired
    UpdateStudyMetadata updateStudy

    def "Update study without request"() {
        when: "Updating a study without a request"
        updateStudy.update(null)

        then: "Fail with an indication that the request was not valid"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.each { it.message == "must not be null" }
    }

    def "Update study with an empty request"() {
        given: "An empty request"
        def request = UpdateStudyMetadata.Request.newBuilder().build()

        when: "Updating a study"
        updateStudy.update(request)

        then: "Fail with an indication that the request was not valid"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.each { it.message == "must not be null" }
    }

    @Unroll("Update study with id '#studyId' fails because studyId: '#constraint'")
    def "Update study with an invalid StudyId"() {
        given: "A request with studyId='#studyId'"
        def request = UpdateStudyMetadata.Request.newBuilder()
                .withStudyId(studyId)
                .withStudyODM(studyODM())
                .build()

        when: "Updating a study"
        updateStudy.update(request)

        then: "Fail with an indication that the request was not valid"
        def ex = thrown(UseCaseConstraintViolationException)
        and: "StudyId was invalid with message '#constraint'"
        with(ex.constraintViolations) {
            field == Arrays.asList(fieldName)
            message == Arrays.asList(constraint)
        }

        where:
        studyId                                                           | fieldName | constraint
        null                                                              | "studyId" | "must not be null"
        StudyId.of(null)                                                  | "studyId.id" | "must not be blank"
        StudyId.of("012345678901234567890123456789012345678901234567891") | "studyId.id" | "size must be between 1 and 50"
    }

    def "Update study with invalid metadata"() {
        given: "A request with metadata=null"
        def request = UpdateStudyMetadata.Request.newBuilder()
                .withStudyId(StudyId.of("Valid"))
                .withStudyODM(null)
                .build()

        when: "Updating a study"
        updateStudy.update(request)

        then: "Fail with an indication that the request was not valid"
        def ex = thrown(UseCaseConstraintViolationException)
        and: "Indicate metadata should not be null"
        ex.constraintViolations.any {
            it.field.toString().endsWith("studyODM") &&
                    it.message.contains("must not be null")
        }
    }

    def "Update study with valid request for unknown study"() {
        given: "An unknown study"
        def studyId = aRandomStudyId()
        and: "A request to update this study"
        def request = UpdateStudyMetadata.Request.newBuilder()
                .withStudyId(studyId)
                .withStudyODM(studyODM())
                .build()

        when: "Updating the unknown study"
        updateStudy.update(request)

        then: "Fail with an indication that the study was not found"
        def ex = thrown(UserException)
        ex.message == DomainException.Type.getMessage(NOT_EXISTS, studyId)
    }

    def "Update study with valid request"() {
        given: "A known study"
        def study = generateKnownStudy(USER_ID_KNOWN)
        and: "A request to update the study with new metadata"
        def request = UpdateStudyMetadata.Request.newBuilder()
                .withStudyId(study.studyId)
                .withStudyODM(studyODM())
                .build()

        when: "Updating the study"
        updateStudy.update(request)

        then: "No exceptions are thrown"
        noExceptionThrown()
        and: "The study was updated"
        def savedStudy = studyRepository.getStudyById(study.getStudyId())
        with(savedStudy) {
            studyId == study.studyId
            metadata.id == META_DATA_DEFINITION_ID
            name == STUDY_NAME
            description == STUDY_DESCRIPTION
        }
    }

    def "Update study should succeed for an unauthenticated user"() {
        given: "A known study"
        def study = generateKnownStudy(USER_ID_KNOWN)
        and: "A request to update the study with new metadata"
        def request = UpdateStudyMetadata.Request.newBuilder()
                .withStudyId(study.studyId)
                .withStudyODM(studyODM())
                .build()
        and: "The user is not authenticated"
        withoutAuthenticatedUser()

        when: "Updating the study"
        updateStudy.update(request)

        then: "The request should succeed"
        noExceptionThrown()
    }

    def studyODM() {
        return StudyODM.of(new String(FileCopyUtils.copyToByteArray(ODM_SAMPLE.getFile())))
    }
}
