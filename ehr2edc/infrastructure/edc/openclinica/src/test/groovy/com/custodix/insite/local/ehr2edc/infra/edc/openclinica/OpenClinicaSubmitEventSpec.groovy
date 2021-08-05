package com.custodix.insite.local.ehr2edc.infra.edc.openclinica

import com.custodix.insite.local.ehr2edc.Study
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshot
import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshotObjectMother
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItem
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItemObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.*
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import com.github.tomakehurst.wiremock.matching.MatchResult
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import com.github.tomakehurst.wiremock.stubbing.Scenario
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.util.UriTemplate
import spock.lang.Narrative

import java.time.Instant
import java.time.LocalDate

import static com.custodix.insite.local.ehr2edc.Study.restoreSnapshot
import static com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolation.constraintViolation
import static com.custodix.insite.local.ehr2edc.snapshots.StudySnapshotObjectMother.aDefaultStudySnapshotBuilder
import static com.custodix.insite.local.ehr2edc.submitted.SubmittedEventObjectMother.aDefaultSubmittedEventBuilder
import static com.custodix.insite.local.ehr2edc.submitted.SubmittedFormObjectMother.aDefaultSubmittedFormBuilder
import static com.custodix.insite.local.ehr2edc.submitted.SubmittedItemGroupObjectMother.aDefaultSubmittedItemGroupBuilder
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType.SUBMIT_EVENT
import static com.github.tomakehurst.wiremock.client.WireMock.*
import static org.springframework.test.util.ReflectionTestUtils.setField

@Narrative("""
OpenClinica resolves data import requests asynchronously.
The POST to the import endpoint returns a response which is one of two possibilities:
    - an error code when something went wrong during the initial setup or launching of the import job
    - a job uuid
To know whether the import was successful for all items or not, one must query a second endpoint with the job uuid.
The returned response is one of two possibilities:
    - an error code indicating that the job uuid is invalid or that the job has not completed yet
    - a CSV formatted import report providing status information
As long as the job has not completed yet, the endpoint must be polled periodically.
The import report can contain errors which we categorize in two types:
    - Form-, itemgroup or item-level errors. These are errors related to the particular forms/itemgroups/items that were submitted.
        We want to indicate to the user which forms/itemgroups/items are in error and their corresponding error messages.
    - Subject- or event-level errors. These are general errors that aren't related to the particular forms/itemgroups/items submitted.
        When such an error occurs, the report will only ever contain 1 record. There can never be more than 1 of these errors.
        We want to simply display this error to the user.
""")
class OpenClinicaSubmitEventSpec extends AbstractOpenClinicaSpecification {
    private static final String JOB_UUID = "d016011a-c187-4b0f-b71f-92470d0f3ad6"
    private static final UriTemplate SUBMIT_URI_TEMPLATE = new UriTemplate("http://localhost:{port}/OpenClinica/pages/auth/api/clinicaldata/import")
    private static final UriTemplate JOB_REPORT_URI_TEMPLATE = new UriTemplate("http://localhost:{port}/OpenClinica/pages/auth/api/jobs/{job}/downloadFile")
    private static final String EDC_RESPONSE = "job uuid: " + JOB_UUID
    private static final EDCSubjectReference EDC_SUBJECT_REFERENCE = EDCSubjectReference.of("001")
    private static final StudyId STUDY_ID_OVERRIDE = StudyId.of("overridden-study-id")
    private static final LocalDate REFERENCE_DATE = LocalDate.parse("2020-01-07")
    private static final Instant SUBMITTED_DATE = Instant.parse("2020-01-07T08:00:00Z")

    @Autowired
    private OpenClinicaImportJobClient importJobClient

    def setup() {
        setField(importJobClient, "importJobUri", new UriTemplate(JOB_REPORT_URI_TEMPLATE.toString().replace("{port}", "${wireMockRule.port()}")))
    }

