package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.command.DeleteItemQueryMapping
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.snapshots.ItemQueryMappingSnapshot
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshotObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId
import static java.util.Arrays.asList
import static java.util.Collections.singletonList
import static org.hamcrest.Matchers.containsInAnyOrder
import static spock.util.matcher.HamcrestSupport.expect

@Title("Delete item query mapping")
class DeleteItemQueryMappingSpec extends AbstractSpecification {

    private static final String DEMOGRAPHIC_ITEM_QUERY_MAPPING_FILE = "com/custodix/insite/local/ehr2edc/application/PopulateFormsPopulateSpec_DemographicQueryMapping.json"

    @Autowired
    DeleteItemQueryMapping deleteItemQueryMapping

    def "No Request for deleting an ItemQueryMapping"() {
        when: "Deleting the ItemQueryMapping without a request"
        deleteItemQueryMapping.delete(null)

        then: "An error indicating a request is needed is thrown"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.getMessage().contains("must not be null")
    }

    def "Request for deleting an ItemQueryMapping in a unknown study"() {
        given: "A request for an unknown study"
        def studyId = aRandomStudyId()
        DeleteItemQueryMapping.Request req = DeleteItemQueryMapping.Request.newBuilder()
                .withStudyId(studyId)
                .withItemId(ItemDefinitionId.of("item"))
                .build()

        when: "Deleting the ItemQueryMapping"
        deleteItemQueryMapping.delete(req)

        then: "An error indicating the study was unknown is thrown"
        def ex = thrown(UserException)
        ex.getMessage() == "Unknown StudyId[id=${studyId.id}]"
    }

    def "Request for deleting an unknown ItemQueryMapping in a known study"() {
        given: "A known study"
        def studyId = generateKnownStudyId(USER_ID_KNOWN)
        and: "A request to delete an unknown ItemQueryMapping"
        DeleteItemQueryMapping.Request req = DeleteItemQueryMapping.Request.newBuilder()
                .withStudyId(studyId)
                .withItemId(ItemDefinitionId.of("unknown"))
                .build()

        when: "Deleting the ItemQueryMapping"
        deleteItemQueryMapping.delete(req)

        then: "No error is thrown"
        noExceptionThrown()
    }

    def "Request for deleting an ItemQueryMapping in a known study"() {
        given: "A known study with ItemQueryMappings"
        StudyId studyId = StudyId.of("STUDY")
        ItemDefinitionId itemId = ItemDefinitionId.of("item-123")
        createStudy(studyId, itemId)
        and: "A request to delete an item"
        DeleteItemQueryMapping.Request req = DeleteItemQueryMapping.Request.newBuilder()
                .withStudyId(studyId)
                .withItemId(itemId)
                .build()

        when: "Deleting the ItemQueryMapping"
        deleteItemQueryMapping.delete(req)

        then: "The ItemQueryMapping was removed from the study"
        !studyRepository.getStudyById(studyId).itemQueryMappings.hasMapping(itemId)
    }

    @Unroll
    def "Invalid Request for deleting an ItemQueryMapping"(StudyId studyId, ItemDefinitionId itemId, List<String> fields, List<String> messages) {
        given: "A request to remove mappings for item #itemId from study #studyId"
        DeleteItemQueryMapping.Request request = DeleteItemQueryMapping.Request.newBuilder()
                .withStudyId(studyId)
                .withItemId(itemId)
                .build()

        when: "Deleting the ItemQueryMapping"
        deleteItemQueryMapping.delete(request)

        then: "An error should be shown that the #field #messages"
        def exception = thrown(UseCaseConstraintViolationException)
        with(exception.constraintViolations) {
            field == fields
            expect message, containsInAnyOrder(messages.toArray())
        }

        where:
        studyId               | itemId                         | fields                             | messages
        StudyId.of("123-123") | null                           | singletonList("itemId")            | singletonList("must not be null")
        StudyId.of("123-123") | ItemDefinitionId.of(null)      | singletonList("itemId.id")         | singletonList("must not be blank")
        StudyId.of("123-123") | ItemDefinitionId.of("")        | singletonList("itemId.id")         | singletonList("must not be blank")
        StudyId.of(null)      | ItemDefinitionId.of("ITEM_ID") | singletonList("studyId.id")        | singletonList("must not be blank")
        StudyId.of("")        | ItemDefinitionId.of("ITEM_ID") | asList("studyId.id", "studyId.id") | asList("must not be blank", "size must be between 1 and 50")
        StudyId.of(" ")       | ItemDefinitionId.of("ITEM_ID") | singletonList("studyId.id")        | singletonList("must not be blank")
        null                  | ItemDefinitionId.of("ITEM_ID") | singletonList("studyId")           | singletonList("must not be null")
    }

    def "Request for deleting an item query mapping should succeed for an unauthenticated user"() {
        given: "A known study with ItemQueryMappings"
        StudyId studyId = StudyId.of("STUDY")
        ItemDefinitionId itemId = ItemDefinitionId.of("item-123")
        createStudy(studyId, itemId)
        and: "A request to delete an item"
        DeleteItemQueryMapping.Request req = DeleteItemQueryMapping.Request.newBuilder()
                .withStudyId(studyId)
                .withItemId(itemId)
                .build()
        and: "The user is not authenticated"
        withoutAuthenticatedUser()

        when: "Deleting the item query mapping"
        deleteItemQueryMapping.delete(req)

        then: "The request should succeed"
        noExceptionThrown()
    }

    private void createStudy(StudyId studyId, ItemDefinitionId itemId) {
        URL url = Resources.getResource(DEMOGRAPHIC_ITEM_QUERY_MAPPING_FILE)
        def mapping = Resources.toString(url, Charsets.UTF_8)
        def studySnapshot = StudySnapshotObjectMother.aDefaultStudySnapshotBuilder(USER_ID_KNOWN)
                .withStudyId(studyId)
                .withItemQueryMappings(Collections.singletonMap(
                        itemId,
                        ItemQueryMappingSnapshot.newBuilder().withValue(mapping).build()
                )).build()
        studyRepository.save(Study.restoreSnapshot(studySnapshot))
    }

}
