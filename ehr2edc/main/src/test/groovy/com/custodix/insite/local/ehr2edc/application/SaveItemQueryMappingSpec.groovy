package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.command.SaveItemQueryMapping
import com.custodix.insite.local.ehr2edc.domain.service.ItemQueryMappingService
import com.custodix.insite.local.ehr2edc.mongo.app.study.StudyMongoRepository
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.birthdate.AgeUnit
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.birthdate.DateOfBirthToAgeProjector
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.GenderProjector
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.command.SaveItemQueryMapping.Request
import static com.custodix.insite.local.ehr2edc.query.executor.demographic.query.DemographicQueryObjectMother.aDefaultDemographicQuery
import static com.custodix.insite.local.ehr2edc.query.executor.demographic.query.DemographicQueryObjectMother.aDemographicQueryWithoutCriterion
import static java.util.Arrays.asList
import static java.util.Collections.singletonList
import static org.hamcrest.Matchers.containsInAnyOrder
import static spock.util.matcher.HamcrestSupport.expect

@Title("Save item query mapping")
class SaveItemQueryMappingSpec extends AbstractSpecification {

    private static final String ITEM_QUERY_MAPPING_FILE = "com/custodix/insite/local/ehr2edc/application/" +
            "SaveItemQueryMappingSpec_storedItemQueryMapping.json"

    @Autowired
    SaveItemQueryMapping saveItemQueryMapping

    @Autowired
    StudyMongoRepository.StudyMongoSnapshotRepository studyMongoSnapshotRepository

    @Autowired
    ItemQueryMappingService itemQueryMappingService

    @Unroll
    def "Invalid Request for Saving an Item Query Mapping"(StudyId studyId, ItemDefinitionId itemId, Query query, List<Projector> projectors, List<String> fields, List<String> messages) {
        given: "A request with item id `#itemId`, query `#query` and `#noProjectors` projectors"
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withItemId(itemId)
                .withQuery(query)
                .withProjectors(projectors)
                .build()

        when: "Saving the Item Query Mapping"
        saveItemQueryMapping.save(request)

        then: "An error should be shown that the #field #messages"
        def exception = thrown(UseCaseConstraintViolationException)
        with(exception.constraintViolations) {
            field == fields
            expect message, containsInAnyOrder(messages.toArray())
        }

        where:
        studyId               | itemId                         | query                      | projectors              | fields                             | messages
        StudyId.of("123-123") | null                           | aDefaultDemographicQuery() | Collections.emptyList() | singletonList("itemId")            | singletonList("must not be null")
        StudyId.of("123-123") | ItemDefinitionId.of(null)      | aDefaultDemographicQuery() | Collections.emptyList() | singletonList("itemId.id")         | singletonList("must not be blank")
        StudyId.of("123-123") | ItemDefinitionId.of("")        | aDefaultDemographicQuery() | Collections.emptyList() | singletonList("itemId.id")         | singletonList("must not be blank")
        StudyId.of("123-123") | ItemDefinitionId.of("ITEM_ID") | null                       | Collections.emptyList() | singletonList("query")             | singletonList("must not be null")
        StudyId.of("123-123") | ItemDefinitionId.of("ITEM_ID") | aDefaultDemographicQuery() | null                    | singletonList("projectors")        | singletonList("must not be null")
        StudyId.of("123-123") | ItemDefinitionId.of("ITEM_ID") | aDefaultDemographicQuery() | null                    | singletonList("projectors")        | singletonList("must not be null")
        StudyId.of(null)      | ItemDefinitionId.of("ITEM_ID") | aDefaultDemographicQuery() | Collections.emptyList() | singletonList("studyId.id")        | singletonList("must not be blank")
        StudyId.of("")        | ItemDefinitionId.of("ITEM_ID") | aDefaultDemographicQuery() | Collections.emptyList() | asList("studyId.id", "studyId.id") | asList("must not be blank", "size must be between 1 and 50")
        StudyId.of(" ")       | ItemDefinitionId.of("ITEM_ID") | aDefaultDemographicQuery() | Collections.emptyList() | singletonList("studyId.id")        | singletonList("must not be blank")
        null                  | ItemDefinitionId.of("ITEM_ID") | aDefaultDemographicQuery() | Collections.emptyList() | singletonList("studyId")           | singletonList("must not be null")
    }

    @Unroll
    def "Saving the item query mapping correctly"(ItemDefinitionId itemId, String subjectId, AgeUnit ageUnit) {
        given: "an existing study"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)

        and: """A request with 
                item id `#itemId`, 
                study id `#studyId`, 
                a demographics query with subject criterion containing a subject id #subjectId,  
                a date of birth to age projector with #ageUnit
                a gender projector"""

        def demographicQuery = aDemographicQueryWithoutCriterion()
        demographicQuery.addCriterion(SubjectCriterion.subjectIs(subjectId))
        def dateOfBirthToAgeProjector = new DateOfBirthToAgeProjector(ageUnit)
        def genderProjector = new GenderProjector()
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withItemId(itemId)
                .withQuery(demographicQuery)
                .withProjectors(asList(dateOfBirthToAgeProjector, genderProjector))
                .build()

        when: "saving item query mapping"
        saveItemQueryMapping.save(request)

        then: "the item query mapping is stored correctly"
        def study = studyMongoSnapshotRepository.findById(studyId.getId())
        study.get().getItemQueryMappings().size() == 1
        study.get().getItemQueryMappings().getAt(0).getItemId() == itemId.id
        JSONAssert.assertEquals(getItemQueryMapping(), study.get().getItemQueryMappings().getAt(0).getValue(), false)

        where:
        itemId                                 | subjectId       | ageUnit
        ItemDefinitionId.of("item-id-123-123") | "subjectId-679" | AgeUnit.YEARS

    }

    def "Saving an item query mapping should succeed for an unauthenticated user"() {
        given: "an existing study"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)

        and: "The user is not authenticated"
        withoutAuthenticatedUser()

        and: """A request with 
                item id `#itemId`, 
                study id `#studyId`, 
                a demographics query with subject criterion containing a subject id #subjectId,  
                a date of birth to age projector with #ageUnit
                a gender projector"""

        def demographicQuery = aDemographicQueryWithoutCriterion()
        demographicQuery.addCriterion(SubjectCriterion.subjectIs("subjectId-679"))
        def dateOfBirthToAgeProjector = new DateOfBirthToAgeProjector(AgeUnit.YEARS)
        def genderProjector = new GenderProjector()
        Request request = Request.newBuilder()
                .withStudyId(studyId)
                .withItemId(ItemDefinitionId.of("item-id-123-123"))
                .withQuery(demographicQuery)
                .withProjectors(asList(dateOfBirthToAgeProjector, genderProjector))
                .build()

        when: "saving item query mapping"
        saveItemQueryMapping.save(request)

        then: "The request should succeed"
        noExceptionThrown()
    }

    private String getItemQueryMapping() throws IOException {
        URL url = Resources.getResource(ITEM_QUERY_MAPPING_FILE)
        return Resources.toString(url, Charsets.UTF_8)
    }

}
