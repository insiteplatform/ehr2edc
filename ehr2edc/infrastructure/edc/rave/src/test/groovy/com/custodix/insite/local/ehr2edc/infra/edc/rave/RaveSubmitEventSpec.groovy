package com.custodix.insite.local.ehr2edc.infra.edc.rave

import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.StudyConnectionRepository
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot
import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshot
import com.custodix.insite.local.ehr2edc.submitted.SubmittedMeasurementUnitReference
import com.custodix.insite.local.ehr2edc.vocabulary.*
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.matching.EqualToXmlPattern
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType.SUBMIT_EVENT
import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

class RaveSubmitEventSpec extends AbstractSpecification {
    private static final EDCSubjectReference EDC_REFERENCE = EDCSubjectReference.newBuilder().withId("subjectId").build()
    private static final def EXTERNAL_SITE_ID = ExternalSiteId.of("123456")
    private static final StudyId STUDY_ID_OVERRIDE = StudyId.of("overridden-study-id")

    @Rule
    WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort())

    @Autowired
    RaveEDCStudyGateway raveEDCStudyGateway

    @MockBean
    StudyConnectionRepository studyConnectionRepository

    def "Submit a reviewed event with a single form"() {
        given: "A reviewed event with a single form for an existing subject"
        def item1 = anItem("ITEM-1", "VALUE-1", false)
        def item2 = anItem("ITEM-2", "VALUE-2", false)
        def item3 = anItem("ITEM-3", "VALUE-3", false)
        def group = anItemGroup("GROUP-1", [item1, item2, item3])
        def form = aForm("FORM-1", [group])
        def event = anEvent([form])
        SubjectSnapshot subject = SubjectSnapshot.newBuilder()
                .withEdcSubjectReference(EDC_REFERENCE)
                .withSubjectId(event.subjectId)
                .build()
        and: "The subject is part of a study"
        def study = Study.restoreSnapshot(StudySnapshot.newBuilder()
                .withStudyId(event.studyId)
                .withSubjects([subject])
                .build())
        and: "The study has a configured EDC connection to submit data"
        def connection = defaultExternalEDCConnectionBuilder().build()
        studyConnectionRepository.findStudyConnectionByIdAndType(study.studyId, SUBMIT_EVENT) >> Optional.ofNullable(connection)
        and: "Rave will return ok"
        stubPostOk()

        when: "I submit the reviewed event"
        raveEDCStudyGateway.submitReviewedEvent(connection, event, study)

        then: "The EDC URL was called with correct ODM XML"
        checkOdmXml(connection.clinicalDataURI, "rave/submitevent/SubmitReviewedEvent-singleForm.xml")
    }

    def "Submit a reviewed event with multiple forms"() {
        given: "A reviewed event with multiple forms for an existing subject"
        def item1 = anItem("ITEM-1", "VALUE-1", false)
        def item2 = anItem("ITEM-2", "VALUE-2", false)
        def item3 = anItem("ITEM-3", "VALUE-3", false)
        def group1 = anItemGroup("GROUP-1", [item1, item2, item3])
        def form1 = aForm("FORM-1", [group1])
        def item4 = anItem("ITEM-4", "VALUE-4", false)
        def item5 = anItem("ITEM-5", "VALUE-5", false)
        def item6 = anItem("ITEM-6", "VALUE-6", false)
        def group2 = anItemGroup("GROUP-2", [item4, item5, item6])
        def form2 = aForm("FORM-2", [group2])
        def event = anEvent([form1, form2])
        SubjectSnapshot subject = SubjectSnapshot.newBuilder()
                .withEdcSubjectReference(EDC_REFERENCE)
                .withSubjectId(event.subjectId)
                .build()
        and: "The subject is part of a study"
        def study = Study.restoreSnapshot(StudySnapshot.newBuilder()
                .withStudyId(event.studyId)
                .withSubjects([subject])
                .build())
        and: "The study has a configured EDC connection to submit data"
        def connection = defaultExternalEDCConnectionBuilder().build()
        studyConnectionRepository.findStudyConnectionByIdAndType(study.studyId, SUBMIT_EVENT) >> Optional.ofNullable(connection)
        and: "Rave will return ok"
        stubPostOk()

        when: "I submit the reviewed event"
        raveEDCStudyGateway.submitReviewedEvent(connection, event, study)

        then: "The EDC URL was called with correct ODM XML"
        checkOdmXml(connection.clinicalDataURI, "rave/submitevent/SubmitReviewedEvent-multiForm.xml")
    }

    def "Submit a reviewed event can override the studyId from the configured connection"() {
        given: "A reviewed event for an existing subject"
        def item1 = anItem("ITEM-1", "VALUE-1", false)
        def item2 = anItem("ITEM-2", "VALUE-2", false)
        def item3 = anItem("ITEM-3", "VALUE-3", false)
        def group = anItemGroup("GROUP-1", [item1, item2, item3])
        def form = aForm("FORM-1", [group])
        def event = anEvent([form])
        SubjectSnapshot subject = SubjectSnapshot.newBuilder()
                .withEdcSubjectReference(EDC_REFERENCE)
                .withSubjectId(event.subjectId)
                .build()
        and: "The subject is part of a study"
        def study = Study.restoreSnapshot(StudySnapshot.newBuilder()
                .withStudyId(event.studyId)
                .withSubjects([subject])
                .build())
        and: "The study has a configured EDC connection to submit data which is configured to override the study id"
        def connection = defaultExternalEDCConnectionBuilder()
                .withStudyIdOVerride(STUDY_ID_OVERRIDE)
                .build()
        studyConnectionRepository.findStudyConnectionByIdAndType(study.studyId, SUBMIT_EVENT) >> Optional.ofNullable(connection)
        and: "Rave will return ok"
        stubPostOk()

        when: "I submit the reviewed event"
        raveEDCStudyGateway.submitReviewedEvent(connection, event, study)

        then: "The EDC URL was called with correct ODM XML"
        checkOdmXml(connection.clinicalDataURI, "rave/submitevent/SubmitReviewedEvent-overrideStudyId.xml")
    }

    def "Submit a reviewed event which is nested in a parent folder"() {
        given: "A reviewed event which is nested in a parent folder for an existing subject"
        def item1 = anItem("ITEM-1", "VALUE-1", false)
        def item2 = anItem("ITEM-2", "VALUE-2", false)
        def item3 = anItem("ITEM-3", "VALUE-3", false)
        def group = anItemGroup("GROUP-1", [item1, item2, item3])
        def form = aForm("FORM-1", [group])
        def event = anEventBuilder([form])
                .withEventParentId("EVT-PARENT").build()
        SubjectSnapshot subject = SubjectSnapshot.newBuilder()
                .withEdcSubjectReference(EDC_REFERENCE)
                .withSubjectId(event.subjectId)
                .build()
        and: "The subject is part of a study"
        def study = Study.restoreSnapshot(StudySnapshot.newBuilder()
                .withStudyId(event.studyId)
                .withSubjects([subject])
                .build())
        and: "The study has a configured EDC connection to submit data"
        def connection = defaultExternalEDCConnectionBuilder().build()
        studyConnectionRepository.findStudyConnectionByIdAndType(study.studyId, SUBMIT_EVENT) >> Optional.ofNullable(connection)
        and: "Rave will return ok"
        stubPostOk()

        when: "I submit the reviewed event"
        raveEDCStudyGateway.submitReviewedEvent(connection, event, study)

        then: "The EDC URL was called with correct ODM XML"
        checkOdmXml(connection.clinicalDataURI, "rave/submitevent/SubmitReviewedEvent-nestedEvent.xml")
    }

    def "Submit a reviewed event with a form that references a local laboratory"() {
        given: "A reviewed event with a form that references a local laboratory for an existing subject"
        def item1 = anItem("ITEM-1", "VALUE-1", false)
        def item2 = anItem("ITEM-2", "VALUE-2", false)
        def item3 = anItem("ITEM-3", "VALUE-3", false)
        def group = anItemGroup("GROUP-1", [item1, item2, item3])
        def form = aFormBuilder("FORM-1", [group])
                .withLocalLab(LabName.of("My Lab Name")).build()
        def event = anEvent([form])
        SubjectSnapshot subject = SubjectSnapshot.newBuilder()
                .withEdcSubjectReference(EDC_REFERENCE)
                .withSubjectId(event.subjectId)
                .build()
        and: "The subject is part of a study"
        def study = Study.restoreSnapshot(StudySnapshot.newBuilder()
                .withStudyId(event.studyId)
                .withSubjects([subject])
                .build())
        and: "The study has a configured EDC connection to submit data"
        def connection = defaultExternalEDCConnectionBuilder().build()
        studyConnectionRepository.findStudyConnectionByIdAndType(study.studyId, SUBMIT_EVENT) >> Optional.ofNullable(connection)
        and: "Rave will return ok"
        stubPostOk()

        when: "I submit the reviewed event"
        raveEDCStudyGateway.submitReviewedEvent(connection, event, study)

        then: "The EDC URL was called with correct ODM XML"
        checkOdmXml(connection.clinicalDataURI, "rave/submitevent/SubmitReviewedEvent-labReference.xml")
    }

    def "Submit a reviewed event with a form containing an item with a measurement unit reference that should be submitted to the EDC"() {
        given: "A reviewed event with a form containing an item with a measurement unit reference that should be submitted to the EDC for an existing subject"
        def unit = SubmittedMeasurementUnitReference.newBuilder()
                .withId("13899.CM/IN.cm")
                .withSubmittedToEDC(true).build()
        def item1 = anItemBuilder("ITEM-1", "VALUE-1", false)
                .withSubmittedMeasurementUnitReference(unit).build()
        def item2 = anItem("ITEM-2", "VALUE-2", false)
        def item3 = anItem("ITEM-3", "VALUE-3", false)
        def group = anItemGroup("GROUP-1", [item1, item2, item3])
        def form = aForm("FORM-1", [group])
        def event = anEvent([form])
        SubjectSnapshot subject = SubjectSnapshot.newBuilder()
                .withEdcSubjectReference(EDC_REFERENCE)
                .withSubjectId(event.subjectId)
                .build()
        and: "The subject is part of a study"
        def study = Study.restoreSnapshot(StudySnapshot.newBuilder()
                .withStudyId(event.studyId)
                .withSubjects([subject])
                .build())
        and: "The study has a configured EDC connection to submit data"
        def connection = defaultExternalEDCConnectionBuilder().build()
        studyConnectionRepository.findStudyConnectionByIdAndType(study.studyId, SUBMIT_EVENT) >> Optional.ofNullable(connection)
        and: "Rave will return ok"
        stubPostOk()

        when: "I submit the reviewed event"
        raveEDCStudyGateway.submitReviewedEvent(connection, event, study)

        then: "The EDC URL was called with correct ODM XML"
        checkOdmXml(connection.clinicalDataURI, "rave/submitevent/SubmitReviewedEvent-itemWithUnitReference.xml")
    }

    def "Submit a reviewed event with a form containing an item with a measurement unit reference that should not be submitted to the EDC"() {
        given: "A reviewed event with a form containing an item with a measurement unit reference that should not be submitted to the EDC for an existing subject"
        def unit = SubmittedMeasurementUnitReference.newBuilder()
                .withId("13899.CM/IN.cm")
                .withSubmittedToEDC(false).build()
        def item1 = anItemBuilder("ITEM-1", "VALUE-1", false)
                .withSubmittedMeasurementUnitReference(unit).build()
        def item2 = anItem("ITEM-2", "VALUE-2", false)
        def item3 = anItem("ITEM-3", "VALUE-3", false)
        def group = anItemGroup("GROUP-1", [item1, item2, item3])
        def form = aForm("FORM-1", [group])
        def event = anEvent([form])
        SubjectSnapshot subject = SubjectSnapshot.newBuilder()
                .withEdcSubjectReference(EDC_REFERENCE)
                .withSubjectId(event.subjectId)
                .build()
        and: "The subject is part of a study"
        def study = Study.restoreSnapshot(StudySnapshot.newBuilder()
                .withStudyId(event.studyId)
                .withSubjects([subject])
                .build())
        and: "The study has a configured EDC connection to submit data"
        def connection = defaultExternalEDCConnectionBuilder().build()
        studyConnectionRepository.findStudyConnectionByIdAndType(study.studyId, SUBMIT_EVENT) >> Optional.ofNullable(connection)
        and: "Rave will return ok"
        stubPostOk()

        when: "I submit the reviewed event"
        raveEDCStudyGateway.submitReviewedEvent(connection, event, study)

        then: "The EDC URL was called with correct ODM XML"
        checkOdmXml(connection.clinicalDataURI, "rave/submitevent/SubmitReviewedEvent-singleForm.xml")
    }

    @Unroll
    def "When the EDC submit endpoint responds with response status '#responseStatus', reason '#reason' and location '#location', throw a UseCaseConstraintViolationException with field '#field' and message the reason"(HttpStatus responseStatus, String reason, String location, String field) {
        given: "A reviewed event for an existing subject"
        def item1 = anItem("ITEM-1", "VALUE-1", false)
        def item2 = anItem("ITEM-2", "VALUE-2", false)
        def item3 = anItem("ITEM-3", "VALUE-3", false)
        def group = anItemGroup("GROUP-1", [item1, item2, item3])
        def form = aForm("FORM-1", [group])
        def event = anEvent([form])
        SubjectSnapshot subject = SubjectSnapshot.newBuilder()
                .withEdcSubjectReference(EDC_REFERENCE)
                .withSubjectId(event.subjectId)
                .build()
        and: "The subject is part of a study"
        def study = Study.restoreSnapshot(StudySnapshot.newBuilder()
                .withStudyId(event.studyId)
                .withSubjects([subject])
                .build())
        and: "The study has a configured EDC connection to submit data"
        def connection = defaultExternalEDCConnectionBuilder().build()
        studyConnectionRepository.findStudyConnectionByIdAndType(study.studyId, SUBMIT_EVENT) >> Optional.ofNullable(connection)
        and: "The EDC returns a response with status '#responseStatus', reason '#reason' and location '#location'"
        stubPostError(responseStatus, reason, location)

        when: "I submit the reviewed event"
        raveEDCStudyGateway.submitReviewedEvent(connection, event, study)

        then: "An error is thrown indicating the violation on field '#field'"
        def ex = thrown(UseCaseConstraintViolationException)
        ex.constraintViolations[0].field == field
        ex.constraintViolations[0].message == reason

        where:
        responseStatus         | reason                           | location                                                                                         || field
        HttpStatus.BAD_REQUEST | "Data not in dictionary."        | "/ODM/ClinicalData[1]/SubjectData[1]/StudyEventData[1]/FormData[1]/ItemGroupData[1]/ItemData[1]" || "instance-ITEM-1"
        HttpStatus.NOT_FOUND   | "Unit Dictionary does not exist" | "/ODM/ClinicalData[1]/SubjectData[1]/StudyEventData[1]/FormData[1]"                              || "instance-FORM-1"
        HttpStatus.NOT_FOUND   | "Unit Dictionary does not exist" | "/ODM/ClinicalData[1]/SubjectData[1]/StudyEventData[1]/FormData[1]/ItemGroupData[1]"             || "instance-GROUP-1"
        HttpStatus.NOT_FOUND   | "Unit Dictionary does not exist" | "/ODM/ClinicalData[1]/SubjectData[1]/StudyEventData[1]/FormData[1]/ItemGroupData[1]/ItemData[1]" || "instance-ITEM-1"
        HttpStatus.NOT_FOUND   | "Unit Dictionary does not exist" | "/ODM/ClinicalData[1]/SubjectData[1]/StudyEventData[1]/FormData[1]/ItemGroupData[1]/ItemData[3]" || "instance-ITEM-3"
    }

    private ExternalEDCConnection edcConnectionData() {
        return defaultExternalEDCConnectionBuilder().build()
    }

    private ExternalEDCConnection.Builder defaultExternalEDCConnectionBuilder() {
        return ExternalEDCConnection.newBuilder()
                .withStudyId(STUDY_ID)
                .withConnectionType(SUBMIT_EVENT)
                .withEdcSystem(EDCSystem.RAVE)
                .withExternalSiteId(EXTERNAL_SITE_ID)
                .withClinicalDataURI(new URI("http://localhost:${wireMockRule.port()}/RaveWebServices/webservice.aspx?PostODMClinicalData"))
                .withUsername("user1")
                .withPassword("user1Pass")
                .withEnabled(true)
    }

    private void stubPostError(HttpStatus status, String reason, String location) {
        stubFor(post(urlPathMatching(edcConnectionData().clinicalDataURI.getPath()))
                .willReturn(aResponse()
                        .withStatus(status.value())
                        .withHeader("Content-Type", MediaType.TEXT_XML_VALUE)
                        .withBody(raveErrorResponseBodyWith(reason, location))))
    }

    private void stubPostOk() {
        stubFor(post(urlPathMatching(edcConnectionData().clinicalDataURI.getPath()))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())))
    }

    private void checkOdmXml(URI edcServiceURI, String xmlFile) {
        verify(postRequestedFor(urlEqualTo("${edcServiceURI.getPath()}?${edcServiceURI.getQuery()}"))
                .withRequestBody(equalXmlUsingWiremockXmlRequestMatchingPlaceholders(xmlFile)))
    }

    private EqualToXmlPattern equalXmlUsingWiremockXmlRequestMatchingPlaceholders(String xmlFile) {
        equalToXml(readSample(xmlFile), true, "\\[\\[", "]]")
    }

    private String raveErrorResponseBodyWith(String reason, String location) {
        return "<Response ReferenceNumber=\"51a65d5d-2a61-4237-8dd8-86f7dd9aefb5\" " +
                "InboundODMFileOID=\"Example-1\" " +
                "IsTransactionSuccessful=\"0\" " +
                "ReasonCode=\"RWS00047\" " +
                "ErrorOriginLocation=\"" + location + "\" " +
                "SuccessStatistics=\"Rave objects touched: Subjects=0; Folders=0; Forms=0; Fields=0; LogLines=0\" " +
                "ErrorClientResponseMessage=\"" + reason + "\"></Response>"
    }
}
