package com.custodix.insite.local.ehr2edc.query.fhir

import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration
import ca.uhn.fhir.rest.api.Constants
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.OrdinalCriterion.Ordinal
import com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.MedicationQueryDsl
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.Medications
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

import static ca.uhn.fhir.model.dstu2.resource.MedicationAdministration.INCLUDE_MEDICATION
import static com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.QueryDsl.medicationQueryDsl
import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId
import static com.github.tomakehurst.wiremock.client.WireMock.*

class FhirEHRGatewayFactoryForMedicationAdministrationSpec extends AbstractMedicationFhirEHRGatewayWithWiremockSpec {


    private static final String SECOND_BUNDLE_OF_10_MEDICATION_ADMINISTRATIONS_URL =
            "/baseDstu2?_getpages=f8739316-c1ed-44da-b4e7-f2e05a0b9283&_getpagesoffset=10&_count=10&_format=json&_pretty=true&_bundletype=searchset"
    private static final String LAST_BUNDLE_OF_8_MEDICATION_ADMINISTRATIONS_URL =
            "/baseDstu2?_getpages=f8739316-c1ed-44da-b4e7-f2e05a0b9283&_getpagesoffset=20&_count=10&_format=json&_pretty=true&_bundletype=searchset"
    private static final SubjectId SUSANNE_SUBJECT_ID = aRandomSubjectId()
    private static final String URL_MEDICATION_ADMINISTRATION_FILTER_BY_PATIENT_SUSANNE_AND_EFFECTIVE_DATE_BETWEEN_2010_01_03_AND_2010_01_08 =
            "/baseDstu2/MedicationAdministration?" +
                    "patient=134&" +
                    "effectivetime=ge2010-01-03T00%3A00%3A00%2B01%3A00&" +
                    "effectivetime=lt2010-01-08T00%3A00%3A00%2B01%3A00&" +
                    "_include=MedicationAdministration%3Amedication"

    @Autowired
    private FhirEHRGatewayFactory fhirEHRGatewayFactory

    private PatientCDWReference susannePatient

    def setup() {
        susannePatient = patientSusanne()
        zeroMedicationDispenseForSusanne()
        zeroMedicationOrderForSusanne()
        zeroMedicationStatementForSusanne()
    }

    def "Get medication administration from patient without medication administrated from fhir"() {
        given: "A patient suzanne with zero medication administration"
        zeroMedicationAdministrationForSusanne()

        when: "I query the medication administration for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 0 medication administration"
        medications.getValues().size() == 0
    }

    def "Get medication administration from patient without medication dosage"() {
        given: "A patient suzanne with medication administration without medication dosage"
        stubMedicationAdministration("without_dosage")

        when: "I query the medication administration for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication administration"
        medications.getValues().size() == 1
    }

    def "Get medication administration from patient without medication dosage quantity"() {
        given: "A patient suzanne with medication administration without medication dosage quantity"
        stubMedicationAdministration("without_dosage_quantity")

        when: "I query the medication administration for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication administration"
        medications.getValues().size() == 1
    }

    def "Get medication for susanne administrated with Trazodone"() {
        given: "A patient suzanne administrated with Trazodone 50 mg Oral Tablet"
        stubMedicationAdministration("trazodone")

        when: "I query the medication administration for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication administration"
        medications.getValues().size() == 1
        and: "medication is Trazodone 50 MG Oral Tablet and administrated Susanne"
        def medication = medications.getValues().get(0)
        medication.medicationConcept.concept.code == TRAZODONE_CODE
        medication.medicationConcept.name == TRAZODONE_NAME
        medication.doseForm == null
        medication.subjectId == SUSANNE_SUBJECT_ID
        medication.getAdministrationRoute() == ORAL_ROUTE_SNOMED_CODE
        medication.startDate == LocalDateTime.parse("2015-01-15T14:30:00")
        medication.endDate == LocalDateTime.parse("2016-01-15T14:30:00")
        medication.dosage.value == BigDecimal.valueOf(50)
        medication.dosage.unit == "mg"
        medication.dosingFrequency == null
    }

    def "Get medication for susanne administrated with Codeine phosphate"() {
        given: "A patient suzanne administrated with Codeine phosphate 12 mg Oral Tablet"
        codeinePhosphateMedication()
        stubMedicationAdministration("codeine_phosphate_with_medication_reference")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication administration"
        medications.getValues().size() == 1
        and: "medication Codeine phosphate 12 mg Oral Tablet and administrated Susanne"
        def medication = medications.getValues().get(0)
        medication.medicationConcept.concept.code == CODEINE_PHOSPHATE_CODE
        medication.medicationConcept.name == CODEINE_PHOSPHATE_NAME
        medication.doseForm == ORAL_DOSE_FORM_CODE
        medication.subjectId == SUSANNE_SUBJECT_ID
        medication.getAdministrationRoute() == ORAL_ROUTE_SNOMED_CODE
        medication.startDate == LocalDateTime.parse("2016-01-15T15:30:00")
        medication.endDate == LocalDateTime.parse("2018-01-15T18:30:00")
        medication.dosage.value == BigDecimal.valueOf(12)
        medication.dosage.unit == "mg"
        medication.dosingFrequency == null
    }

