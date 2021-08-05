package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.*
import com.custodix.insite.local.ehr2edc.command.RegisterSubject
import com.custodix.insite.local.ehr2edc.domain.service.PatientEHRGateway
import com.custodix.insite.local.ehr2edc.events.SubjectCreated
import com.custodix.insite.local.ehr2edc.events.SubjectRegisteredEvent
import com.custodix.insite.local.ehr2edc.shared.exceptions.UseCaseConstraintViolationException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import com.custodix.insite.local.ehr2edc.snapshots.StudySnapshot
import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshot
import com.custodix.insite.local.ehr2edc.vocabulary.*
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.access.AccessDeniedException
import spock.lang.Ignore
import spock.lang.Title
import spock.lang.Unroll

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import static com.custodix.insite.local.ehr2edc.command.RegisterSubject.Request
import static com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReferenceObjectMother.aRandomEdcSubjectReference
import static com.custodix.insite.local.ehr2edc.vocabulary.PatientIdObjectMother.aRandomPatientId
import static com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother.aRandomStudyId
import static org.mockito.BDDMockito.given

@Title("Subject Registration")
class RegisterSubjectSpec extends AbstractSpecification {

    private static final LocalDate DATE_2009_09_09 = LocalDate.of(2009, 9, 9)
    private static final String LAST_NAME = "Smith"
    private static final String FIRST_NAME = "John"
    @Autowired
    RegisterSubject registerSubject

    @SpringBean
    private EDCStudyGateway edcStudyGateway = Stub()

    @MockBean
    private PatientEHRGateway patientEHRGateway

    EventPublisher eventPublisher

    def setup() {
        eventPublisher = Mock()
        DomainEventPublisher.setPublisher(eventPublisher)
    }

    def teardown() {
        0 * _
    }

    def "Registering a Subject for a Study with an empty request"() {
        given: "An empty Subject Registration Request"
        Request request = Request.newBuilder().build()

        when: "Registering the Subject for the Study"
        registerSubject.register(request)

        then: "Access is denied"
        AccessDeniedException ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"
        and: "No event was published"
        0 * eventPublisher.publishEvent(*_)
    }

    def "Registering a Subject for a Study without a SubjectId"() {
        given: "A Subject Registration Request without a SubjectId"
        Request request = Request.newBuilder()
                .withPatientId(generateKnownPatientId())
                .withStudyId(generateKnownStudyId(USER_ID_KNOWN))
                .withDateOfConsent(DATE_NOW)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()

        when: "Registering the Subject for the Study"
        registerSubject.register(request)

        then: "Indicate the request was invalid"
        thrown UseCaseConstraintViolationException
        and: "No event was published"
        0 * eventPublisher.publishEvent(*_)
    }

    def "Registering a Subject for a Study without a PatientId"() {
        given: "A Subject Registration Request without a PatientId"
        Request request = Request.newBuilder()
                .withStudyId(generateKnownStudyId(USER_ID_KNOWN))
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .withDateOfConsent(DATE_NOW)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()

        when: "Registering the Subject for the Study"
        registerSubject.register(request)

        then: "Indicate the request was invalid"
        thrown UseCaseConstraintViolationException
        and: "No event was published"
        0 * eventPublisher.publishEvent(*_)
    }

    def "Registering a Subject for a Study without a Date of Consent"() {
        given: "A Subject Registration Request without a Date of Consent"
        Request request = Request.newBuilder()
                .withPatientId(generateKnownPatientId())
                .withStudyId(generateKnownStudyId(USER_ID_KNOWN))
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()

        when: "Registering the Subject for the Study"
        registerSubject.register(request)

        then: "Indicate the request was invalid"
        thrown UseCaseConstraintViolationException
        and: "No event was published"
        0 * eventPublisher.publishEvent(*_)
    }

