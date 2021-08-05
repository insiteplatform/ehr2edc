package com.custodix.insite.local.ehr2edc.query.fhir

import ca.uhn.fhir.context.FhirContext
import com.custodix.insite.local.ehr2edc.EHRConnectionRepository
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification
import com.custodix.insite.local.ehr2edc.vocabulary.EHRConnectionObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.PatientSearchCriteria
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import groovy.json.JsonOutput
import org.apache.commons.lang3.StringUtils
import org.apache.http.HttpStatus
import org.hl7.fhir.instance.model.api.IBaseResource
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.util.UriTemplate

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.function.Function
import java.util.function.Supplier

import static com.custodix.insite.local.ehr2edc.populator.PopulationSpecificationObjectMother.aDefaultPopulationSpecificationBuilder
import static com.github.tomakehurst.wiremock.client.WireMock.*
import static org.mockito.BDDMockito.given

abstract class AbstractFhirEHRGatewayWithWiremockSpec extends AbstractFhirDstu2Spec {

    private static final int MIN_NO_THREADS_TO_MAKE_WIREMOCK_SERVER_START = 7

    static final String APPLICATION_JSON_FHIR_CHARSET_UTF_8 = "application/json+fhir;charset=utf-8"
    static final String FHIR_URL_BASE = "/baseDstu2"
    final UriTemplate FHIR_URL = new UriTemplate("http://localhost:{port}" + getBasePath())
    static final String PATIENT_SUSANNE_RESOURCE_ID = "134"
    static final String PATIENT_SUSANNE_IDENTIFIER_SYSTEM = "http://acme.org/mrns"
    static final String PATIENT_SUSANNE_IDENTIFIER_ID = "12345"
    static final String GET_PATIENT_BASE_PATH = "/samples/patient/get"
    static final String SEARCH_PATIENT_BASE_PATH = "/samples/patient/search"
    static final PatientCDWReference SUSANNE_PATIENT_CDW_REFERENCE = PatientCDWReference.newBuilder()
            .withId(PATIENT_SUSANNE_IDENTIFIER_ID)
            .withSource(PATIENT_SUSANNE_IDENTIFIER_SYSTEM)
            .build()
    static final StudyId STUDY_ID = StudyId.of("STUDY-123 ")
    static final PopulationSpecification POPULATION_SPECIFICATION = aDefaultPopulationSpecificationBuilder().withStudyId(STUDY_ID).build()
    static final LocalDate REFERENCE_DATE = LocalDate.parse("2020-01-01")
    static final String INCLUDE_PARAM_NAME = "_include"

    static final RESOURCE_PATIENT = "Patient"
    static final RESOURCE_MEDICATION = "Medication"
    static final RESOURCE_MEDICATION_ORDER = "MedicationOrder"
    static final RESOURCE_MEDICATION_DISPENSE = "MedicationDispense"
    static final RESOURCE_MEDICATION_ADMINISTRATION = "MedicationAdministration"
    static final RESOURCE_MEDICATION_STATEMENT = "MedicationStatement"

    @Rule
    public WireMockRule wireMockserver = new WireMockRule(WireMockConfiguration.wireMockConfig()
            .containerThreads(MIN_NO_THREADS_TO_MAKE_WIREMOCK_SERVER_START).dynamicPort())

    @Autowired
    private EHRConnectionRepository ehrConnectionRepository

    @Autowired
    FhirEHRGatewayFactory fhirEHRGatewayFactory

    def setup() {
        fhirDstu2Metadata()
        stubSearchPatientByIdentifier()
        aStudyIsLinkedToFhir(STUDY_ID, fhirUrl())
    }

    StubMapping fhirDstu2Metadata() {
        stubFor(get(urlEqualTo("${getBasePath()}/metadata")).willReturn(aResponse()
                .withHeader("Content-Type", APPLICATION_JSON_FHIR_CHARSET_UTF_8)
                .withBody(readToString("/samples/metadata/get/metadata.json"))))
    }

