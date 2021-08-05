package com.custodix.insite.local.ehr2edc.query.fhir

import com.custodix.insite.local.ehr2edc.EHRConnectionRepository
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query
import com.custodix.insite.local.ehr2edc.query.executor.common.query.QueryObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.EHRConnectionObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.EHRSystem
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate

import static com.custodix.insite.local.ehr2edc.populator.PopulationSpecificationObjectMother.aDefaultPopulationSpecification
import static com.custodix.insite.local.ehr2edc.populator.PopulationSpecificationObjectMother.aDefaultPopulationSpecificationBuilder
import static org.mockito.BDDMockito.given

class FhirEHRGatewayFactorySpec extends AbstractFhirDstu2Spec {

	@Autowired
	private FhirEHRGatewayFactory fhirEHRGatewayFactory
	@Autowired
	private EHRConnectionRepository ehrConnectionRepository

	def "I can query with unknown query"() {
		given: "an unknown query"
		Query unknownQuery = QueryObjectMother.anUnknownQuery()

		when: "query with unknown query"
		def gateway = fhirEHRGatewayFactory.create(unknownQuery, aDefaultPopulationSpecification())
		def queryResult = gateway.execute(unknownQuery, LocalDate.now())

		then: "an empty result is returned"
		queryResult.results.isEmpty()
	}

	def "Study can be handle by fhir gateway"() {
		String studyIdValue = "studyid-123"
		given: "StudyId with id '#studyIdValue'"
		StudyId studyId = StudyId.of(studyIdValue)
		and: "StudyId with id '#studyIdValue' has an fhir connection defined"
		fhirConnectionFor(studyId)

		when: "checking whether the fhir gateway can handle study"
		def canHandle = fhirEHRGatewayFactory.canHandle(QueryObjectMother.aDefaultQuery(), aDefaultPopulationSpecificationBuilder().withStudyId(studyId).build())

		then: "it can handle it"
		canHandle
	}

	def "Study cannot be handle by fhir gateway"() {
		String studyIdValue = "studyid-233"
		given: "StudyId with id '#studyIdValue'"
		StudyId studyId = StudyId.of(studyIdValue)
		and: "StudyId with id '#studyIdValue' has no fhir connection defined"
		noFhirConnectionFor(studyId)

		when: "checking whether the fhir gateway can handle study"
		def canHandle = fhirEHRGatewayFactory.canHandle(
				QueryObjectMother.aDefaultQuery(),
				aDefaultPopulationSpecificationBuilder().withStudyId(studyId).build())

		then: "it cannot handle it"
		!canHandle
	}

	void fhirConnectionFor(StudyId studyId) {
		given(ehrConnectionRepository.findByStudyIdAndSystem(studyId, EHRSystem.FHIR))
				.willReturn(Optional.of(EHRConnectionObjectMother.aFhirConnection(studyId)))
	}

	void noFhirConnectionFor(StudyId studyId) {
		given(ehrConnectionRepository.findByStudyIdAndSystem(studyId, EHRSystem.FHIR))
				.willReturn(Optional.empty())
	}
}