    def "Submit reviewed event"() {
        given: "a subject"
        SubjectSnapshot subject = SubjectSnapshotObjectMother.aDefaultSubjectSnapshotBuilder()
                .withEdcSubjectReference(EDC_SUBJECT_REFERENCE)
                .build()
        and: "a study for which the subject is registered"
        Study study = restoreSnapshot(aDefaultStudySnapshotBuilder()
                .withSubjects([subject])
                .build())
        and: "a reviewed event for the study"
        SubmittedEvent event = aSubmittedEventWithThreeItems(study.studyId, subject.subjectId)
        and: "the study has a configured EDC connection for OpenClinica"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aBasicStudyConnectionBuilder(study.studyId, SUBMIT_EVENT)
                .withEdcSystem(EDCSystem.OPEN_CLINICA)
                .withClinicalDataURI(getSubmitUri())
                .build()
        and: "The authentication token provider returns a valid token"
        authenticationTokenProvider.get(connection) >> AUTHENTICATION_TOKEN
        and: "The EDC returns a job uuid when called with a valid submit request"
        stubSubmitOk("openclinica/submitevent/SubmitReviewedEvent.xml")
        and: "The EDC returns a data import report when called with the correct job uuid, which indicates the items were submitted successfully"
        stubJobReportOk("openclinica/submitevent/job-report-success.csv")

        when: "I submit the reviewed event"
        openClinicaEDCStudyGateway.submitReviewedEvent(connection, event, study)

        then: "The EDC is called with a valid submit request"
        verify(1, postRequestedFor(urlEqualTo(getSubmitUri().getPath())))
        and: "The EDC is called with a valid job report request"
        verify(1, getRequestedFor(urlEqualTo(getJobReportUri().getPath())))
    }

    def "Submit reviewed event using a connection with an overridden study id"() {
        given: "a subject"
        SubjectSnapshot subject = SubjectSnapshotObjectMother.aDefaultSubjectSnapshotBuilder()
                .withEdcSubjectReference(EDC_SUBJECT_REFERENCE)
                .build()
        and: "a study for which the subject is registered"
        Study study = restoreSnapshot(aDefaultStudySnapshotBuilder()
                .withSubjects([subject])
                .build())
        and: "a reviewed event for the study"
        SubmittedEvent event = aSubmittedEventWithThreeItems(study.studyId, subject.subjectId)
        and: "the study has a configured EDC connection for OpenClinica with an overridden study id"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aBasicStudyConnectionBuilder(study.studyId, SUBMIT_EVENT)
                .withEdcSystem(EDCSystem.OPEN_CLINICA)
                .withClinicalDataURI(getSubmitUri())
                .withStudyIdOVerride(STUDY_ID_OVERRIDE)
                .build()
        and: "The authentication token provider returns a valid token"
        authenticationTokenProvider.get(connection) >> AUTHENTICATION_TOKEN
        and: "The EDC returns a job uuid when called with a valid submit request"
        stubSubmitOk("openclinica/submitevent/SubmitReviewedEvent-studyIdOverride.xml")
        and: "The EDC returns a data import report when called with the correct job uuid, which indicates the items were submitted successfully"
        stubJobReportOk("openclinica/submitevent/job-report-success.csv")

        when: "I submit the reviewed event"
        openClinicaEDCStudyGateway.submitReviewedEvent(connection, event, study)

        then: "The EDC is called with a valid submit request"
        verify(1, postRequestedFor(urlEqualTo(getSubmitUri().getPath())))
        and: "The EDC is called with a valid job report request"
        verify(1, getRequestedFor(urlEqualTo(getJobReportUri().getPath())))
    }

    def "Support an import job report with 2 trailing newlines, because the endpoint returns it that way"() {
        given: "a subject"
        SubjectSnapshot subject = SubjectSnapshotObjectMother.aDefaultSubjectSnapshotBuilder()
                .withEdcSubjectReference(EDC_SUBJECT_REFERENCE)
                .build()
        and: "a study for which the subject is registered"
        Study study = restoreSnapshot(aDefaultStudySnapshotBuilder()
                .withSubjects([subject])
                .build())
        and: "a reviewed event for the study"
        SubmittedEvent event = aSubmittedEventWithThreeItems(study.studyId, subject.subjectId)
        and: "the study has a configured EDC connection for OpenClinica"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aBasicStudyConnectionBuilder(study.studyId, SUBMIT_EVENT)
                .withEdcSystem(EDCSystem.OPEN_CLINICA)
                .withClinicalDataURI(getSubmitUri())
                .build()
        and: "The authentication token provider returns a valid token"
        authenticationTokenProvider.get(connection) >> AUTHENTICATION_TOKEN
        and: "The EDC returns a job uuid when called with a valid submit request"
        stubSubmitOk("openclinica/submitevent/SubmitReviewedEvent.xml")
        and: "The EDC returns a data import report with 2 trailing newlines"
        stubJobReportOk("openclinica/submitevent/job-report-trailing-newlines.csv")

        when: "I submit the reviewed event"
        openClinicaEDCStudyGateway.submitReviewedEvent(connection, event, study)

        then: "The EDC is called with a valid submit request"
        verify(1, postRequestedFor(urlEqualTo(getSubmitUri().getPath())))
        and: "The EDC is called with a valid job report request"
        verify(1, getRequestedFor(urlEqualTo(getJobReportUri().getPath())))
    }

