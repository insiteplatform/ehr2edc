package com.custodix.insite.local.ehr2edc.query.patient

import com.custodix.insite.local.ehr2edc.EHRConnectionRepository
import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2ClientFactory
import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2Configuration
import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientGateway
import com.custodix.insite.local.ehr2edc.query.mongo.patient.gateway.InProcessPatientGateway
import com.custodix.insite.local.ehr2edc.query.mongo.patient.gateway.InProcessPatientGatewayConfiguration
import com.custodix.insite.local.ehr2edc.query.mongo.patient.repository.PatientIdDocumentRepository
import com.custodix.insite.local.ehr2edc.vocabulary.EHRConnectionObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import spock.lang.Specification

import static org.mockito.BDDMockito.given

@SpringBootTest(classes = [ FhirDstu2Configuration.class, PatientEHRConfiguration.class, InProcessPatientGatewayConfiguration.class ])
class PatientEHRGatewayFactorySpec extends Specification {

	private static final StudyId STUDY_ID = StudyId.of("STUDY-123 ")

	@Autowired
	PatientEHRGatewayFactory patientEHRGatewayFactory

	@MockBean
	private EHRConnectionRepository ehrConnectionRepository
	@MockBean
	private PatientIdDocumentRepository patientIdDocumentRepository
	@MockBean
	private FhirDstu2ClientFactory fhirDstu2ClientFactory

	def "select fhir patient ehr gateway"() {
		given: "a study linked to fhir"
		def aStudyId = STUDY_ID

		aStudyIsLinkedToFhir(aStudyId)

		when: "selecting the patient ehr gateway"
		def gateway = patientEHRGatewayFactory.selectGateway(aStudyId)

		then: "gateway is the right type"
		gateway instanceof FhirDstu2PatientGateway
	}

	def "select in-process patient ehr gateway"() {
		given: "a study is not linked to fhir"
		def aStudyId = STUDY_ID
		aStudyIsNotLinkedToFhir(aStudyId)

		when: "selecting the patient ehr gateway"
		def gateway = patientEHRGatewayFactory.selectGateway(aStudyId)

		then: "gateway is the right type"
		gateway instanceof InProcessPatientGateway
	}

	def "select in-process patient ehr gateway mongo system"() {
		given: "a study is linked to mongo"
		def aStudyId = STUDY_ID
		aStudyIsLinkedToMongo(aStudyId)

		when: "selecting the patient ehr gateway"
		def gateway = patientEHRGatewayFactory.selectGateway(aStudyId)

		then: "gateway is the right type"
		gateway instanceof InProcessPatientGateway
	}

	def "isFhir is true when the study is linked to FHIR"() {
		given: "a study linked to fhir"
		def aStudyId = STUDY_ID
		aStudyIsLinkedToFhir(aStudyId)

		when: "querying if the study is linked"
		def isFhir = patientEHRGatewayFactory.isFhir(aStudyId)

		then: "the study is linked"
		isFhir
	}

	def "isFhir is false when the study is not linked to FHIR"() {
		given: "a study is not linked to fhir"
		def aStudyId = STUDY_ID
		aStudyIsNotLinkedToFhir(aStudyId)

		when: "querying if the study is linked"
		def isFhir = patientEHRGatewayFactory.isFhir(aStudyId)

		then: "the study is linked"
		!isFhir
	}


	def aStudyIsLinkedToFhir(StudyId studyId) {
		def aFhirConnection = EHRConnectionObjectMother.aFhirConnection(studyId)
		given(ehrConnectionRepository.findByStudyId(studyId)).willReturn(Optional.of(aFhirConnection))
	}

	def aStudyIsLinkedToMongo(StudyId studyId) {
		def aMongoConnection = EHRConnectionObjectMother.aMongoConnection(studyId)
		given(ehrConnectionRepository.findByStudyId(studyId)).willReturn(Optional.of(aMongoConnection))
	}

	def aStudyIsNotLinkedToFhir(StudyId studyId) {
		given(ehrConnectionRepository.findByStudyId(studyId)).willReturn(Optional.empty())
	}
}
