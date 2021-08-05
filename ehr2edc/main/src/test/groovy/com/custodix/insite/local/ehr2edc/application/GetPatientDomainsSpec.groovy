package com.custodix.insite.local.ehr2edc.application

import com.custodix.insite.local.ehr2edc.domain.service.PatientEHRGateway
import com.custodix.insite.local.ehr2edc.patient.PatientDomain
import com.custodix.insite.local.ehr2edc.query.GetPatientDomains
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean

import static org.mockito.BDDMockito.given

class GetPatientDomainsSpec extends AbstractSpecification {

    @MockBean
    private PatientEHRGateway patientEHRGateway

    @Autowired
    private GetPatientDomains getPatientDomains

    def "Get patient domain list"() {
        given: "There are 2 patient from different domains"
        def aStudyId = StudyId.of('study-1')
        def domains = (0..1).collect { generateKnownPatientId().source }
        given(patientEHRGateway.getPatientDomains(aStudyId))
                .willReturn(domains.collect { PatientDomain.of(it) })

        when: "I query for all patient domains"
        def request = GetPatientDomains.Request.newBuilder().withStudyId(aStudyId).build()
        def response = getPatientDomains.getAll(request)

        then: "The list is not empty"
        response.patientDomains.name == domains
    }
}