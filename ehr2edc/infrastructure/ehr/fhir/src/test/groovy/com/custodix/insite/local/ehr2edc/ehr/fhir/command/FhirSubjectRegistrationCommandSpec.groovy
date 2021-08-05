package com.custodix.insite.local.ehr2edc.ehr.fhir.command

import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatus
import com.custodix.insite.local.ehr2edc.ehr.domain.event.EHRSubjectRegistrationStatusUpdated
import com.custodix.insite.local.ehr2edc.ehr.fhir.config.EhrFhirConfiguration
import com.custodix.insite.local.ehr2edc.ehr.main.domain.event.DomainEventPublisher
import com.custodix.insite.local.ehr2edc.ehr.main.domain.event.EventPublisher
import com.custodix.insite.local.ehr2edc.query.patient.PatientEHRGatewayFactory
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.mockito.ArgumentCaptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import spock.lang.Specification

import static org.mockito.BDDMockito.given
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyZeroInteractions

@SpringBootTest(classes = [ EhrFhirConfiguration ])
class FhirSubjectRegistrationCommandSpec extends Specification {

    public static final StudyId STUDY_ID = StudyId.of("study-123")
    public static final String PATIENT_ID = "patientId-456"
    public static final String PATIENT_ID_SOURCE = "patientIdSource"
    public static final String SUBJECT_ID = "subjectId-123"

    @MockBean
    protected EventPublisher eventPublisher

    @MockBean
    protected PatientEHRGatewayFactory patientEHRGatewayFactory

    @Autowired
    protected FhirSubjectRegistration fhirSubjectRegistration

    def setup() {
        DomainEventPublisher.setPublisher(eventPublisher)
    }

    ArgumentCaptor<EHRSubjectRegistrationStatusUpdated> eventCaptor = ArgumentCaptor.forClass(EHRSubjectRegistrationStatusUpdated)

    def "An EHRSubjectRegistrationStatusUpdated event is sent when the study is linked to fhir"() {
        given: "a request "
        def aPatient = PatientCDWReference.newBuilder().withId(PATIENT_ID).withSource(PATIENT_ID_SOURCE).build()
        def request = FhirSubjectRegistration.Request.newBuilder()
                .withPatientId(aPatient)
                .withStudyId(STUDY_ID)
                .withSubjectId(SubjectId.of(SUBJECT_ID))
                .build()
        given(patientEHRGatewayFactory.isFhir(STUDY_ID)).willReturn(true)

        when: "register subject from fhir"
        fhirSubjectRegistration.register(request)

        then: "an EHRSubjectRegistrationStatusUpdated is published"
        verify(eventPublisher).publishEvent(eventCaptor.capture())
        eventCaptor.value.subjectId == SUBJECT_ID
        eventCaptor.value.patientId == PATIENT_ID
        eventCaptor.value.namespace == PATIENT_ID_SOURCE
        eventCaptor.value.status == EHRSubjectRegistrationStatus.REGISTERED
    }

    def "An EHRSubjectRegistrationStatusUpdated event is not sent when the study is not linked to fhir"() {
        given: "a request "
        def aPatient = PatientCDWReference.newBuilder().withId(PATIENT_ID).withSource(PATIENT_ID_SOURCE).build()
        def request = FhirSubjectRegistration.Request.newBuilder()
                .withPatientId(aPatient)
                .withStudyId(STUDY_ID)
                .withSubjectId(SubjectId.of(SUBJECT_ID))
                .build()
        given(patientEHRGatewayFactory.isFhir(STUDY_ID)).willReturn(false)

        when: "register subject from fhir"
        fhirSubjectRegistration.register(request)

        then: "an EHRSubjectRegistrationStatusUpdated is not published"
        verifyZeroInteractions(eventPublisher)
    }
}