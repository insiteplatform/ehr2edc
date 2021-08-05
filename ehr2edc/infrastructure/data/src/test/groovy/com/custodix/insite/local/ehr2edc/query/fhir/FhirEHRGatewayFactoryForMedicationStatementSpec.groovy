package com.custodix.insite.local.ehr2edc.query.fhir


import com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.QueryDsl
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.EventType
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.Medications
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

import static ca.uhn.fhir.model.dstu2.resource.MedicationStatement.*
import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId
import static com.github.tomakehurst.wiremock.client.WireMock.*

class FhirEHRGatewayFactoryForMedicationStatementSpec extends AbstractMedicationFhirEHRGatewayWithWiremockSpec {

    private static final SubjectId SUSANNE_SUBJECT_ID = aRandomSubjectId()
    private PatientCDWReference susannePatient

    def setup() {
        susannePatient = patientSusanne()
        zeroMedicationAdministrationForSusanne()
        zeroMedicationDispenseForSusanne()
        zeroMedicationOrderForSusanne()
    }

    def "Get medication for patient with 0 statement medication "() {
        given: "A patient susanne with 0 medication statement"
        zeroMedicationStatementForSusanne()

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 0 medication"
        medications.getValues().size() == 0
    }

    def "Get medication for patient with 1 statement medication"() {
        given: "A patient susanne with 1 medication statement"
        stubMedicationStatementForSusanne("1_statement")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
    }

