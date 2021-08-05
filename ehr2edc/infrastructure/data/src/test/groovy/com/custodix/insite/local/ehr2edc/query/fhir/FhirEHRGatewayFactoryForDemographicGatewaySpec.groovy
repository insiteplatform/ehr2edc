package com.custodix.insite.local.ehr2edc.query.fhir

import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.DemographicTypeCriterion
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.DemographicQuery
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.Demographics
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.apache.http.HttpStatus

import static com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType.*
import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId
import static com.github.tomakehurst.wiremock.client.WireMock.*

class FhirEHRGatewayFactoryForDemographicGatewaySpec extends AbstractFhirEHRGatewayWithWiremockSpec {

	private static final String UNKNOWN_PATIENT_RESOURCE_ID = "unknown"
	private static final String UNKNOWN_PATIENT_SOURCE = "unknown"
	private static final PatientCDWReference UNKNOWN_PATIENT_CDW_REFERENCE = PatientCDWReference.newBuilder()
			.withId(UNKNOWN_PATIENT_RESOURCE_ID)
			.withSource(UNKNOWN_PATIENT_SOURCE)
			.build()

	def "Get gender from fhir"() {
		given: "A female patient 'Susanne'"
		def patientCDWReference = patientSusanne()

		when: "I query the gender for 'Susanne'"
		Query<Demographics> query = createGenderQuery(aRandomSubjectId(), patientCDWReference)
		def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
		Demographics demographics = gateway.execute(query, REFERENCE_DATE)

		then: "I expect exactly one demographic"
		demographics.getDemographics().size() == 1
		and: "with gender 'female'"
		demographics.getDemographics().get(0).value == "female"
	}

	def "Get patient with unknown gender in FHIR"() {
		given: "A patient without demographic info"
		def patientCDWReference = patientWithoutDemographicInfo()

		when: "I query the gender for the patient"
		Query<Demographics> query = createGenderQuery(aRandomSubjectId(), patientCDWReference)
		def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
		Demographics demographics = gateway.execute(query, REFERENCE_DATE)

		then: "I expect exactly zero demographic"
		demographics.getDemographics().size() == 0
	}

	def "Get unknown patient in FHIR"() {
		given: "An unknown patient"
		def patientCDWReference = unknownPatient()

		when: "I query the gender for the unknown patient"
		Query<Demographics> query = createGenderQuery(aRandomSubjectId(), patientCDWReference)
		def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
		Demographics demographics = gateway.execute(query, REFERENCE_DATE)

		then: "I expect exactly zero demographic"
		demographics.getDemographics().size() == 0
	}

	def "Get birth date from fhir"() {
		given: "A patient born on '1951-01-20'"
		def patientCDWReference = patientBornOn1951_01_20()

		when: "I query the birth date for the patient"
		Query<Demographics> query = createBirthDateQuery(aRandomSubjectId(), patientCDWReference)
		def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
		Demographics demographics = gateway.execute(query, REFERENCE_DATE)

		then: "I expect exactly one demographic"
		demographics.getDemographics().size() == 1
		and: "with birth date '1951-01-20'"
		demographics.getDemographics().get(0).value == "1951-01-20"
	}

	def "Get patient without birth date information from FHIR"() {
		given: "A patient without demographic info"
		def patientCDWReference = patientWithoutDemographicInfo()

		when: "I query the birth date for the patient"
		Query<Demographics> query = createBirthDateQuery(aRandomSubjectId(), patientCDWReference)
		def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
		Demographics demographics = gateway.execute(query, REFERENCE_DATE)

		then: "I expect exactly zero demographic"
		demographics.getDemographics().size() == 0
	}

	def "Get decease date from fhir"() {
		given: "A patient who died on '1963-01-20'"
		def patientCDWReference = patientDiedOn1963_01_20()

		when: "I query the decease date for the patient"
		Query<Demographics> query = createDeceasedDateQuery(aRandomSubjectId(), patientCDWReference)
		def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
		Demographics demographics = gateway.execute(query, REFERENCE_DATE)

		then: "I expect exactly one demographic"
		demographics.getDemographics().size() == 1
		and: "with a type of 'DEATH_DATE'"
		demographics.getDemographics().get(0).demographicType == DEATH_DATE
		and: "with decease date '1963-01-20'"
		demographics.getDemographics().get(0).value == "1963-01-20"
	}

	def "Get patient without decease date information from FHIR"() {
		given: "A patient without demographic info"
		def patientCDWReference = patientWithoutDemographicInfo()

		when: "I query the decease date for the patient"
		Query<Demographics> query = createDeceasedDateQuery(aRandomSubjectId(), patientCDWReference)
		def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
		Demographics demographics = gateway.execute(query, REFERENCE_DATE)

		then: "I expect exactly zero demographic"
		demographics.getDemographics().size() == 0
	}

	def "Get vital status for patient with deceased-flag in FHIR"() {
		given: "A deceased patient"
		def patientCDWReference = deceasedPatient()

		when: "I query the vital status for the patient"
		Query<Demographics> query = createVitalStatusQuery(aRandomSubjectId(), patientCDWReference)
		def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
		Demographics demographics = gateway.execute(query, REFERENCE_DATE)

		then: "I expect exactly one demographic"
		demographics.getDemographics().size() == 1
		and: "with vital status DECEASED"
		demographics.getDemographics().get(0).value == "DECEASED"
	}

