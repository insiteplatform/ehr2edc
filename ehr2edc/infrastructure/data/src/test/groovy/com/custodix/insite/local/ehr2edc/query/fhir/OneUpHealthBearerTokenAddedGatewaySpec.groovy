package com.custodix.insite.local.ehr2edc.query.fhir

import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.DemographicTypeCriterion
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.DemographicQuery
import com.custodix.insite.local.ehr2edc.query.executor.demographic.query.Demographics
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import org.apache.http.HttpStatus

import static com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType.GENDER
import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId
import static com.github.tomakehurst.wiremock.client.WireMock.*

class OneUpHealthBearerTokenAddedGatewaySpec extends AbstractFhirEHRGatewayWithWiremockSpec {

	def "Get gender from fhir through 1up.health service"() {
		given: "A female patient 'Susanne'"
		def patientCDWReference = patientSusanneOneUp()

		when: "I query the gender for 'Susanne'"
		Query<Demographics> query = createGenderQuery(aRandomSubjectId(), patientCDWReference)
		def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
		Demographics demographics = gateway.execute(query, REFERENCE_DATE)

		then: "I expect exactly one demographic"
		demographics.getDemographics().size() == 1
		and: "with gender 'female'"
		demographics.getDemographics().get(0).value == "female"
	}


	private static DemographicQuery createGenderQuery(SubjectId subjectId, PatientCDWReference patientId) {
		def query = new DemographicQuery()
		query.addCriterion(DemographicTypeCriterion.type(GENDER))
		query = query.forSubject(SubjectCriterion.subjectIs(subjectId, patientId))
		(DemographicQuery)query
	}

	@Override
	protected String getBasePath() {
		return "/api.1up.health$FHIR_URL_BASE"
	}

	PatientCDWReference patientSusanneOneUp() {
		stubFor(get(urlEqualTo("${getBasePath()}/Patient/" + PATIENT_SUSANNE_RESOURCE_ID))
				.withHeader("Authorization", new EqualToPattern("Bearer grizzly-token"))
				.willReturn(aResponse()
				.withHeader("Content-Type", APPLICATION_JSON_FHIR_CHARSET_UTF_8)
				.withStatus(HttpStatus.SC_OK)
				.withBody(readToString(GET_PATIENT_BASE_PATH + "/susanne.json"))))

		return SUSANNE_PATIENT_CDW_REFERENCE
	}

}