    def "Throw an exception when the import call responds with an error response and an error code"(String errorCode, String expectedErrorMessage) {
        given: "a subject"
        SubjectSnapshot subject = SubjectSnapshotObjectMother.aDefaultSubjectSnapshotBuilder()
                .withEdcSubjectReference(EDC_SUBJECT_REFERENCE)
                .build()
        and: "a study for which the subject is registered"
        Study study = restoreSnapshot(aDefaultStudySnapshotBuilder()
                .withSubjects([subject])
                .build())
        and: "a reviewed event for the study"
        SubmittedEvent event = aSubmittedEventWithThreeItems(study.studyId, subject.subjectId)
        and: "the study has a configured EDC connection for OpenClinica"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aBasicStudyConnectionBuilder(study.studyId, SUBMIT_EVENT)
                .withEdcSystem(EDCSystem.OPEN_CLINICA)
                .withClinicalDataURI(getSubmitUri())
                .build()
        and: "The authentication token provider returns a valid token"
        authenticationTokenProvider.get(connection) >> AUTHENTICATION_TOKEN
        and: "The EDC returns an error response with an error code"
        stubSubmitBadRequestErrorCode(errorCode)

        when: "I submit the reviewed event"
        openClinicaEDCStudyGateway.submitReviewedEvent(connection, event, study)

        then: "An exception is thrown with error message '#expectedErrorMessage'"
        def exception = thrown UserException
        exception.message == expectedErrorMessage

        where:
        errorCode                 || expectedErrorMessage
        "errorCode.studyNotExist" || "No study with matching OID found on the EDC"
        "errorCode.unknownCode"   || "errorCode.unknownCode"
    }

    def "Throw an exception when the import call responds with a success response and an error code"(String errorCode, String expectedErrorMessage) {
        given: "a subject"
        SubjectSnapshot subject = SubjectSnapshotObjectMother.aDefaultSubjectSnapshotBuilder()
                .withEdcSubjectReference(EDC_SUBJECT_REFERENCE)
                .build()
        and: "a study for which the subject is registered"
        Study study = restoreSnapshot(aDefaultStudySnapshotBuilder()
                .withSubjects([subject])
                .build())
        and: "a reviewed event for the study"
        SubmittedEvent event = aSubmittedEventWithThreeItems(study.studyId, subject.subjectId)
        and: "the study has a configured EDC connection for OpenClinica"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aBasicStudyConnectionBuilder(study.studyId, SUBMIT_EVENT)
                .withEdcSystem(EDCSystem.OPEN_CLINICA)
                .withClinicalDataURI(getSubmitUri())
                .build()
        and: "The authentication token provider returns a valid token"
        authenticationTokenProvider.get(connection) >> AUTHENTICATION_TOKEN
        and: "The EDC returns a success response with an error code"
        stubSubmitOkErrorCode(errorCode)

        when: "I submit the reviewed event"
        openClinicaEDCStudyGateway.submitReviewedEvent(connection, event, study)

        then: "An exception is thrown with error message '#expectedErrorMessage'"
        def exception = thrown UserException
        exception.message == expectedErrorMessage

        where:
        errorCode                 || expectedErrorMessage
        "errorCode.studyNotExist" || "No study with matching OID found on the EDC"
        "errorCode.unknownCode"   || "errorCode.unknownCode"
    }

    def "Periodically poll the import job until it is completed"() {
        given: "a subject"
        SubjectSnapshot subject = SubjectSnapshotObjectMother.aDefaultSubjectSnapshotBuilder()
                .withEdcSubjectReference(EDC_SUBJECT_REFERENCE)
                .build()
        and: "a study for which the subject is registered"
        Study study = restoreSnapshot(aDefaultStudySnapshotBuilder()
                .withSubjects([subject])
                .build())
        and: "a reviewed event for the study"
        SubmittedEvent event = aSubmittedEventWithThreeItems(study.studyId, subject.subjectId)
        and: "the study has a configured EDC connection for OpenClinica"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aBasicStudyConnectionBuilder(study.studyId, SUBMIT_EVENT)
                .withEdcSystem(EDCSystem.OPEN_CLINICA)
                .withClinicalDataURI(getSubmitUri())
                .build()
        and: "The authentication token provider returns a valid token"
        authenticationTokenProvider.get(connection) >> AUTHENTICATION_TOKEN
        and: "The EDC returns a job uuid when called with a valid submit request"
        stubSubmitOk("openclinica/submitevent/SubmitReviewedEvent.xml")
        and: "The EDC returns a data import report the 2nd time it is polled with the correct job uuid"
        stubJobReportOkOnSecondCall()

        when: "I submit the reviewed event"
        openClinicaEDCStudyGateway.submitReviewedEvent(connection, event, study)

        then: "The EDC is called with a valid submit request"
        verify(1, postRequestedFor(urlEqualTo(getSubmitUri().getPath())))
        and: "The EDC is called 2 times with a valid job report request"
        verify(2, getRequestedFor(urlEqualTo(getJobReportUri().getPath())))
    }