    def "Get medication for susanne administrated with aspirin defined in a reference 'contained' medication"() {
        given: "A patient suzanne administrated with aspirin defined in a reference 'contained' medication"
        stubMedicationAdministration("with_medication_contained")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication of aspirin"
        medications.getValues().size() == 1
        medications.getValues().get(0).medicationConcept.concept.code == ASPIRINE_CODE
        medications.getValues().get(0).medicationConcept.name == ASPIRIN_NAME
    }

    def "Get all medication administration for susanne"() {
        given: "A patient suzanne administrated with 28 medication administration"
        stubMedicationAdministration("with_first_bundle_of_10_medication_administrations")
        stubNextMedicationAdministrationUrl(SECOND_BUNDLE_OF_10_MEDICATION_ADMINISTRATIONS_URL,
                "with_second_bundle_of_10_medication_administrations")
        stubNextMedicationAdministrationUrl(LAST_BUNDLE_OF_8_MEDICATION_ADMINISTRATIONS_URL,
                "with_last_bundle_of_8_medication_administrations")

        when: "I query the medication administration for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 28 medication administrations for Susanne"
        medications.getValues().size() == 28
    }

    @Unroll
    def "Get #ordinal medication administration for susanne"(Ordinal ordinal, String sortParam, String sortValue, String countParam, String countValue) {
        given: "A patient suzanne administrated with Trazodone 50 mg Oral Tablet"
        stubMedicationAdministration("trazodone")
        and: "A query for the #ordinal medication administration of susanne"
        Query<Medications> query = aMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
                .forOrdinal(ordinal)
                .getQuery()

        when: "I execute the query on the fhir gateway"
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        gateway.execute(query, REFERENCE_DATE)

        then: "The fhir endpoint is called correctly"
        verify(getRequestedFor(urlPathEqualTo(MEDICATION_ADMINISTRATION_URL))
                .withQueryParam(MedicationAdministration.SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam(URLEncoder.encode(sortParam, "UTF-8"), equalTo(sortValue))
                .withQueryParam(countParam, equalTo(countValue)))

        where:
        ordinal       || sortParam                 | sortValue                                 | countParam            | countValue
        Ordinal.LAST  || Constants.PARAM_SORT_DESC | MedicationAdministration.SP_EFFECTIVETIME | Constants.PARAM_COUNT | "1"
        Ordinal.FIRST || Constants.PARAM_SORT_ASC  | MedicationAdministration.SP_EFFECTIVETIME | Constants.PARAM_COUNT | "1"
    }


    @Unroll("Get medication dated #period before #referenceDate")
    def "Get administrated medication based on freshness criterion"(LocalDate referenceDate, Period period, int expectedAmount) {
        given: "A subject with a medication administration that matches the reference date"
        aMedicationAdministration()

        when: "I query for administrated medications fits within #period before #referenceDate"
        def query = medicationQueryDsl()
                .forSubject(SUSANNE_SUBJECT_ID, susannePatient)
                .freshFor(period)
                .getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, referenceDate)

        then: "I expect #expectedAmount medication administration(s) to be found"
        medications.getValues().size() == expectedAmount

        where:
        referenceDate       | period            | expectedAmount
        dateOf(2010, 1, 7)  | Period.ofDays(5)  | 1
    }

    private static LocalDate dateOf(Integer year, Integer month, Integer day) {
        LocalDate.of(year, month, day)
    }

    private void aMedicationAdministration() {
        stubFor(get(urlEqualTo(URL_MEDICATION_ADMINISTRATION_FILTER_BY_PATIENT_SUSANNE_AND_EFFECTIVE_DATE_BETWEEN_2010_01_03_AND_2010_01_08))
                .withQueryParam(MedicationAdministration.SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam(INCLUDE_PARAM_NAME, equalTo(INCLUDE_MEDICATION.getValue()))
                .willReturn(getOkResponse(
                        PATIENT_SUSANNE_RESOURCE_ID, PATH_TRAZODONE_MEDICATION_ADMINISTRATION_TEMPLATE)))
    }

    private static MedicationQuery createMedicationQuery(SubjectId subjectId, PatientCDWReference patientId) {
        medicationQueryDsl().forSubject(subjectId, patientId).getQuery()
    }

    private static MedicationQueryDsl aMedicationQuery(SubjectId subjectId, PatientCDWReference patientId) {
        medicationQueryDsl().forSubject(subjectId, patientId)
    }

    private void stubMedicationAdministration(String fileName) {
        stubFor(get(urlPathEqualTo(MEDICATION_ADMINISTRATION_URL))
                .withQueryParam(MedicationAdministration.SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam(INCLUDE_PARAM_NAME, equalTo(INCLUDE_MEDICATION.getValue()))
                .willReturn(getOkResponse(
                        PATIENT_SUSANNE_RESOURCE_ID, "/samples/medication_administration/search/template_${fileName}.json")))

    }

    private void stubNextMedicationAdministrationUrl(String medication_administration_url, String fileName) {
        stubFor(get(urlEqualTo(medication_administration_url))
                .willReturn(getOkResponse(
                        PATIENT_SUSANNE_RESOURCE_ID, "/samples/medication_administration/search/template_${fileName}.json")))
    }
}
