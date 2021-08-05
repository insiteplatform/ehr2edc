package com.custodix.insite.local.ehr2edc.rest.patient

import com.custodix.insite.local.ehr2edc.query.GetPatientDomains
import com.custodix.insite.local.ehr2edc.rest.ControllerSpec
import com.fasterxml.jackson.databind.ObjectMapper
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.FileCopyUtils

import static java.util.stream.Collectors.toList
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.when
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@ContextConfiguration(classes = PatientDomainController)
class PatientDomainControllerSpec extends ControllerSpec {
    private static final List<String> PATIENT_DOMAIN_NAMES = Arrays.asList("MASTER_PATIENT_INDEX", "OTHER_INDEX")

    @MockBean
    private GetPatientDomains getPatientDomains
    @Autowired
    private ObjectMapper objectMapper

    def "List all the patient domain names"() {
        given: "2 patient domains exist"
        when(getPatientDomains.getAll(any())).thenReturn(buildResponse())

        when: "I browse for the patient domain names"
        def request = get("/ehr2edc/patients/domains?studyId={studyId}", "study-1")
        def response = mockMvc.perform(request).andReturn().response

        then: "I expect the call to have succeeded"
        response.status == HttpStatus.OK.value()
        response.contentType == "application/json;charset=UTF-8"
        and: "The patient domain names are returned"
        JSONAssert.assertEquals(response.contentAsString,
                readJsonForController("patientIdentifierSources"),
                JSONCompareMode.STRICT)
    }

    private GetPatientDomains.Response buildResponse() {
        def patientDomains = PATIENT_DOMAIN_NAMES.stream().map({ name -> buildPatientDomain(name) }).collect(toList())
        GetPatientDomains.Response.newBuilder().withPatientDomains(patientDomains).build()
    }

    private GetPatientDomains.PatientDomain buildPatientDomain(String name) {
        GetPatientDomains.PatientDomain.newBuilder().withName(name).build()
    }

    static def readJsonForController(String path) {
        return new String(FileCopyUtils.copyToByteArray(new ClassPathResource("samples/patientdomaincontroller/" + path + ".json").getFile()))
    }
}