    @Ignore("Until E2E-630 is implemented")
    def "Registering a Subject for a Study without a date of birth"() {
        given: "A Subject Registration Request without a Date of birth"
        Request request = Request.newBuilder()
                .withPatientId(generateKnownPatientId())
                .withStudyId(generateKnownStudyId(USER_ID_KNOWN))
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .withDateOfConsent(DATE_NOW)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()

        when: "Registering the Subject for the Study"
        registerSubject.register(request)

        then: "Indicate the request was invalid"
        def exception = thrown UseCaseConstraintViolationException
        exception.message == '[birthDate: must not be null]'
        and: "No event was published"
        0 * eventPublisher.publishEvent(*_)
    }

    @Ignore("Until E2E-630 is implemented")
    @Unroll
    def "Registering a Subject for a Study without a first name"(String firstName, String expectedErrorMessage) {
        given: "A Subject Registration Request without a first name"
        Request request = Request.newBuilder()
                .withPatientId(generateKnownPatientId())
                .withStudyId(generateKnownStudyId(USER_ID_KNOWN))
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .withDateOfConsent(DATE_NOW)
                .withLastName(LAST_NAME)
                .withFirstName(firstName)
                .withBirthDate(DATE_2009_09_09)
                .build()

        when: "Registering the Subject for the Study"
        registerSubject.register(request)

        then: "Indicate the request was invalid"
        def exception = thrown UseCaseConstraintViolationException
        exception.message == expectedErrorMessage
        and: "No event was published"
        0 * eventPublisher.publishEvent(*_)

        where:
        firstName   | expectedErrorMessage
        null        | "[firstName: must not be blank]"
        ""          | "[firstName: must not be blank]"
        "     "     | "[firstName: must not be blank]"
    }

    @Ignore("Until E2E-630 is implemented")
    @Unroll
    def "Registering a Subject for a Study without a last name"(String lastName, String expectedErrorMessage) {
        given: "A Subject Registration Request without a last name"
        Request request = Request.newBuilder()
                .withPatientId(generateKnownPatientId())
                .withStudyId(generateKnownStudyId(USER_ID_KNOWN))
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .withDateOfConsent(DATE_NOW)
                .withLastName(lastName)
                .withFirstName(FIRST_NAME)
                .withBirthDate(DATE_2009_09_09)
                .build()

        when: "Registering the Subject for the Study"
        registerSubject.register(request)

        then: "Indicate the request was invalid"
        def exception = thrown UseCaseConstraintViolationException
        exception.message == expectedErrorMessage
        and: "No event was published"
        0 * eventPublisher.publishEvent(*_)

        where:
        lastName   | expectedErrorMessage
        null        | "[lastName: must not be blank]"
        ""          | "[lastName: must not be blank]"
        "     "     | "[lastName: must not be blank]"
    }

    def "Registering a Subject for an unknown Study"() {
        given: "A Subject Registration Request with an unknown StudyId"
        StudyId studyId = aRandomStudyId()
        Request request = Request.newBuilder()
                .withPatientId(generateKnownPatientId())
                .withStudyId(studyId)
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .withDateOfConsent(DATE_NOW)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()

        when: "Registering the Subject for the Study"
        registerSubject.register(request)

        then: "Indicate the user has no permission to perform the request"
        def ex = thrown(AccessDeniedException)
        ex.message == "User is not an assigned Investigator"
        and: "No event was published"
        0 * eventPublisher.publishEvent(*_)
    }

    def "Registering a Subject for a known Study with an unauthenticated user should fail"() {
        given: "A Subject Registration Request with a known StudyId and unknown Investigator"
        withoutAuthenticatedUser()
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        Request request = Request.newBuilder()
                .withPatientId(generateKnownPatientId())
                .withStudyId(studyId)
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .withDateOfConsent(DATE_NOW)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()

        when: "Registering the Subject for the Study"
        registerSubject.register(request)

        then: "Indicate the user is not a assigned investigator"
        thrown(AccessDeniedException)
        and: "No event was published"
        0 * eventPublisher.publishEvent(*_)
    }