    PatientCDWReference patientSusanne() {
        stubFor(get(urlEqualTo("${getBasePath()}/Patient/" + PATIENT_SUSANNE_RESOURCE_ID)).willReturn(aResponse()
                .withHeader("Content-Type", APPLICATION_JSON_FHIR_CHARSET_UTF_8)
                .withStatus(HttpStatus.SC_OK)
                .withBody(readToString(GET_PATIENT_BASE_PATH + "/susanne.json"))))

        return SUSANNE_PATIENT_CDW_REFERENCE
    }

    ResponseDefinitionBuilder getOkResponse(IBaseResource resource) {
        def body = FhirContext.forDstu2().newJsonParser().encodeResourceToString(resource)
        return aResponse().withHeader("Content-Type", APPLICATION_JSON_FHIR_CHARSET_UTF_8)
                .withStatus(HttpStatus.SC_OK)
                .withBody(body)
    }

    ResponseDefinitionBuilder getOkResponse(String patientResourceId, String bodyPath) {
        return aResponse().withHeader("Content-Type", APPLICATION_JSON_FHIR_CHARSET_UTF_8)
                .withStatus(HttpStatus.SC_OK)
                .withBody(getBody(patientResourceId, bodyPath))
    }

    ResponseDefinitionBuilder getOkResponse(String bodyPath) {
        return aResponse().withHeader("Content-Type", APPLICATION_JSON_FHIR_CHARSET_UTF_8)
                .withStatus(HttpStatus.SC_OK)
                .withBody(getBody(bodyPath))
    }

    protected String getBasePath(){
        return FHIR_URL_BASE
    }

    private String getBody(String patientResourceId, String templatePath) {
        String bodyTemplate = getBody(templatePath)
        return StringUtils.replace(bodyTemplate, "\${patientResourceId}", patientResourceId)
    }

    private String getBody(String templatePath) {
        String bodyTemplate = readToString(templatePath)
        return StringUtils.replace(bodyTemplate, "\${serverPort}", "${wireMockserver.port()}")
    }

    void stubSearchPatientByIdentifier() {
        def url = "${getBasePath()}/Patient?identifier=http%3A%2F%2Facme.org%2Fmrns%7C12345"
        def response = anOkJsonResponse({ -> readToString(SEARCH_PATIENT_BASE_PATH + "/by_resource_id.json")})
        stubFor(get(urlEqualTo(url)).willReturn(response))
    }

    def aStudyIsLinkedToFhir(StudyId studyId, String url) {
        def fhirConnection = EHRConnectionObjectMother.aFhirConnection(studyId, url)
        given(ehrConnectionRepository.getByStudyId(studyId)).willReturn(fhirConnection)
    }

    ResponseDefinitionBuilder anOkJsonResponse(Supplier<byte[]> body) {
        return aResponse().withHeader("Content-Type", APPLICATION_JSON_FHIR_CHARSET_UTF_8)
                .withStatus(HttpStatus.SC_OK)
                .withBody(body.get())
    }

    def aSearchReturnsBundle(String queryUrl, Map<String, String> queryParams, Function<String, FhirJson.Bundle> bundleWithUrl) {
        def mapping = get(urlPathEqualTo(queryUrl))
        queryParams.forEach { k,v -> mapping.withQueryParam(k, equalTo(v))}
        def formattedUrl = formatUrl(queryUrl, queryParams)
        def bundle = bundleWithUrl.apply(formattedUrl)
        def response = anOkJsonResponse({ -> JsonOutput.toJson(bundle) })
        stubFor(mapping.willReturn(response))
    }

    def aSearchReturnsNoResult(String queryUrl, Map<String,String> queryParams) {
        def createBundle = { String url -> FhirJson.Bundle.empty(url) }
        return aSearchReturnsBundle(queryUrl, queryParams, createBundle)
    }

    def aSearchReturnsResults(String queryUrl, Map<String,String> queryParams, List resources) {
        def createBundle = { String url -> FhirJson.Bundle.many(url, resources) }
        return aSearchReturnsBundle(queryUrl, queryParams, createBundle)
    }

