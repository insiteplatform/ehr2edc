package com.custodix.insite.local.ehr2edc.query.fhir

import com.custodix.insite.local.ehr2edc.query.fhir.patient.FhirDstu2PatientGatewayFactory
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.PatientSearchCriteria
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore

import java.time.LocalDate

class FhirDstu2PatientGatewaySpec extends AbstractFhirEHRGatewayWithWiremockSpec {

    private static final StudyId STUDY_ID = StudyId.of("STUDY-123 ")
    private static final String PATIENT_DOMAIN = FhirJson.Identity.aDefaultSystem()
    private static final String PATIENT_ID = "0ba1b4c1-82fb-4f24-87ff-1bc302cfc6ba"
    private static final String PATIENT_NAME_GIVEN = "Carolyn"
    private static final String PATIENT_NAME_FAMILY = "Broadhead"
    private static final LocalDate PATIENT_BIRTH_DATE =  LocalDate.of(2009, 9, 9)
    private static final int DEFAULT_LIMIT = 100
    private static final PatientCDWReference PATIENT_CDW_REFERENCE = PatientCDWReference.newBuilder()
            .withSource(PATIENT_DOMAIN)
            .withId(PATIENT_ID)
            .build()

    @Autowired
    private final FhirDstu2PatientGatewayFactory fhirDstu2PatientGatewayFactory

    def "a patient exists (To be remove once E2E-620 is implemented)"() {
        given: "a patient reference"
        aSearchForAPatientBySystemAndIdentifierReturnsAResult(PATIENT_DOMAIN, PATIENT_ID)

        when: "getting matching patients"
        def patientEHRGateway = fhirDstu2PatientGatewayFactory.create(STUDY_ID)
        def exists = patientEHRGateway.exists(aPatientSearchCriteria(PATIENT_CDW_REFERENCE))

        then: "the patient exists"
        exists
    }

    def "a patient does not exist (To be remove once E2E-620 is implemented)"() {
        given: "a patient reference"
        aSearchForAPatientBySystemAndIdentifierReturnsNoResult(PATIENT_DOMAIN, PATIENT_ID)

        when: "getting matching patients"
        def patientEHRGateway = fhirDstu2PatientGatewayFactory.create(STUDY_ID)
        def exists = patientEHRGateway.exists(aPatientSearchCriteria(PATIENT_CDW_REFERENCE))

        then: "the patient exists"
        !exists
    }

    //Can be remove once E2E-620 is implemented
    def aSearchForAPatientBySystemAndIdentifierReturnsNoResult(String system, String id) {
        aSearchReturnsNoResult(resourceUrl(RESOURCE_PATIENT), [ identifier: "${system}|${id}" ])
    }

    @Ignore("Unitl E2E-630")
    def "a patient exists"() {
        given: "a patient reference"
        def patientSearchCriteria = aPatientSearchCriteria()
        aSearchForAPatientByPatientSearchCriteriaReturnsAResult(patientSearchCriteria)

        when: "getting matching patients"
        def patientEHRGateway = fhirDstu2PatientGatewayFactory.create(STUDY_ID)
        def exists = patientEHRGateway.exists(patientSearchCriteria)

        then: "the patient exists"
        exists
    }

    @Ignore("Unitl E2E-630")
    def "a patient does not exist"() {
        given: "a patient reference"
        def patientSearchCriteria = aPatientSearchCriteria()
        aSearchForAPatientByPatientSearchCriteriaReturnsNoResult(patientSearchCriteria)

        when: "getting matching patients"
        def patientEHRGateway = fhirDstu2PatientGatewayFactory.create(STUDY_ID)
        def exists = patientEHRGateway.exists(patientSearchCriteria)

        then: "the patient exists"
        !exists
    }

    def "a filtered patient query returns results"() {
        given: "a patient domain and identifier"
        def aFilter = PATIENT_NAME_FAMILY.substring(0, 4)
        aSearchForAPatientByFilterReturnsAResult(aFilter, PATIENT_DOMAIN, PATIENT_ID)

        when: "getting matching patients"
        def patientEHRGateway = fhirDstu2PatientGatewayFactory.create(STUDY_ID)
        def patients = patientEHRGateway.getFiltered(PATIENT_DOMAIN, aFilter, DEFAULT_LIMIT)

        then: "patients list is not empty"
        !patients.isEmpty()
    }

    def "a filtered patient query returns no results"() {
        given: "a patient domain and identifier"
        def aFilter = PATIENT_NAME_FAMILY.substring(0, 4)
        aSearchForAPatientByFilterReturnsNoResult(aFilter, PATIENT_DOMAIN)

        when: "getting matching patients"
        def patientEHRGateway = fhirDstu2PatientGatewayFactory.create(STUDY_ID)
        def patients = patientEHRGateway.getFiltered(PATIENT_DOMAIN, aFilter, DEFAULT_LIMIT)

        then: "patients list is not empty"
        patients.isEmpty()
    }

    def "a filtered patient query limits results"() {
        given: "a patient domain and identifier"
        def aLimit = 5
        def aFilter = "someFilter"
        aSearchForAPatientByFilterReturnsManyResults(aFilter, PATIENT_DOMAIN, aLimit)

        when: "getting matching patients"
        def patientEHRGateway = fhirDstu2PatientGatewayFactory.create(STUDY_ID)
        def patients = patientEHRGateway.getFiltered(PATIENT_DOMAIN, aFilter, aLimit)

        then: "patients list is not empty"
        patients.size() == aLimit
    }

    def aSearchForAPatientByFilterReturnsAResult(String filter, String system, String id) {
        def params = [ identifier: "${system}|", _content: filter, _count: Integer.toString(DEFAULT_LIMIT) ]
        aSearchReturnsAPatient(resourceUrl(RESOURCE_PATIENT), params, system, id)
    }

    def aSearchForAPatientByFilterReturnsManyResults(String filter, String system, Integer count) {
        def resources = (1..count).collect { FhirJson.Patient.aRandomPatientWithSystem(system) }
        def params = [ identifier: "${system}|", _content: filter, _count: count.toString() ]
        aSearchReturnsResults(resourceUrl(RESOURCE_PATIENT), params, resources)
    }

    def aSearchForAPatientByFilterReturnsNoResult(String filter, String system) {
        def params = [ identifier: "${system}|", _content: filter, _count: Integer.toString(DEFAULT_LIMIT) ]
        aSearchReturnsNoResult(resourceUrl(RESOURCE_PATIENT), params)
    }

    def aPatientSearchCriteria( ) {
        PatientSearchCriteria.newBuilder()
                .withPatientCDWReference(PATIENT_CDW_REFERENCE)
                .withLastName(PATIENT_NAME_FAMILY)
                .withFirstName(PATIENT_NAME_GIVEN)
                .withBirthDate(PATIENT_BIRTH_DATE)
                .build()
    }

    def aPatientSearchCriteria( patientSearchCriteria) {
        PatientSearchCriteria.newBuilder().withPatientCDWReference(patientSearchCriteria).build()
    }
}
