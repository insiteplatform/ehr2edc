package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.*
import com.custodix.insite.local.ehr2edc.audit.PopulationContextRepository
import com.custodix.insite.local.ehr2edc.config.EHR2EDCAppConfig
import com.custodix.insite.local.ehr2edc.infra.edc.api.SpecificEDCStudyGateway
import com.custodix.insite.local.ehr2edc.infra.time.TestTimeService
import com.custodix.insite.local.ehr2edc.mongo.app.document.EDCConnectionDocument
import com.custodix.insite.local.ehr2edc.mongo.app.event.PopulatedEventMongoRepository
import com.custodix.insite.local.ehr2edc.mongo.app.study.StudyConnectionMongoRepository
import com.custodix.insite.local.ehr2edc.mongo.app.submitted.SubmittedEventMongoRepository
import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent
import com.custodix.insite.local.ehr2edc.populator.PopulatedEventObjectMother
import com.custodix.insite.local.ehr2edc.populator.PopulatedEventRepository
import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2Configuration
import com.custodix.insite.local.ehr2edc.query.mongo.configuration.EHR2EDCMongoDBQueryConfiguration
import com.custodix.insite.local.ehr2edc.query.mongo.demographic.repository.DemographicRepository
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.repository.LabValueRepository
import com.custodix.insite.local.ehr2edc.query.mongo.medication.repository.MedicationRepository
import com.custodix.insite.local.ehr2edc.query.mongo.vitalsign.repository.VitalSignRepository
import com.custodix.insite.local.ehr2edc.snapshots.*
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEventRepository
import com.custodix.insite.local.ehr2edc.vocabulary.*
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.health.HealthAggregator
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

import java.time.LocalDate

import static com.custodix.insite.local.ehr2edc.snapshots.InvestigatorSnapshotObjectMother.generateInvestigatorFromUserIdentifier
import static com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReferenceObjectMother.aRandomEdcSubjectReference
import static com.custodix.insite.local.ehr2edc.vocabulary.EHRRegistrationStatus.REGISTERED
import static com.custodix.insite.local.ehr2edc.vocabulary.ExternalSiteIdObjectMother.aRandomExternalSiteId
import static com.custodix.insite.local.ehr2edc.vocabulary.PatientIdObjectMother.aRandomPatientId
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId
import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId
import static com.google.common.collect.Sets.newHashSet
import static java.util.Arrays.asList
import static java.util.stream.Collectors.toList

@SpringBootTest(
        classes = [
                SpecConfiguration.class,
                EHR2EDCAppConfig.class,
                EHR2EDCMongoDBQueryConfiguration.class,
                FhirDstu2Configuration.class,
                EmbeddedMongoAutoConfiguration.class]
        , properties = ["spring.main.allow-bean-definition-overriding=true"])
@TestPropertySource(locations = "classpath:test-main.properties")
abstract class AbstractSpecification extends Specification {

    @Autowired
    protected StudyConnectionMongoRepository.StudyConnectionMongoSnapshotRepository studyConnectionMongoSnapshotRepository
    @Autowired
    protected TestStudyRepository testStudyRepository
    @Autowired
    protected StudyRepository studyRepository
    @Autowired
    protected StudyConnectionRepository studyConnectionRepository
    @Autowired
    protected EHRConnectionRepository ehrConnectionRepository
    @Autowired
    protected PopulatedEventRepository eventRepository
    @Autowired
    protected PopulationContextRepository populationContextRepository
    @Autowired
    protected TestEventPublisher testEventPublisher
    @Autowired
    protected PopulatedEventMongoRepository.EventMongoSnapshotRepository eventMongoSnapshotRepository
    @Autowired
    protected SpecificEDCStudyGateway edcStudyGateway
    @Autowired
    protected TestRegistrationRecordRepository registrationRecordRepository
    @Autowired
    protected InMemoryGetCurrentUserController currentUserController
    @Autowired
    protected SubmittedEventRepository submittedEventRepository
    @Autowired
    protected SubmittedEventMongoRepository.AuditedReviewSnapshotRepository reviewContextMongoRepository
    @Autowired
    protected DemographicRepository demographicRepository
    @Autowired
    protected LabValueRepository labValueRepository
    @Autowired
    protected VitalSignRepository vitalSignRepository
    @Autowired
    protected MedicationRepository medicationRepository
    @Autowired
    protected TestGetUsersController userRepository