    def aSearchReturnsAPatient(String queryUrl, Map<String,String> queryParams, String system, String id) {
        def resource = FhirJson.Patient.aRandomPatientWithSystemAndId(system, id)
        aSearchReturnsResults(queryUrl, queryParams, [resource])
        return resource
    }

    def aSearchForAResourceByIdReturnsResults(FhirJson.Resource resource) {
        def url = resourceUrl(resource.resourceType)
        def mapping = get(urlPathEqualTo("${url}/${resource.id}"))
        def response = anOkJsonResponse({ -> JsonOutput.toJson(resource) })
        stubFor(mapping.willReturn(response))
    }

    def aSearchForAPatientBySystemAndIdentifierReturnsAResult(String system, String id) {
        def formattedId = "${system}|${id}"
        def url = resourceUrl(RESOURCE_PATIENT)
        return aSearchReturnsAPatient(url, [ identifier: formattedId ], system, id)
    }

    def aSearchReturnsAPatient(String queryUrl, Map<String,String> queryParams, PatientSearchCriteria patientSearchCriteria) {
        def resource = FhirJson.Patient.aRandomPatientWithPatientSearchCriteria(patientSearchCriteria);
        aSearchReturnsResults(queryUrl, queryParams, [resource])
        return resource
    }

    def aSearchForAPatientByPatientSearchCriteriaReturnsAResult(PatientSearchCriteria patientSearchCriteria) {
        def url = resourceUrl(RESOURCE_PATIENT)
        return aSearchReturnsAPatient(url,
                createQueryParams(patientSearchCriteria),
                patientSearchCriteria)
    }

    def aSearchForAPatientByPatientSearchCriteriaReturnsNoResult(PatientSearchCriteria patientSearchCriteria) {
        aSearchReturnsNoResult(resourceUrl(RESOURCE_PATIENT),
                createQueryParams(patientSearchCriteria),
        )
    }

    def createQueryParams(PatientSearchCriteria patientSearchCriteria) {
        def formattedId = "${patientSearchCriteria.patientCDWReference.source}|${patientSearchCriteria.patientCDWReference.id}"
        [
                identifier: formattedId,
                family  : patientSearchCriteria.lastName,
                given : patientSearchCriteria.firstName,
                birthdate : patientSearchCriteria.birthDate.format(DateTimeFormatter.ISO_LOCAL_DATE)]
    }

    def aSearchForAPatientByCDWReferenceReturnsAResult(PatientCDWReference ref) {
        return aSearchForAPatientBySystemAndIdentifierReturnsAResult(ref.getSource(), ref.getId())
    }

    def aSearchForAPatientBySystemAndIdentifierReturnsAResult(String fhirId, String system, String id) {
        def resource = FhirJson.Patient.aRandomPatientWithSystemAndId(fhirId, system, id)
        aSearchForAPatientBySystemAndIdentifierReturnsResults(system, id, [ resource ])
    }

    def aSearchForAPatientBySystemAndIdentifierReturnsResults(String system, String id, List<FhirJson.Resource> resources) {
        def formattedId = "${system}|${id}"
        def url = resourceUrl(RESOURCE_PATIENT)
        aSearchReturnsResults(url, [ identifier: formattedId ], resources)
        resources.forEach { resource -> aSearchForAResourceByIdReturnsResults(resource) }
    }

    def aSearchForPatientsByIdOrSystemAndIdentifierReturnsResults(String system, String id, List<FhirJson.Resource> resources) {
        aSearchForAPatientBySystemAndIdentifierReturnsResults(system, id, resources)
        resources.forEach { resource -> aSearchForAResourceByIdReturnsResults(resource) }
    }

    def resourceUrl(String resource) {
        return "${getBasePath()}/${resource}"
    }

    def fhirUrl() {
        return FHIR_URL.expand(wireMockserver.port()).toString()
    }

    def formatUrl(String url, Map<String,String> queryParams) {
        def formattedParams = queryParams.collect { k,v -> "${k}=${v}" }
        return url + (formattedParams.isEmpty()? "" : "?" + formattedParams.join("&"))
    }
}