	def "Get vital status for patient with deceased date in FHIR"() {
		given: "A patient who died on '2001-11-27'"
		def patientCDWReference = patientDeceasedOn2001_12_25()

		when: "I query the vital status for the patient"
		Query<Demographics> query = createVitalStatusQuery(aRandomSubjectId(), patientCDWReference)
		def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
		Demographics demographics = gateway.execute(query, REFERENCE_DATE)

		then: "I expect exactly one demographic"
		demographics.getDemographics().size() == 1
		and: "with vital status DECEASED"
		demographics.getDemographics().get(0).value == "DECEASED"
	}

	def "Get vital status for patient without deceased information in FHIR"() {
		given: "A patient that died on '2001-11-27'"
		def patientCDWReference = patientWithoutDemographicInfo()

		when: "I query the vital status for the patient"
		Query<Demographics> query = createVitalStatusQuery(aRandomSubjectId(), patientCDWReference)
		def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
		Demographics demographics = gateway.execute(query, REFERENCE_DATE)

		then: "I expect no demographic"
		demographics.getDemographics().size() == 0
	}

	private static DemographicQuery createGenderQuery(SubjectId subjectId, PatientCDWReference patientId) {
		def query = new DemographicQuery()
		query.addCriterion(DemographicTypeCriterion.type(GENDER))
		query = query.forSubject(SubjectCriterion.subjectIs(subjectId, patientId))
		(DemographicQuery)query
	}

	private static DemographicQuery createBirthDateQuery(SubjectId subjectId, PatientCDWReference patientId) {
		createQuery(subjectId, patientId, BIRTH_DATE)
	}

	private static DemographicQuery createDeceasedDateQuery(SubjectId subjectId, PatientCDWReference patientId) {
		createQuery(subjectId, patientId, DEATH_DATE)
	}

	private static DemographicQuery createVitalStatusQuery(SubjectId subjectId, PatientCDWReference patientId) {
		createQuery(subjectId, patientId, VITAL_STATUS)
	}
	private static DemographicQuery createQuery(SubjectId subjectId, PatientCDWReference patientId, DemographicType type) {
		def query = new DemographicQuery()
		query.addCriterion(DemographicTypeCriterion.type(type))
		(DemographicQuery)query.forSubject(SubjectCriterion.subjectIs(subjectId, patientId))
	}

	private PatientCDWReference unknownPatient() {
		stubFor(get(urlEqualTo("/baseDstu2/Patient?identifier=unknown%7Cunknown")).willReturn(aResponse()
				.withHeader("Content-Type", APPLICATION_JSON_FHIR_CHARSET_UTF_8)
				.withStatus(HttpStatus.SC_NOT_FOUND)
				.withBody(readToString(
						SEARCH_PATIENT_BASE_PATH + "/without_results.json"))))
		return UNKNOWN_PATIENT_CDW_REFERENCE
	}

	private PatientCDWReference patientBornOn1951_01_20() {
		stubFor(get(urlEqualTo("/baseDstu2/Patient/134")).willReturn(aResponse()
				.withHeader("Content-Type", APPLICATION_JSON_FHIR_CHARSET_UTF_8)
				.withBody(readToString(
						GET_PATIENT_BASE_PATH + "/susanne_born_on_1951-01-20.json"))))
		return SUSANNE_PATIENT_CDW_REFERENCE
	}

	private PatientCDWReference patientDiedOn1963_01_20() {
		stubFor(get(urlEqualTo("/baseDstu2/Patient/134")).willReturn(aResponse()
				.withHeader("Content-Type", APPLICATION_JSON_FHIR_CHARSET_UTF_8)
				.withBody(readToString(
						GET_PATIENT_BASE_PATH + "/susanne_decease_on_1963-01-20.json"))))
		return SUSANNE_PATIENT_CDW_REFERENCE
	}

	private PatientCDWReference deceasedPatient() {
		stubFor(get(urlEqualTo("/baseDstu2/Patient/134")).willReturn(aResponse()
				.withHeader("Content-Type", APPLICATION_JSON_FHIR_CHARSET_UTF_8)
				.withBody(readToString(
						GET_PATIENT_BASE_PATH + "/susanne_deceased.json"))))
		return SUSANNE_PATIENT_CDW_REFERENCE
	}

	private PatientCDWReference patientDeceasedOn2001_12_25() {
		stubFor(get(urlEqualTo("/baseDstu2/Patient/134")).willReturn(
				aResponse().withHeader("Content-Type", APPLICATION_JSON_FHIR_CHARSET_UTF_8)
						.withBody(readToString(
								GET_PATIENT_BASE_PATH + "/susanne_died_on_2001_12_25.json"))))
		return SUSANNE_PATIENT_CDW_REFERENCE
	}

	private PatientCDWReference patientWithoutDemographicInfo() {
		stubFor(get(urlEqualTo("/baseDstu2/Patient/134")).willReturn(aResponse()
				.withHeader("Content-Type", APPLICATION_JSON_FHIR_CHARSET_UTF_8)
				.withStatus(HttpStatus.SC_OK)
				.withBody(readToString(
						GET_PATIENT_BASE_PATH + "/without_demographic_info.json"))))
		return SUSANNE_PATIENT_CDW_REFERENCE
	}

}