    @MockBean
    private HealthAggregator healthAggregator
    @MockBean
    private ExportPatient exportPatient

    protected TestTimeService testTimeService

    protected static final UserIdentifier USER_ID_KNOWN = UserIdentifier.of("42")
    protected static final UserIdentifier USER_ID_OTHER = UserIdentifier.of("43")

    protected static final LocalDate DATE_NOW = LocalDate.now()
    protected static final LocalDate DATE_YESTERDAY = DATE_NOW.minusDays(1)
    protected static final LocalDate DATE_TOMORROW = DATE_NOW.plusDays(1)
    protected static final LocalDate DATE_1850 = LocalDate.of(1850, 07, 1)

    def setup() {
        withCurrentUserHavingId(USER_ID_KNOWN)
        testEventPublisher.clear()
        testStudyRepository.deleteAll()
        eventMongoSnapshotRepository.deleteAll()
        DomainEventPublisher.setPublisher(testEventPublisher)
        testTimeService = new TestTimeService()
        DomainTime.setTime(testTimeService)
        reviewContextMongoRepository.deleteAll()
        demographicRepository.deleteAll()
        labValueRepository.deleteAll()
        vitalSignRepository.deleteAll()
        medicationRepository.deleteAll()
        userRepository.clear()
    }

    def generateKnownPatientId() {
        return aRandomPatientId()
    }

    def generateUUID() {
        return UUID.randomUUID().toString()
    }

    def generateKnownStudyId(UserIdentifier investigator) {
        StudyId studyId = aRandomStudyId()
        def study = StudySnapshot.newBuilder()
                .withStudyId(studyId)
                .withInvestigators(generateInvestigatorFromUserIdentifier(investigator))
                .build()
        saveInStudyMongoSnapshotRepository(study)
        Arrays.stream(StudyConnectionType.values())
                .forEach { e -> saveInStudyConnectionSnapshotRepository(StudyConnectionObjectMother.aDefaultStudyConnectionOfType(studyId, e)) }
        return studyId
    }

    def generateKnownStudy(UserIdentifier investigator) {
        return generateKnownStudy(aRandomStudyId(), "Study Name", "Study Description", investigator)
    }

    def generateKnownStudy(StudyId studyId, String name, String description, UserIdentifier investigator) {
        StudySnapshot study = StudySnapshotObjectMother.generateStudy(studyId, name, description, investigator)
        saveInStudyMongoSnapshotRepository(study)
        Arrays.stream(StudyConnectionType.values())
                .forEach({ e -> saveInStudyConnectionSnapshotRepository(StudyConnectionObjectMother.aDefaultStudyConnectionOfType(studyId, e)) })
        return study
    }

    def generateKnownStudyWithoutEDCConnection(UserIdentifier investigator) {
        return generateKnownStudyWithoutEDCConnection(aRandomStudyId(), "Study name", "Study description", investigator)
    }

    def generateKnownStudyWithoutEDCConnection(StudyId studyId, String name, String description, UserIdentifier investigator) {
        StudySnapshot study = StudySnapshotObjectMother.generateStudy(studyId, name, description, investigator)
        saveInStudyMongoSnapshotRepository(study)
        return study
    }

    def generateKnownStudyWithoutEDCConnection(StudyId studyId, String name, String description, SubjectSnapshot subject) {
        return generateKnownStudy(studyId, name, description, asList(subject), UserIdentifier.of("42"))
    }

    def generateKnownStudyWithoutEDCConnection(StudyId studyId, String name, String description, List<SubjectSnapshot> subjects, UserIdentifier investigator) {
        StudySnapshot study = StudySnapshot.Builder.newInstance()
                .withStudyId(studyId)
                .withName(name)
                .withDescription(description)
                .withSubjects(subjects)
                .withInvestigators(newHashSet(generateInvestigatorFromUserIdentifier(investigator)))
                .build()
        saveInStudyMongoSnapshotRepository(study)
        return study
    }

