package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.command.ClearItemQueryMappings
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId
import static java.util.Arrays.asList
import static java.util.Collections.singletonList
import static org.hamcrest.Matchers.containsInAnyOrder
import static spock.util.matcher.HamcrestSupport.expect

@Title("Delete all mappings for a study")
class ClearItemQueryMappingsSpec extends AbstractSpecification {

    private final static String LONG_ID = "123456789012345678901234567890123456789012345678901234567890"

    @Autowired
    ClearItemQueryMappings clearItemQueryMappings

    def "No request for clearing all mappings"() {
        when: "Clearing all mappings without a request"
        clearItemQueryMappings.clear(null)

        then: "An error indicating a request is needed is thrown"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.getMessage().contains("must not be null")

    }

    @Unroll
    def "Invalid Request for clearing all mappings"(StudyId studyId, List<String> fields, List<String> messages) {
        given: "A request to remove mappings for item #itemId from study #studyId"
        ClearItemQueryMappings.Request request = ClearItemQueryMappings.Request.newBuilder()
                .withStudyId(studyId)
                .build()

        when: "Clearing  all mappings"
        clearItemQueryMappings.clear(request)

        then: "An error should be shown that the #field #messages"
        def exception = thrown(UseCaseConstraintViolationException)
        with(exception.constraintViolations) {
            field == fields
            expect message, containsInAnyOrder(messages.toArray())
        }

        where:
        studyId             | fields                             | messages
        StudyId.of(null)    | singletonList("studyId.id")        | singletonList("must not be blank")
        StudyId.of("")      | asList("studyId.id", "studyId.id") | asList("must not be blank", "size must be between 1 and 50")
        StudyId.of(" ")     | singletonList("studyId.id")        | singletonList("must not be blank")
        StudyId.of(LONG_ID) | singletonList("studyId.id")        | singletonList("size must be between 1 and 50")
        null                | singletonList("studyId")           | singletonList("must not be null")
    }

    def "Request for clearing all mappings of a unknown study"() {
        given: "A request for an unknown study"
        def studyId = aRandomStudyId()
        ClearItemQueryMappings.Request req = ClearItemQueryMappings.Request.newBuilder()
                .withStudyId(studyId)
                .build()

        when: "Clearing  all mappings"
        clearItemQueryMappings.clear(req)

        then: "An error indicating the study was unknown is thrown"
        def ex = thrown(UserException)
        ex.getMessage() == "Unknown StudyId[id=${studyId.id}]"
    }

    def "Request for clearing all mappings of a known study"() {
        given: "A known study with ItemQueryMappings"
        StudySnapshot study = generateKnownStudy(USER_ID_KNOWN)
        assert !study.itemQueryMappings.isEmpty()
        and: "The a request to remove the mappings"
        ClearItemQueryMappings.Request req = ClearItemQueryMappings.Request.newBuilder()
                .withStudyId(study.studyId)
                .build()

        when: "Clearing  all mappings"
        clearItemQueryMappings.clear(req)

        then: "All mappings are removed from the study"
        studyRepository.getStudyById(study.getStudyId()).itemQueryMappings.itemQueryMappingJsonMap.isEmpty()
    }

    def "Request for clearing all mappings of a known study should succeed for unauthenticated users"() {
        given: "A known study with ItemQueryMappings"
        StudySnapshot study = generateKnownStudy(USER_ID_KNOWN)
        assert !study.itemQueryMappings.isEmpty()
        and: "The a request to remove the mappings"
        ClearItemQueryMappings.Request req = ClearItemQueryMappings.Request.newBuilder()
                .withStudyId(study.studyId)
                .build()
        and: "The user is not authenticated"
        withoutAuthenticatedUser()

        when: "Clearing  all mappings"
        clearItemQueryMappings.clear(req)

        then: "The request should succeed"
        noExceptionThrown()
    }

}