    def "Registering a Subject for a known Study with an authenticated user who is not an investigator should fail"() {
        given: "A Subject Registration Request with a known StudyId and unknown Investigator"
        StudyId studyId = generateKnownStudyId(USER_ID_OTHER)
        Request request = Request.newBuilder()
                .withPatientId(generateKnownPatientId())
                .withStudyId(studyId)
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .withDateOfConsent(DATE_NOW)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()

        when: "Registering the Subject for the Study"
        registerSubject.register(request)

        then: "Indicate the user is not a assigned investigator"
        thrown(AccessDeniedException)
        and: "No event was published"
        0 * eventPublisher.publishEvent(*_)
    }

    def "Registering a Subject for a known Study and a known Investigator"() {
        given: "A Subject Registration Request with a known StudyId and known Investigator"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        PatientCDWReference givenPatientId = generateKnownPatientId()
        EDCSubjectReference edcReference = aRandomEdcSubjectReference()
        Request request = Request.newBuilder()
                .withPatientId(givenPatientId)
                .withStudyId(studyId)
                .withEdcSubjectReference(edcReference)
                .withDateOfConsent(DATE_NOW)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .withBirthDate((DATE_2009_09_09))
                .build()
        given(patientEHRGateway.exists(studyId, aPatientSearchCriteria(givenPatientId, LAST_NAME, FIRST_NAME, DATE_2009_09_09))).willReturn(true)

        when: "Registering the Subject for the Study"
        registerSubject.register(request)

        then: "The Subject was added to the Study"
        Subject expected = Subject.initialRegistration(new Study.SubjectRegistrationData(givenPatientId, studyId, edcReference, DATE_NOW, LAST_NAME, FIRST_NAME, DATE_2009_09_09))
        def returned = studyRepository.getStudyById(studyId).subjects.items.first()
        expected.deregisterReason == null
        expected.dateOfConsent.isEqual(returned.dateOfConsent)
        expected.dateOfConsentWithdrawn == null
        expected.patientCDWReference.id == returned.patientCDWReference.id
        expected.patientCDWReference.source == returned.patientCDWReference.source
        expected.edcSubjectReference.id == returned.edcSubjectReference.id

        then: "A subject was added to the Study"
        with(studyRepository.getStudyById(studyId).subjects.items.first()) {
            dateOfConsent.isEqual(DATE_NOW)
            patientCDWReference == givenPatientId
            edcSubjectReference == edcReference

            subjectId.id
            !deregisterReason
            !dateOfConsentWithdrawn
        }
        and: "A matching 'subject registered' event was published"
        2 * eventPublisher.publishEvent({ isMatchingSubjectRegisteredEvent(it, givenPatientId) })
        and: "A matching 'subject added to study' event was published"
        1 * eventPublisher.publishEvent({ isMatchingSubjectAddedEvent(it, studyId, edcReference) })
    }

    static isMatchingSubjectRegisteredEvent(event, patientId) {
        SubjectRegisteredEvent.isCase(event) &&
                event.patientId == patientId.id &&
                event.namespace == patientId.source &&
                event.namespace == patientId.source &&
                event.subjectId != null
    }

    static isMatchingSubjectAddedEvent(event, studyId, edcReference) {
        SubjectCreated.isCase(event) &&
                event.studyId == studyId &&
                event.edcSubjectReference == edcReference
    }