    def generateKnownStudy(StudyId studyId, String name, String description, SubjectSnapshot subject, UserIdentifier investigator) {
        return generateKnownStudy(studyId, name, description, asList(subject), investigator)
    }

    def generateKnownStudy(StudyId studyId, String name, String description, List<SubjectSnapshot> subjects, UserIdentifier investigatorId) {
        StudySnapshot study = StudySnapshot.Builder.newInstance()
                .withStudyId(studyId)
                .withName(name)
                .withDescription(description)
                .withSubjects(subjects)
                .withInvestigators(newHashSet(generateInvestigatorFromUserIdentifier(investigatorId)))
                .build()
        saveInStudyMongoSnapshotRepository(study)
        return study
    }

    def generateKnownStudyWithNoFormDefinitions() {
        StudySnapshot study = StudySnapshotObjectMother.generateStudyWithoutFormDefinitions(aRandomStudyId(), UserIdentifier.of("42"))
        saveInStudyMongoSnapshotRepository(study)
        return study
    }

    def populateEvents(StudySnapshot study) {
        def events = PopulatedEventObjectMother.createForAllSubjectsAndForms(study)
        saveEvents(events)
        return events
    }

    def populateEventsWithoutPopulator(StudySnapshot study) {
        def events = PopulatedEventObjectMother.createForAllSubjectsAndFormsNoPopulator(study)
        saveEvents(events)
        return events
    }

    void saveForm(StudySnapshot study, EventDefinitionSnapshot eventSnapshot, SubjectSnapshot subject) {
        PopulatedEvent event = PopulatedEventObjectMother.generateEvent(study.studyId, eventSnapshot.id, subject.subjectId)
        eventRepository.save(event)
    }

    void saveEvents(List<PopulatedEvent> events) {
        events.each { eventRepository.save(it) }
    }

    def generateKnownStudyWithoutInvestigators(StudyId studyId, String name, String description) {
        generateKnownStudyWithoutInvestigators(studyId, name, description, Collections.emptyList())
    }

    def generateKnownStudyWithoutInvestigators(StudyId studyId, String name, String description, List<SubjectSnapshot> subjects) {
        StudySnapshot study = StudySnapshot.newBuilder()
                .withStudyId(studyId)
                .withName(name)
                .withDescription(description)
                .withSubjects(subjects)
                .build()

        saveInStudyMongoSnapshotRepository(study)
        return study
    }

    def generateKnownStudyWithoutInvestigators() {
        return generateKnownStudyWithoutInvestigators(aRandomStudyId(), "Study name", "Study description")
    }

    def addReadSubjectConnection(StudySnapshot study) {
        def connection = StudyConnectionObjectMother.aDefaultStudyConnectionBuilder(study.studyId, StudyConnectionType.READ_SUBJECTS).build()
        return studyConnectionMongoSnapshotRepository.save(EDCConnectionDocument.from(connection))
    }

    def addSubmitEventEDCConnection(StudySnapshot study) {
        def connection = StudyConnectionObjectMother.aDefaultStudyConnectionBuilder(study.studyId, StudyConnectionType.SUBMIT_EVENT).build()
        return studyConnectionMongoSnapshotRepository.save(EDCConnectionDocument.from(connection))
    }

    def addSubmitEventEDCConnection(StudySnapshot study, int port) {
        def connection = StudyConnectionObjectMother.aDefaultStudyConnectionBuilderWithPort(study.studyId, StudyConnectionType.SUBMIT_EVENT, port + "").build()
        return studyConnectionMongoSnapshotRepository.save(EDCConnectionDocument.from(connection))
    }

    def addSubmitEventEDCConnection(StudySnapshot study, boolean enabled) {
        def connection = StudyConnectionObjectMother.aDefaultStudyConnectionBuilder(study.studyId, StudyConnectionType.SUBMIT_EVENT)
                .withEnabled(enabled)
                .build()
        return studyConnectionMongoSnapshotRepository.save(EDCConnectionDocument.from(connection))
    }

