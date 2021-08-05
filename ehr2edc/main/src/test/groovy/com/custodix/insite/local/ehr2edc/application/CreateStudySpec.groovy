package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.command.CreateStudy
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot
import com.custodix.insite.local.ehr2edc.vocabulary.EventDefinitionId
import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.StudyODM
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.util.FileCopyUtils
import spock.lang.Title

import static com.custodix.insite.local.ehr2edc.command.CreateStudy.Request
import static com.custodix.insite.local.ehr2edc.command.CreateStudy.Response

@Title("Create Study")
class CreateStudySpec extends AbstractSpecification {
    private static final Resource ODM_SAMPLE = new ClassPathResource("samples/min-sample-study.xml")
    private static final Resource ODM_SAMPLE_REPEATING = new ClassPathResource("samples/studyWithRepeatingItemGroup.xml")
    private static final Resource ODM_SAMPLE_MISSING_INFO = new ClassPathResource("samples/missing-info-study.xml")
    private static final Resource ODM_SAMPLE_BLANK_NAME = new ClassPathResource("samples/blank-name-study.xml")
    private static final StudyId STUDY_ID = StudyId.of("EHR2EDC")
    private static final String STUDY_NAME = "Minimal Study"
    private static final String STUDY_DESCRIPTION = "EHR2EDC Description"
    private static final String META_DATA_DEFINITION_ID = "2788"
    private static final EventDefinitionId EVENT_ID = EventDefinitionId.of("00")
    private static final String EVENT_NAME = "Screening"
    private static final FormDefinitionId FORM_ID = FormDefinitionId.of("DM_DOB")
    private static final String GROUP_ID = "DM_DOB"
    private static final String ITEM_ID = "DM_DOB.BRTHYR"
    private static final String ITEM_DATATYPE = "date"

    @Autowired
    CreateStudy createStudy

    def "Create Study with an empty request"() {
        given: "An empty request"
        Request request = Request.newBuilder().build()

        when: "Creating a study"
        createStudy.create(request)

        then: "Indicate that the request was not valid"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.size() == 1
    }

    def "Create Study with a valid request"() {
        given: "A valid StudyODM"
        StudyODM studyODM = studyODM()
        and: "A request containing a valid StudyODM"
        Request request = Request.newBuilder()
                .withStudyODM(studyODM)
                .build()
        when: "creating a study"
        Response response = createStudy.create(request)

        then: "The study id is returned"
        response.studyId == STUDY_ID
        and: "A study is created with the correct values"
        def createdStudy = studyRepository.getStudyById(STUDY_ID)
        with(createdStudy) {
            studyId == STUDY_ID
            name == STUDY_NAME
            description == STUDY_DESCRIPTION
        }
        and: "The study metadata is correct"
        with(createdStudy.metadata) {
            id == META_DATA_DEFINITION_ID
            eventDefinitions.size() == 1
        }
        def eventDefinition = createdStudy.metadata.eventDefinitions[0]
        with(eventDefinition) {
            id == EVENT_ID
            it.name == EVENT_NAME
            formDefinitions.size() == 1
        }
        def formDefinition = eventDefinition.formDefinitions[0]
        with(formDefinition) {
            id == FORM_ID
            it.name == "Demographics"
            itemGroupDefinitions.size() == 1
        }
        def itemGroup = formDefinition.itemGroupDefinitions[0]
        with(itemGroup) {
            id.id == GROUP_ID
            it.name == "DM_DOB"
            itemDefinitions.size() == 16
        }
        def item = itemGroup.itemDefinitions[0]
        with(item) {
            id.id == ITEM_ID
            it.label.name == "BRTHYR"
            dataType == ITEM_DATATYPE
        }    }

    def "Create Study with a repeating item group"() {
        given: "A valid StudyODM"
        StudyODM studyODM = studyODMWithRepeatingItemGroup()
        and: "A request containing a valid StudyODM"
        Request request = Request.newBuilder()
                .withStudyODM(studyODM)
                .build()
        when: "creating a study"
        Response response = createStudy.create(request)

        then: "The study id is returned"
        response.studyId == STUDY_ID
        and: "A study is created with the correct values"
        def createdStudy = studyRepository.getStudyById(STUDY_ID)
        with(createdStudy) {
            studyId == STUDY_ID
            name == STUDY_NAME
            description == STUDY_DESCRIPTION
        }
        and: "The study metadata contains a repeating itemgroup"
        with(createdStudy.metadata) {
            id == META_DATA_DEFINITION_ID
            eventDefinitions.size() == 1
        }
        def eventDefinition = createdStudy.metadata.eventDefinitions[0]
        with(eventDefinition) {
            id == EVENT_ID
            it.name == EVENT_NAME
            formDefinitions.size() == 1
        }
        def formDefinition = eventDefinition.formDefinitions[0]
        with(formDefinition) {
            id == FORM_ID
            itemGroupDefinitions.size() == 1
        }
        def itemGroup = formDefinition.itemGroupDefinitions[0]
        with(itemGroup) {
            id.id == GROUP_ID
            itemDefinitions.size() == 1
            repeating == true
        }
        def item = itemGroup.itemDefinitions[0]
        with(item) {
            id.id == ITEM_ID
            dataType == ITEM_DATATYPE
        }
    }

