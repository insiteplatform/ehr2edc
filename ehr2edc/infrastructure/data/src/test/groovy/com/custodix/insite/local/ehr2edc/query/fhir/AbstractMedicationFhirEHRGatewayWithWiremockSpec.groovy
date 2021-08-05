package com.custodix.insite.local.ehr2edc.query.fhir

import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement

import static ca.uhn.fhir.model.dstu2.resource.MedicationDispense.INCLUDE_MEDICATION
import static com.github.tomakehurst.wiremock.client.WireMock.*

abstract class AbstractMedicationFhirEHRGatewayWithWiremockSpec extends AbstractFhirEHRGatewayWithWiremockSpec {

    static final String TRAZODONE_CODE = "856377"
    static final String TRAZODONE_NAME = "Trazodone 50 MG Oral Tablet"
    static final String CODEINE_PHOSPHATE_RESOURCE_ID = "172917"
    static final String CODEINE_PHOSPHATE_CODE = "261000"
    static final String CODEINE_PHOSPHATE_NAME = "Codeine phosphate (substance)"
    static final String AMOXICILLIN_CODE = "308189"
    static final String AMOXICILLIN_NAME = "Amoxicillin 80 MG/ML Oral Suspension"
    static final String ASPIRINE_CODE = '370167008'
    static final String ASPIRIN_NAME = "aspirin 81mg e/c tablet"
    static final String ORAL_DOSE_FORM_CODE = "385268001"
    static final String ORAL_ROUTE_SNOMED_CODE = "26643006"
    static final String MEDICATION_ADMINISTRATION_URL = "/baseDstu2/MedicationAdministration"
    static final String MEDICATION_DISPENSE_URL = "/baseDstu2/MedicationDispense"
    static final String MEDICATION_STATEMENT_URL = "/baseDstu2/MedicationStatement"
    static final String MEDICATION_ORDER_URL = "/baseDstu2/MedicationOrder"
    static final String PATH_TRAZODONE_MEDICATION_ADMINISTRATION_TEMPLATE =
            "/samples/medication_administration/search/template_trazodone.json"
    static final String CODEINE_PHOSPHATE_MEDICATION_URL = "/baseDstu2/Medication/" + CODEINE_PHOSPHATE_RESOURCE_ID


    private static final String PATH_0_MEDICATION_ADMINISTRATIONS_TEMPLATE =
            "/samples/medication_administration/search/template_with_0_medication_administrations.json"
    private static final String PATH_0_MEDICATION_DESPENSE_TEMPLATE =
            "/samples/medication_dispense/search/template_with_0_medication_dispense.json"
    private static final String PATH_0_MEDICATION_STATEMENT_TEMPLATE =
            "/samples/medication_statement/search/template_with_0_statement.json"
    private static final String PATH_CODEINE_PHOSPHATE_MEDICATION =
            "/samples/medication/get/codeine_phosphate.json"

    void zeroMedicationAdministrationForSusanne() {
        stubFor(get(urlPathEqualTo(MEDICATION_ADMINISTRATION_URL))
                .withQueryParam(MedicationAdministration.SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam(INCLUDE_PARAM_NAME, equalTo(MedicationAdministration.INCLUDE_MEDICATION.getValue()))
                .willReturn(getOkResponse(
                        PATIENT_SUSANNE_RESOURCE_ID, PATH_0_MEDICATION_ADMINISTRATIONS_TEMPLATE)))
    }

    void zeroMedicationDispenseForSusanne() {
        stubFor(get(urlPathEqualTo(MEDICATION_DISPENSE_URL))
                .withQueryParam(MedicationAdministration.SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam(INCLUDE_PARAM_NAME, equalTo(INCLUDE_MEDICATION.getValue()))
                .willReturn(getOkResponse(
                        PATIENT_SUSANNE_RESOURCE_ID, PATH_0_MEDICATION_DESPENSE_TEMPLATE)))
    }

    void zeroMedicationOrderForSusanne() {
        stubFor(get(urlPathEqualTo(MEDICATION_ORDER_URL))
                .withQueryParam(MedicationOrder.SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .willReturn(getOkResponse(
                        PATIENT_SUSANNE_RESOURCE_ID, PATH_0_MEDICATION_DESPENSE_TEMPLATE)))
    }

    void zeroMedicationStatementForSusanne() {
        stubFor(get(urlPathEqualTo(MEDICATION_STATEMENT_URL))
                .withQueryParam(MedicationStatement.SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam(INCLUDE_PARAM_NAME, equalTo(MedicationStatement.INCLUDE_MEDICATION.getValue()))
                .willReturn(getOkResponse(
                        PATIENT_SUSANNE_RESOURCE_ID, PATH_0_MEDICATION_STATEMENT_TEMPLATE)))
    }

    void codeinePhosphateMedication() {
        stubFor(get(urlPathEqualTo(CODEINE_PHOSPHATE_MEDICATION_URL))
                .willReturn(getOkResponse(PATH_CODEINE_PHOSPHATE_MEDICATION)))
    }
}