    def addWriteSubjectEDCConnection(StudySnapshot study) {
        def connection = StudyConnectionObjectMother.aDefaultStudyConnectionBuilder(study.studyId, StudyConnectionType.WRITE_SUBJECT).build()
        return studyConnectionMongoSnapshotRepository.save(EDCConnectionDocument.from(connection))
    }

    def addReadLabAnalyteRangesConnection(StudySnapshot study) {
        def connection = StudyConnectionObjectMother.aDefaultStudyConnectionBuilder(study.studyId, StudyConnectionType.READ_LABNAMES).build()
        return studyConnectionMongoSnapshotRepository.save(EDCConnectionDocument.from(connection))
    }

    def generateEDCConnectionData(StudyId studyId, StudyConnectionType type) {
        return generateEDCConnectionData(studyId, type, true)
    }

    def generateEDCConnectionData(StudyId studyId, StudyConnectionType type, boolean enabled) {
        return ExternalEDCConnection.newBuilder()
                .withStudyId(studyId)
                .withConnectionType(type)
                .withExternalSiteId(aRandomExternalSiteId())
                .withClinicalDataURI(new URI("/studies/SID_001/Subjects"))
                .withUsername("usr")
                .withPassword("pwd")
                .withEnabled(enabled)
                .build()
    }

    def addRegisteredSubjectToStudy(StudySnapshot study, PatientCDWReference patientId, EDCSubjectReference edcSubjectReference, SubjectId subjectId) {
        def subjects = study.subjects
        subjects.add(SubjectSnapshot.newBuilder()
                .withPatientCDWReference(patientId)
                .withEdcSubjectReference(edcSubjectReference)
                .withSubjectId(subjectId)
                .withDateOfConsent(DATE_NOW)
                .build())

        saveInStudyMongoSnapshotRepository(StudySnapshot.newBuilder().copy(study)
                .withSubjects(subjects)
                .build())
    }

    def addDeregisteredSubjectToStudy(StudyId studyId, PatientCDWReference patientId, EDCSubjectReference edcSubjectReference, SubjectId subjectId) {
        Study study = studyRepository.getStudyById(studyId)
        def subject = SubjectSnapshot.newBuilder()
                .withPatientCDWReference(patientId)
                .withEdcSubjectReference(edcSubjectReference)
                .withSubjectId(subjectId)
                .withDateOfConsentWithdrawn(LocalDate.now())
                .withDeregisterReason(DeregisterReason.CONSENT_RETRACTED)
                .withEHRRegistrationStatus(REGISTERED)
                .build()

        saveInStudyMongoSnapshotRepository(StudySnapshot.newBuilder().copy(study.toSnapShot())
                .withSubjects(asList(subject))
                .build())
    }

    def addDeregisteredSubjectToStudy(StudySnapshot study, PatientCDWReference patientId, EDCSubjectReference edcSubjectReference, SubjectId subjectId) {
        def subject = SubjectSnapshot.newBuilder()
                .withPatientCDWReference(patientId)
                .withEdcSubjectReference(edcSubjectReference)
                .withSubjectId(subjectId)
                .withDateOfConsentWithdrawn(DATE_NOW)
                .withDeregisterReason(DeregisterReason.CONSENT_RETRACTED)
                .build()

        saveInStudyMongoSnapshotRepository(StudySnapshot.newBuilder().copy(study)
                .withSubjects(asList(subject))
                .build())
    }

    def createSubjectSnapshots(Collection<EDCSubjectReference> subjects) {
        def result = new ArrayList()
        for (EDCSubjectReference subjectReference : subjects) {
            result.add(subjectSnapshotForRef(subjectReference))
        }

        return result
    }

    private SubjectSnapshot subjectSnapshotForRef(EDCSubjectReference ref) {
        return SubjectSnapshot
                .newBuilder()
                .withSubjectId(SubjectId.generate())
                .withEdcSubjectReference(ref)
                .withPatientCDWReference(generateKnownPatientId())
                .withDateOfConsent(DATE_NOW)
                .build()
    }

    def generateKnownSubject(StudyId studyId) {
        return generateKnownSubject(studyId, generateKnownPatientId(), aRandomSubjectId(), DATE_NOW, null, null)
    }