    def "Adjusting the registration date for a previously registered patient"() {
        given: "A known PatientId, known StudyId and a SubjectId"
        PatientCDWReference patientId = generateKnownPatientId()
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        EDCSubjectReference edcSubjectReference = aRandomEdcSubjectReference()

        Request registerRequest = Request.newBuilder()
                .withPatientId(patientId)
                .withStudyId(studyId)
                .withEdcSubjectReference(edcSubjectReference)
                .withDateOfConsent(DATE_YESTERDAY)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()
        Request updateRequest = Request.newBuilder()
                .withPatientId(patientId)
                .withStudyId(studyId)
                .withEdcSubjectReference(edcSubjectReference)
                .withDateOfConsent(DATE_NOW)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()

        given(patientEHRGateway.exists(studyId, aPatientSearchCriteria(patientId, LAST_NAME, FIRST_NAME, DATE_2009_09_09))).willReturn(true)

        when: "I update the date of consent"
        registerSubject.register(registerRequest)
        registerSubject.register(updateRequest)

        then: "The updated date of consent should be returned"
        Subject expectedAfterUpdate = Subject.initialRegistration(new Study.SubjectRegistrationData(patientId, studyId, edcSubjectReference, DATE_NOW, LAST_NAME, FIRST_NAME, DATE_2009_09_09))
        def returned2 = studyRepository.getStudyById(studyId).subjects.items.stream()
                .filter({s -> s.getEdcSubjectReference().equals(edcSubjectReference)})
                .findFirst().get()
        expectedAfterUpdate.deregisterReason == null
        expectedAfterUpdate.dateOfConsent.isEqual(returned2.dateOfConsent)
        expectedAfterUpdate.dateOfConsentWithdrawn == null
        expectedAfterUpdate.patientCDWReference.id == returned2.patientCDWReference.id
        expectedAfterUpdate.patientCDWReference.source == returned2.patientCDWReference.source
    }

    def "A Patient can only be a Subject once in the same Study"() {
        given: "An existing Subject"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        SubjectSnapshot subject = generateKnownSubject(studyId)
        and: "A request with the same patientId but different subjectId"
        Request request = Request.newBuilder()
                .withPatientId(subject.patientCDWReference)
                .withStudyId(studyId)
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .withDateOfConsent(DATE_NOW)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()

        when: "I register the patient"
        registerSubject.register(request)

        then: "The same patient can't be a subject twice in the same study"
        UserException ex = thrown()
        ex.message == "Can't register subject with cdw reference '" + subject.patientCDWReference.id + "' which is already in use."
        and: "No events are published"
        0 * eventPublisher.publishEvent(*_)
    }

    def "An EDCSubjectReference can never be associated with a different PatientCDWReference after deregistering"() {
        given: "An EDCSubjectReference"
        def edcReference = aRandomEdcSubjectReference()
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        and: "A request for a patient with that EDC reference"
        PatientCDWReference patientId1 = generateKnownPatientId()
        Request requestForPatient1 = Request.newBuilder()
                .withPatientId(patientId1)
                .withStudyId(studyId)
                .withEdcSubjectReference(edcReference)
                .withDateOfConsent(DATE_NOW)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()
        given(patientEHRGateway.exists(studyId, aPatientSearchCriteria(patientId1, LAST_NAME, FIRST_NAME, DATE_2009_09_09))).willReturn(true)
        and: "Another request for a different patient, with the same EDC reference"
        PatientCDWReference patientId2 = generateKnownPatientId()
        Request requestForPatient2 = Request.newBuilder(requestForPatient1)
                .withPatientId(patientId2)
                .build()
        given(patientEHRGateway.exists(studyId, aPatientSearchCriteria(patientId2, LAST_NAME, FIRST_NAME, DATE_2009_09_09))).willReturn(true)

        when: "I register both patients with the duplicate EDC reference"
        registerSubject.register(requestForPatient1)
        registerSubject.register(requestForPatient2)

        then: "The first patient is added to the Study"
        with(studyRepository.getStudyById(studyId).subjects.items.first()) {
            edcSubjectReference == edcReference
            patientCDWReference == patientId1
            dateOfConsent.isEqual(DATE_NOW)
            !dateOfConsentWithdrawn
        }
        and: "The second patient is not registered because EDCSubjectReference can't be assigned twice"
        UserException ex = thrown()
        ex.message == "Can't reregister this edcSubjectReference (" + edcReference.id + ") with a different patientCDWReference"
        and: "One event was published, registering the first patient"
        1 * eventPublisher.publishEvent({
            SubjectRegisteredEvent.isCase(it) &&
                    it.patientId == patientId1.id &&
                    it.namespace == patientId1.source &&
                    it.subjectId == it.subjectId
        })
        and: "A 'subject added to study' event was published"
        1 * eventPublisher.publishEvent(_ as SubjectCreated)
    }