    def "Get medication statement by effective time"() {
        given: "A patient suzanne with medication statement with effective date"
        aMedicationForSusanneWithEffectiveDate()
        and: "a query reference date"
        LocalDate referenceDate = LocalDate.parse("2020-01-20")

        when: "I query the medication for 'Susanne' and by reference date '2020-01-20'"
        MedicationQuery query = QueryDsl.medicationQueryDsl()
                .forSubject(SUSANNE_SUBJECT_ID, susannePatient)
                .freshFor(Period.ofDays(10)).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, referenceDate)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
    }

    def "Get medication for patient with statement without dosage"() {
        given: "A patient susanne with medication statement without dosage "
        stubMedicationStatementForSusanne("without_dosage")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 0 medication"
        medications.getValues().size() == 0
    }

    def "Get medication for patient with statement without dosage quantity"() {
        given: "A patient susanne with medication statement without dosage quantity"
        stubMedicationStatementForSusanne("without_dosage_quantity")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
    }

    def "Get medication for patient with statement with dosage"() {
        given: "A patient susanne with medication statement with dosage 50 mg"
        stubMedicationStatementForSusanne("with_dosage")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "the medication dosage is 50 mg"
        medications.getValues().get(0).getDosage().getValue() == BigDecimal.valueOf(50)
        medications.getValues().get(0).getDosage().getUnit() == "mg"
    }

    def "Get medication for patient with statement with medication code" () {
        given: "A patient susanne with medication statement for medication Trazodone"
        stubMedicationStatementForSusanne("with_trazodone_code")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication of Trazodone"
        medications.getValues().size() == 1
        medications.getValues().get(0).medicationConcept.concept.code == TRAZODONE_CODE
        medications.getValues().get(0).medicationConcept.name == TRAZODONE_NAME
    }

    def "Get unknown medication for patient with statement " () {
        given: "A patient susanne with medication statement for medication Trazodone"
        stubMedicationStatementForSusanne("with_trazodone_code")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication of Trazodone"
        medications.getValues().size() == 1
        medications.getValues().get(0).eventType == EventType.UNKNOWN
    }

    def "Get medication for patient with statement with medication reference" () {
        given: "A patient susanne with medication statement containing reference to medication Codeine Phosphate"
        codeinePhosphateMedication()
        stubMedicationStatementForSusanne("with_medication_reference")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication of Codeine Phosphate"
        medications.getValues().size() == 1
        medications.getValues().get(0).medicationConcept.concept.code == CODEINE_PHOSPHATE_CODE
        medications.getValues().get(0).medicationConcept.name == CODEINE_PHOSPHATE_NAME
    }

    def "Get medication for patient with statement with a reference 'contained' medication" () {
        given: "A patient susanne with medication statement containing a reference 'contained' medication aspirin"
        stubMedicationStatementForSusanne("with_medication_contained")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication of aspirin"
        medications.getValues().size() == 1
        medications.getValues().get(0).medicationConcept.concept.code == ASPIRINE_CODE
        medications.getValues().get(0).medicationConcept.name == ASPIRIN_NAME
    }

    def "Get medication for patient with statement with effective date time"() {
        given: "A patient susanne with medication statement with effective date time of '2019-12-15T14:30:00'"
        stubMedicationStatementForSusanne("with_effective_date_time")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication with start and end date on '2019-12-15T14:30:00'"
        LocalDateTime effectiveDateTime = LocalDateTime.parse("2019-12-15T14:30:00")
        medications.getValues().size() == 1
        medications.getValues().get(0).startDate == effectiveDateTime
        medications.getValues().get(0).endDate == effectiveDateTime
    }

    def "Get medication for patient with statement with effective period"() {
        given: "A patient susanne with medication statement with effective period '2019-11-15T14:30:00' till '2012-12-20T16:30:00' "
        stubMedicationStatementForSusanne("with_effective_period")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication with start date on '2019-11-15T14:30:00' "
        medications.getValues().size() == 1
        medications.getValues().get(0).startDate == LocalDateTime.parse("2019-11-15T14:30:00")
        and: "end date on '2012-12-20T16:30:00'"
        medications.getValues().get(0).endDate == LocalDateTime.parse("2012-12-20T16:30:00")
    }

    def "Get medication for patient with statement with only effective start period"() {
        given: "A patient susanne with medication statement with effective start period '2019-11-15T14:30:00' "
        stubMedicationStatementForSusanne("with_effective_start_period")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication with start date on '2019-11-15T14:30:00' "
        medications.getValues().size() == 1
        medications.getValues().get(0).startDate == LocalDateTime.parse("2019-11-15T14:30:00")
        and: "end date is null"
        medications.getValues().get(0).endDate == null
    }

    def "Get medication for patient with statement with only effective end period"() {
        given: "A patient susanne with medication statement with effective end period '2012-12-20T16:30:00' "
        stubMedicationStatementForSusanne("with_effective_end_period")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication with start date is null "
        medications.getValues().size() == 1
        medications.getValues().get(0).startDate == null
        and: "end date on '2012-12-20T16:30:00'"
        medications.getValues().get(0).endDate == LocalDateTime.parse("2012-12-20T16:30:00")
    }

    def "Get medication with dosing frequency 2 till 3 times every one day or two days (2..3/1..2d)"() {
        given: "A patient susanne with medication statement with frequency 2 till 3 times every one or two days"
        stubMedicationStatementForSusanne("dosage_frequency_2_till_3_times_every_1_or_2_days")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "medication with frequency 2 till 3 times every one day or two days (2..3/1..2d) "
        def medication = medications.getValues().get(0)
        medication.dosingFrequency == "2..3/1..2d"
    }

    def "Get medication with dosing frequency max 3 times every max two days (..3/..2d)"() {
        given: "A patient susanne with medication statement with max frequency 3 times every max 2 days"
        stubMedicationStatementForSusanne("dosage_max_frequency_3_times_every_max_2_days")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "medication with frequency max 3 times every max two days (..3/..2d) "
        def medication = medications.getValues().get(0)
        medication.dosingFrequency == "..3/..2d"
    }

    def "Get medication with dosing frequency 2 times a day"() {
        given: "A patient susanne with medication statement with frequency 2 times every day"
        stubMedicationStatementForSusanne("dosage_frequency_2_times_every_day")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "medication with frequency 2 times a day "
        def medication = medications.getValues().get(0)
        medication.dosingFrequency == "2/1d"
    }

    def "Get medication with empty dosing frequency"() {
        given: "A patient susanne with medication statement without dosage frequency"
        stubMedicationStatementForSusanne("without_dosage_frequency")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "medication should be null "
        def medication = medications.getValues().get(0)
        medication.dosingFrequency == null
    }

    def "Get medication for patient for subject id"() {
        given: "A patient susanne with medication statement"
        stubMedicationStatementForSusanne("1_statement")

        when: "I query the medication for 'Susanne' with subject id ${SUSANNE_SUBJECT_ID}"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication "
        medications.getValues().size() == 1
        and: "subject id is  ${SUSANNE_SUBJECT_ID}"
        def medication = medications.getValues().get(0)
        medication.subjectId == SUSANNE_SUBJECT_ID
    }

    def "Get medication for patient with statement containing dose form"() {
        given: "A patient susanne with medication statement containing dose form 'oral'"
        stubMedicationStatementForSusanne("with_dose_form")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication "
        medications.getValues().size() == 1
        and: "medication dose form is oral"
        def medication = medications.getValues().get(0)
        medication.doseForm == ORAL_DOSE_FORM_CODE
    }

    def "Get medication for patient with statement containing administration route"() {
        given: "A patient susanne with medication statement containing administration route 'oral'"
        stubMedicationStatementForSusanne("with_administration_route")

        when: "I query the medication for 'Susanne'"
        MedicationQuery query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication "
        medications.getValues().size() == 1
        and: "medication administration route is oral"
        def medication = medications.getValues().get(0)
        medication.administrationRoute == ORAL_ROUTE_SNOMED_CODE
    }

    def aMedicationForSusanneWithEffectiveDate() {
        stubFor(get(urlPathEqualTo(MEDICATION_STATEMENT_URL))
                .withQueryParam(SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam(SP_EFFECTIVEDATE, equalTo("ge2020-01-10T00:00:00+01:00"))
                .withQueryParam(SP_EFFECTIVEDATE, equalTo("lt2020-01-21T00:00:00+01:00"))
                .withQueryParam(INCLUDE_PARAM_NAME, equalTo(INCLUDE_MEDICATION.getValue()))
                .willReturn(getOkResponse(
                        PATIENT_SUSANNE_RESOURCE_ID, "/samples/medication_statement/search/template_effective_date.json")))
    }

    private static MedicationQuery createMedicationQuery(SubjectId subjectId, PatientCDWReference patientId) {
        QueryDsl.medicationQueryDsl().forSubject(subjectId, patientId).getQuery()
    }

    private void stubMedicationStatementForSusanne(String fileName) {
        stubFor(get(urlPathEqualTo(MEDICATION_STATEMENT_URL))
                .withQueryParam(SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam(INCLUDE_PARAM_NAME, equalTo(INCLUDE_MEDICATION.getValue()))
                .willReturn(getOkResponse(
                        PATIENT_SUSANNE_RESOURCE_ID, "/samples/medication_statement/search/template_${fileName}.json")))
    }
}