    def "Create a study with missing info"() {
        given: "A StudyODM with missing info"
        StudyODM studyODM = studyODMMissingInfo()
        and: "A request containing the StudyODM"
        Request request = Request.newBuilder()
                .withStudyODM(studyODM)
                .build()
        when: "creating a study"
        createStudy.create(request)

        then: "An exception is thrown indicating that the StudyODM has missing info"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations.size() == 2
        ex.constraintViolations.each { it.message == "must not be null" }
    }

    def "Create a Study which already exists"() {
        given: "A known study"
        StudySnapshot knownStudy = generateKnownStudy(STUDY_ID, STUDY_NAME, STUDY_DESCRIPTION, USER_ID_KNOWN)
        and: "A valid StudyODM using the same studyId as the known study"
        StudyODM studyODM = studyODM()
        and: "A request containing a valid StudyODM"
        Request request = Request.newBuilder()
                .withStudyODM(studyODM)
                .build()

        when: "creating the study"
        createStudy.create(request)

        then: "An exception is thrown indicating a study with such an id already exists"
        def ex = thrown(UserException)
        ex.message == "StudyId[id=" + knownStudy.studyId.id + "] already exists"
    }

    def "Create a Study with a blank name"() {
        given: "A StudyODM which is missing a StudyName"
        StudyODM studyODM = studyODMBlankName()
        and: "A request containing a valid StudyODM"
        Request request = Request.newBuilder()
                .withStudyODM(studyODM)
                .build()

        when: "creating the study"
        createStudy.create(request)

        then: "An exception is thrown"
        def ex = thrown(UseCaseConstraintViolationException)
        ex
        ex.constraintViolations.size() == 1
        ex.constraintViolations.each {
            it.field == "name"
            it.message == "must not be blank"
        }
    }

    def "Create a study with item labels represented in ODM as Question/Text"() {
        given: "A request with ODM containing items with Question/Text"
        Request request = Request.newBuilder()
                .withStudyODM(studyODM())
                .build()
        when: "creating a study"
        Response response = createStudy.create(request)
        then: "the item labels are received by the repository"
        def createdStudy = studyRepository.getStudyById(response.getStudyId())
        def formDefinition = createdStudy.metadata.getFormDefinition(FORM_ID)
        def itemGroupDefinition = formDefinition.getItemGroupBy(GROUP_ID)
        def itemDefinition = itemGroupDefinition.getItemBy(ITEM_ID)
        itemDefinition.getDisplayLabel(Locale.ENGLISH) == "Year of Birth"
        itemDefinition.getDisplayLabel(Locale.JAPANESE) == "生年"
    }

    def "Create Study should succeed for an unauthenticated user"() {
        given: "A valid StudyODM"
        StudyODM studyODM = studyODM()
        and: "A request containing a valid StudyODM"
        Request request = Request.newBuilder()
                .withStudyODM(studyODM)
                .build()
        and: "The user is not authenticated"
        withoutAuthenticatedUser()

        when: "creating a study"
        createStudy.create(request)

        then: "The request should succeed"
        noExceptionThrown()
    }

    def studyODM() {
        return StudyODM.of(new String(FileCopyUtils.copyToByteArray(ODM_SAMPLE.getFile())))
    }

    def studyODMWithRepeatingItemGroup() {
        return StudyODM.of(new String(FileCopyUtils.copyToByteArray(ODM_SAMPLE_REPEATING.getFile())))
    }

    def studyODMMissingInfo() {
        return StudyODM.of(new String(FileCopyUtils.copyToByteArray(ODM_SAMPLE_MISSING_INFO.getFile())))
    }

    def studyODMBlankName() {
        return StudyODM.of(new String(FileCopyUtils.copyToByteArray(ODM_SAMPLE_BLANK_NAME.getFile())))
    }
}