    def "A Patient can be a Subject in multiple Studies"()  {
        given: "A patientId"
        PatientCDWReference givenPatientId = generateKnownPatientId()
        and: "A request for that patient"
        StudyId studyId1 = generateKnownStudyId(USER_ID_KNOWN)
        def reference = aRandomEdcSubjectReference()
        Request requestForStudy1 = Request.newBuilder()
                .withPatientId(givenPatientId)
                .withStudyId(studyId1)
                .withEdcSubjectReference(reference)
                .withDateOfConsent(DATE_NOW)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()
        given(patientEHRGateway.exists(studyId1, aPatientSearchCriteria(givenPatientId, LAST_NAME, FIRST_NAME, DATE_2009_09_09))).willReturn(true)
        and: "Another request for that patient, with a different study"
        StudyId studyId2 = generateKnownStudyId(USER_ID_KNOWN)
        Request requestForStudy2 = Request.newBuilder(requestForStudy1)
                .withStudyId(studyId2)
                .build()
        given(patientEHRGateway.exists(studyId2,  aPatientSearchCriteria(givenPatientId, LAST_NAME, FIRST_NAME, DATE_2009_09_09))).willReturn(true)

        when: "I register the Patient for both studies"
        registerSubject.register(requestForStudy1)
        registerSubject.register(requestForStudy2)

        then: "Two 'subject added to study' events were published"
        2 * eventPublisher.publishEvent({
            SubjectRegisteredEvent.isCase(it) &&
                    it.patientId == givenPatientId.id &&
                    it.namespace == givenPatientId.source
        })
        and: "Two 'subject added to study' events were published"
        2 * eventPublisher.publishEvent(_ as SubjectCreated)
        and: "The patient is added to the second study"
        def savedSubject = studyRepository.getStudyById(studyId2).subjects.items.find { s -> s.edcSubjectReference }
        with(savedSubject) {
            dateOfConsent.isEqual(DATE_NOW)
            edcSubjectReference == reference
            patientCDWReference == givenPatientId

            subjectId.id

            !deregisterReason
            !dateOfConsentWithdrawn
        }

    }

    def "A patient must exist in the clinical data warehouse"() {
        given: "A Subject Registration Request with a PatientId for which no patient exists"
        PatientCDWReference unknownPatientId = aRandomPatientId()
        Request request = Request.newBuilder()
                .withStudyId(generateKnownStudyId(USER_ID_KNOWN))
                .withPatientId(unknownPatientId)
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .withDateOfConsent(DATE_NOW)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()

        when: "Registering the Subject for the Study"
        registerSubject.register(request)

        then: "An exception is thrown indicating that the request has a PatientId for which no patient exists"
        UserException ex = thrown()
        ex.message == String.format("Unknown subject with cdw reference '%s/%s', last name '%s', first name '%s' and birth date '%s'.",
                unknownPatientId.source,
                unknownPatientId.id,
                LAST_NAME,
                FIRST_NAME,
                DATE_2009_09_09.format(DateTimeFormatter.ISO_LOCAL_DATE))
        and: "No event was published"
        0 * eventPublisher.publishEvent(*_)
    }

    def "A Patient is registered with a unrealistically early consent date"() {
        given: "A request with a consent date in 1850"
        StudyId knownStudyId = generateKnownStudyId(USER_ID_KNOWN)
        Request request = Request.newBuilder()
                .withPatientId(generateKnownPatientId())
                .withStudyId(knownStudyId)
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .withDateOfConsent(DATE_1850)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()

        when: "Registering the request"
        registerSubject.register(request)

        then: "An exception is thrown for the invalid consent date"
        UseCaseConstraintViolationException ex = thrown()
        ex.constraintViolations.first().message == "Date must be between 1950-01-01 and today."
        and: "No event was published"
        0 * eventPublisher.publishEvent(*_)
    }