    def "Throw an exception when the import job report contains errors on the form, itemgroup or item level"(String jobReportFile, Collection expectedViolations) {
        given: "a subject"
        SubjectSnapshot subject = SubjectSnapshotObjectMother.aDefaultSubjectSnapshotBuilder()
                .withEdcSubjectReference(EDC_SUBJECT_REFERENCE)
                .build()
        and: "a study for which the subject is registered"
        Study study = restoreSnapshot(aDefaultStudySnapshotBuilder()
                .withSubjects([subject])
                .build())
        and: "a reviewed event for the study"
        SubmittedEvent event = aSubmittedEventWithThreeItems(study.studyId, subject.subjectId)
        and: "the study has a configured EDC connection for OpenClinica"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aBasicStudyConnectionBuilder(study.studyId, SUBMIT_EVENT)
                .withEdcSystem(EDCSystem.OPEN_CLINICA)
                .withClinicalDataURI(getSubmitUri())
                .build()
        and: "the authentication token provider returns a valid token"
        authenticationTokenProvider.get(connection) >> AUTHENTICATION_TOKEN
        and: "the EDC returns a job uuid when called with a valid submit request"
        stubSubmitOk("openclinica/submitevent/SubmitReviewedEvent.xml")
        and: "the EDC returns a data import report when called with the correct job uuid"
        stubJobReportOk("openclinica/submitevent/${jobReportFile}")

        when: "I submit the reviewed event"
        openClinicaEDCStudyGateway.submitReviewedEvent(connection, event, study)

        then: "an exception is thrown indicating the particular form, itemgroup or item that failed with an appropriate error message"
        def exception = thrown UseCaseConstraintViolationException
        exception.constraintViolations.toArray() == expectedViolations.toArray()

        where:
        jobReportFile                     || expectedViolations
        "job-report-failed-form.csv"      || [constraintViolation("populatedFormId", "No form with matching OID found in the study on the EDC")]
        "job-report-failed-itemgroup.csv" || [constraintViolation("populatedItemGroupId", "No item group with matching OID found in the study on the EDC")]
        "job-report-failed-item.csv"      || [constraintViolation("populatedItemId-2", "errorCode.unknownError"), constraintViolation("populatedItemId-3", "No item with matching OID found in the study on the EDC")]
    }

    def "Throw an exception when the import job report contains an error on the subject or event level"(String jobReportFile, String expectedErrorMessage) {
        given: "a subject"
        SubjectSnapshot subject = SubjectSnapshotObjectMother.aDefaultSubjectSnapshotBuilder()
                .withEdcSubjectReference(EDC_SUBJECT_REFERENCE)
                .build()
        and: "a study for which the subject is registered"
        Study study = restoreSnapshot(aDefaultStudySnapshotBuilder()
                .withSubjects([subject])
                .build())
        and: "a reviewed event for the study"
        SubmittedEvent event = aSubmittedEventWithThreeItems(study.studyId, subject.subjectId)
        and: "the study has a configured EDC connection for OpenClinica"
        ExternalEDCConnection connection = StudyConnectionObjectMother.aBasicStudyConnectionBuilder(study.studyId, SUBMIT_EVENT)
                .withEdcSystem(EDCSystem.OPEN_CLINICA)
                .withClinicalDataURI(getSubmitUri())
                .build()
        and: "the authentication token provider returns a valid token"
        authenticationTokenProvider.get(connection) >> AUTHENTICATION_TOKEN
        and: "the EDC returns a job uuid when called with a valid submit request"
        stubSubmitOk("openclinica/submitevent/SubmitReviewedEvent.xml")
        and: "the EDC returns a data import report when called with the correct job uuid"
        stubJobReportOk("openclinica/submitevent/${jobReportFile}")

        when: "I submit the reviewed event"
        openClinicaEDCStudyGateway.submitReviewedEvent(connection, event, study)

        then: "an exception is thrown with an appropriate error message"
        def exception = thrown UserException
        exception.message == expectedErrorMessage

        where:
        jobReportFile                   || expectedErrorMessage
        "job-report-failed-subject.csv" || "No matching participant is found for the given study on the EDC"
        "job-report-failed-event.csv"   || "No study event with matching OID found in the study on the EDC"
    }