    def generateKnownStudyWithMetaData(StudyId studyId, SubjectId subjectId, MetaDataDefinitionSnapshot metaDataDefinitionSnapshot, UserIdentifier investigator) {
        def subject = SubjectSnapshot.newBuilder()
                .withPatientCDWReference(generateKnownPatientId())
                .withSubjectId(subjectId)
                .withDateOfConsent(DATE_NOW)
                .withEHRRegistrationStatus(REGISTERED)
                .build()
        def mappings = [:]
        mappings[ItemDefinitionId.of("ITEM-ID")] = ItemQueryMappingSnapshotObjectMother.aDefaultItemQueryMappingSnapshot()
        def studySnapshotBuilder = StudySnapshot.newBuilder()
                .withStudyId(studyId)
                .withInvestigators(newHashSet(generateInvestigatorFromUserIdentifier(investigator)))
                .withSubjects(Collections.singletonList(subject))
                .withMetadata(metaDataDefinitionSnapshot)
                .withItemQueryMappings(mappings)
        def studySnapshot = studySnapshotBuilder
                .build()
        saveInStudyMongoSnapshotRepository(studySnapshot)
        return studySnapshot

    }

    def generateKnownSubject(StudyId studyId, SubjectId subjectId, LocalDate dateOfConsent, UserIdentifier investigator) {
        def subject = SubjectSnapshot.newBuilder()
                .withPatientCDWReference(generateKnownPatientId())
                .withSubjectId(subjectId)
                .withDateOfConsent(dateOfConsent)
                .build()
        def studySnapshot = StudySnapshot.newBuilder()
                .withStudyId(studyId)
                .withInvestigators(newHashSet(generateInvestigatorFromUserIdentifier(investigator)))
                .withSubjects(Collections.singletonList(subject))
                .build()
        saveInStudyMongoSnapshotRepository(studySnapshot)
        return subject

    }

    def generateKnownSubject(StudyId studyId, PatientCDWReference patientId, SubjectId subjectId, LocalDate dateOfConsent,
                             LocalDate dateOfConsentWithdrawn, DeregisterReason deregisterReason) {
        SubjectSnapshot subject =
                SubjectSnapshot.newBuilder()
                        .withPatientCDWReference(patientId)
                        .withSubjectId(subjectId)
                        .withDateOfConsent(dateOfConsent)
                        .withDateOfConsentWithdrawn(dateOfConsentWithdrawn)
                        .withDeregisterReason(deregisterReason)
                        .withEdcSubjectReference(aRandomEdcSubjectReference())
                        .build()

        def studySnapshot = StudySnapshot.newBuilder().copy(studyRepository.getStudyById(studyId).toSnapShot())
                .withSubjects(asList(subject))
                .build()
        saveInStudyMongoSnapshotRepository(studySnapshot)
        return subject
    }

    private saveInStudyMongoSnapshotRepository(StudySnapshot study) {
        studyRepository.save(Study.restoreSnapshot(study))
    }

    def saveInStudyConnectionSnapshotRepository(ExternalEDCConnection connection) {
        EDCConnectionDocument studyConnectionSnapshot = EDCConnectionDocument.from(connection)
        studyConnectionMongoSnapshotRepository.save(studyConnectionSnapshot)
    }

    def withCurrentUserHavingId(UserIdentifier userId) {
        currentUserController.withUserId(userId)
    }

    def withCurrentUserNoDRM() {
        currentUserController.withDRM(false)
    }

    def withCurrentUserDRM() {
        currentUserController.withDRM(true)
    }

    def withoutAuthenticatedUser() {
        currentUserController.withoutUser()
    }

    Study getLastSavedStudy() {
        return testStudyRepository.getLastSavedStudy()
    }

    protected addInvestigators(Collection<GetStudySpec.Investigator> investigators, StudySnapshot study) {
        def investigatorsSnapshots = investigators.stream().map({ i -> InvestigatorSnapshot.newBuilder().withUserId(i.getInvestigatorId()).withName(i.getName()).build() }).collect(toList())
        saveInStudyMongoSnapshotRepository(StudySnapshot.newBuilder().copy(study).withInvestigators(investigatorsSnapshots).build())
    }
}