    def "A Patient is registered with a consent date in the future"() {
        given: "A request with a consent date in the future"
        StudyId studyId = generateKnownStudyId(USER_ID_KNOWN)
        Request request = Request.newBuilder()
                .withPatientId(generateKnownPatientId())
                .withStudyId(studyId)
                .withEdcSubjectReference(aRandomEdcSubjectReference())
                .withDateOfConsent(DATE_TOMORROW)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()

        when: "Registering the request"
        registerSubject.register(request)

        then: "An exception is thrown for the invalid consent date"
        UseCaseConstraintViolationException ex = thrown()
        ex.constraintViolations.first().message == "Date must be between 1950-01-01 and today."
        and: "No event was published"
        0 * eventPublisher.publishEvent(*_)
    }

    def "The EDC Reference must be known in the registered EDC"() {
        given: "A Study with registered EDC"
        StudySnapshot study = generateKnownStudyWithoutEDCConnection(USER_ID_KNOWN)
        addReadSubjectConnection(study)
        and: "A Subject Registration Request with an EDC Reference which is not known in the EDC"
        EDCSubjectReference unknownEDCReference = aRandomEdcSubjectReference()
        edcStudyGateway.isRegisteredSubject(study.studyId, unknownEDCReference) >> Optional.of(false)
        Request request = Request.newBuilder()
                .withStudyId(study.studyId)
                .withPatientId(generateKnownPatientId())
                .withEdcSubjectReference(unknownEDCReference)
                .withDateOfConsent(DATE_NOW)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()

        when: "Registering the Subject for the Study"
        registerSubject.register(request)

        then: "An exception is thrown indicating that the request has an EDC Reference which does not exist"
        UserException ex = thrown()
        ex.message == "Unknown subject with edc reference '${unknownEDCReference.id}'."
        and: "No event was published"
        0 * eventPublisher.publishEvent(*_)
    }

    def "The EDC Reference must not be known without registered EDC"() {
        given: "A Study without registered EDC"
        StudySnapshot study = generateKnownStudyWithoutEDCConnection(USER_ID_KNOWN)
        and: "A valid Subject Registration Request with a random EDC Reference"
        EDCSubjectReference edcReference = aRandomEdcSubjectReference()
        def patientId = generateKnownPatientId()
        Request request = Request.newBuilder()
                .withStudyId(study.studyId)
                .withPatientId(patientId)
                .withEdcSubjectReference(edcReference)
                .withDateOfConsent(DATE_NOW)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()
        given(patientEHRGateway.exists(study.studyId, aPatientSearchCriteria(patientId, LAST_NAME, FIRST_NAME, DATE_2009_09_09))).willReturn(true)

        when: "Registering the Subject for the Study"
        registerSubject.register(request)

        then: "No exceptions are thrown"
        noExceptionThrown()
        and: "A registration event was published"
        1 * eventPublisher.publishEvent(_ as SubjectRegisteredEvent)
        and: "A 'subject added to study' event was published"
        1 * eventPublisher.publishEvent(_ as SubjectCreated)
    }