    private stubSubmitOk(String filePath) {
        wireMockRule.stubFor(post(urlEqualTo(getSubmitUri().getPath()))
                .withHeader("Authorization", equalTo("Bearer " + AUTHENTICATION_TOKEN.value))
                .withMultipartRequestBody(
                        aMultipart()
                                .withHeader("Content-Disposition", equalTo("form-data; name=\"file\"; filename=\"odm.xml\""))
                                .withBody(new CustomStringValuePattern(readFile(filePath)))
                )
                .willReturn(aResponse().withStatus(200).withBody(EDC_RESPONSE)))
    }

    private stubSubmitBadRequestErrorCode(String errorCode) {
        stubFor(post(urlEqualTo(getSubmitUri().getPath()))
                .willReturn(aResponse().withStatus(400).withBody(errorCode)))
    }

    private stubSubmitOkErrorCode(String errorCode) {
        stubFor(post(urlEqualTo(getSubmitUri().getPath()))
                .willReturn(aResponse().withStatus(200).withBody(errorCode)))
    }

    private stubJobReportOk(String filePath) {
        stubFor(get(urlEqualTo(getJobReportUri().getPath()))
                .withHeader("Authorization", equalTo("Bearer " + AUTHENTICATION_TOKEN.value))
                .willReturn(aResponse().withStatus(200).withBody(readFile(filePath))))
    }

    private stubJobReportOkOnSecondCall() {
        def scenario = "Job is completed on second call"
        def stateCalledOnce = "Called once"
        stubFor(get(urlEqualTo(getJobReportUri().getPath())).inScenario(scenario)
                .whenScenarioStateIs(Scenario.STARTED)
                .withHeader("Authorization", equalTo("Bearer " + AUTHENTICATION_TOKEN.value))
                .willReturn(aResponse().withStatus(200).withBody("errorCode.jobInProgress"))
                .willSetStateTo(stateCalledOnce))
        stubFor(get(urlEqualTo(getJobReportUri().getPath())).inScenario(scenario)
                .whenScenarioStateIs(stateCalledOnce)
                .withHeader("Authorization", equalTo("Bearer " + AUTHENTICATION_TOKEN.value))
                .willReturn(aResponse().withStatus(200).withBody(readFile("openclinica/submitevent/job-report-success.csv"))))
    }

    private SubmittedEvent aSubmittedEventWithThreeItems(StudyId studyId, SubjectId subjectId) {
        def item1 = aSubmittedItem(1)
        def item2 = aSubmittedItem(2)
        def item3 = aSubmittedItem(3)
        def itemGroup = aDefaultSubmittedItemGroupBuilder().withSubmittedItems([item1, item2, item3]).build()
        def form = aDefaultSubmittedFormBuilder().withSubmittedItemGroups([itemGroup]).build()
        return aDefaultSubmittedEventBuilder()
                .withStudyId(studyId)
                .withSubjectId(subjectId)
                .withEventParentId(null)
                .withSubmittedForms([form])
                .withReferenceDate(REFERENCE_DATE)
                .withSubmittedDate(SUBMITTED_DATE)
                .build()
    }

    private SubmittedItem aSubmittedItem(int index) {
        SubmittedItemObjectMother.aDefaultSubmittedItemBuilder()
                .withId(ItemDefinitionId.of("itemDefinitionId-${index}"))
                .withInstanceId(SubmittedItemId.of("submittedItemId-${index}"))
                .withPopulatedItemId(ItemId.of("populatedItemId-${index}"))
                .build()
    }

    private URI getSubmitUri() {
        return SUBMIT_URI_TEMPLATE.expand(wireMockRule.port())
    }

    private URI getJobReportUri() {
        return JOB_REPORT_URI_TEMPLATE.expand(wireMockRule.port(), JOB_UUID)
    }

    private static final class CustomStringValuePattern extends StringValuePattern {
        CustomStringValuePattern(String expectedValue) {
            super(expectedValue)
        }

        @Override
        MatchResult match(String value) {
            def pattern = new EqualToPattern(expectedValue.replaceAll("\\s", ""))
            def valueWithoutWhitespace = value.replaceAll("\\s", "")
            return pattern.match(valueWithoutWhitespace)
        }
    }
}