    def "A new EDCSubjectReference can be associated with a previously used PatientCDWReference after deregistering"() {
        given: "A patientId that will be reused for 2 subjects"
        PatientCDWReference patientCDWReference = generateKnownPatientId()
        and: "A request to register a new subject with the same patient cdw reference"
        StudyId studyId = aRandomStudyId()
        def deregisteredEdcReference = aRandomEdcSubjectReference()
        def newEdcReference = aRandomEdcSubjectReference()
        given(patientEHRGateway.exists(studyId, aPatientSearchCriteria(patientCDWReference, LAST_NAME, FIRST_NAME, DATE_2009_09_09))).willReturn(true)

        def subject = SubjectSnapshot
                .newBuilder()
                .withSubjectId(SubjectId.generate())
                .withEdcSubjectReference(deregisteredEdcReference)
                .withPatientCDWReference(patientCDWReference)
                .withDateOfConsent(DATE_NOW)
                .withDateOfConsentWithdrawn(DATE_NOW)
                .withDeregisterReason(DeregisterReason.CONSENT_RETRACTED)
                .build()
        generateKnownStudy(studyId, "name", "description", Arrays.asList(subject), USER_ID_KNOWN)

        Request requestForStudy1 = Request.newBuilder()
                .withPatientId(patientCDWReference)
                .withStudyId(studyId)
                .withEdcSubjectReference(newEdcReference)
                .withDateOfConsent(DATE_NOW)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()

        when: "I reregister the Patient for the study"
        registerSubject.register(requestForStudy1)

        then: "A 'subject added to study' events is published"
        1 * eventPublisher.publishEvent({
            SubjectRegisteredEvent.isCase(it) &&
                    it.patientId == patientCDWReference.id &&
                    it.namespace == patientCDWReference.source
        })
        and: "A 'subject added to study' events is published"
        1 * eventPublisher.publishEvent(_ as SubjectCreated)
        and: "The patient is added to the study"
        def savedSubject = studyRepository.getStudyById(studyId).subjects.items.find { s -> s.edcSubjectReference == newEdcReference }
        with(savedSubject) {
            dateOfConsent.isEqual(DATE_NOW)
            edcSubjectReference == newEdcReference
            patientCDWReference == patientCDWReference
            subjectId.id
            !deregisterReason
            !dateOfConsentWithdrawn
        }
    }

    def "A deregistered Subject can not be activated if the patient reference was already reused"() {
        given: "A patient reference"
        PatientCDWReference patientCDWReference = generateKnownPatientId()
        and: "A deregistered subject for the patient"
        def deregisteredEdcReference = aRandomEdcSubjectReference()
        def deregisteredSubject = SubjectSnapshot
                .newBuilder()
                .withSubjectId(SubjectId.generate())
                .withEdcSubjectReference(deregisteredEdcReference)
                .withPatientCDWReference(patientCDWReference)
                .withDateOfConsent(DATE_NOW)
                .withDateOfConsentWithdrawn(DATE_NOW)
                .withDeregisterReason(DeregisterReason.CONSENT_RETRACTED)
                .build()
        and: "An active subject for the patient"
        def activeEdcReference = aRandomEdcSubjectReference()
        def activeSubject = SubjectSnapshot
                .newBuilder()
                .withSubjectId(SubjectId.generate())
                .withEdcSubjectReference(activeEdcReference)
                .withPatientCDWReference(patientCDWReference)
                .withDateOfConsent(DATE_NOW)
                .build()
        and: "A study containing both subjects"
        StudyId studyId = aRandomStudyId()
        generateKnownStudy(studyId, "name", "description", Arrays.asList(deregisteredSubject, activeSubject), USER_ID_KNOWN)

        when: "Re-registering the deregistered subject"
        Request request = Request.newBuilder()
                .withPatientId(patientCDWReference)
                .withStudyId(studyId)
                .withEdcSubjectReference(deregisteredEdcReference)
                .withDateOfConsent(DATE_NOW)
                .withBirthDate(DATE_2009_09_09)
                .withLastName(LAST_NAME)
                .withFirstName(FIRST_NAME)
                .build()
        registerSubject.register(request)

        then: "The same patient can't be a subject twice in the same study"
        UserException ex = thrown()
        ex.message == "Can't register subject with cdw reference '"+ patientCDWReference.id +"' which is already in use."
        and: "No events are published"
        0 * eventPublisher.publishEvent(*_)
    }

    def aPatientSearchCriteria(PatientCDWReference givenPatientId, String lastName, String firstName, LocalDate birthDate) {
        PatientSearchCriteria.newBuilder()
                .withPatientCDWReference(givenPatientId)
                .withLastName(lastName)
                .withFirstName(firstName)
                .withBirthDate(birthDate)
                .build()
    }
}