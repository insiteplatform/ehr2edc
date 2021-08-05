package com.custodix.insite.local.ehr2edc.query.fhir

import ca.uhn.fhir.model.dstu2.composite.TimingDt
import ca.uhn.fhir.model.dstu2.resource.BundleObjectMother
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration
import ca.uhn.fhir.model.dstu2.resource.MedicationDispense
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query
import com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.QueryDsl
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.Medications
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import spock.lang.Unroll

import java.time.*

import static ca.uhn.fhir.model.dstu2.resource.BundleObjectMother.aDefaultBundle
import static ca.uhn.fhir.model.dstu2.resource.MedicationDispense.INCLUDE_MEDICATION
import static ca.uhn.fhir.model.dstu2.resource.MedicationDispenseObjectMother.aDefaultDosageInstruction
import static ca.uhn.fhir.model.dstu2.resource.MedicationDispenseObjectMother.aDefaultMedicationDispense
import static com.custodix.insite.local.ehr2edc.query.executor.medication.domain.EventType.DISPENSION
import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId
import static com.github.tomakehurst.wiremock.client.WireMock.*
import static java.util.Arrays.asList

class FhirEHRGatewayFactoryForMedicationDispenseSpec extends AbstractMedicationFhirEHRGatewayWithWiremockSpec {

    private static final SubjectId SUSANNE_SUBJECT_ID = aRandomSubjectId()
    private static final String TYLENOL_CODE = "50580-506-02"
    private static final String TYLENOL_NAME = "Tylenol PM"
    public static final String GASTROSTOMY_USE = "127490009"

    private PatientCDWReference susannePatient

    def setup() {
        susannePatient = patientSusanne()
        zeroMedicationAdministrationForSusanne()
        zeroMedicationOrderForSusanne()
        zeroMedicationStatementForSusanne()
    }

    def "Get medication for patient without dispensed medication from fhir"() {
        given: "A patient susanne with zero medication dispensed"
        zeroMedicationDispenseForSusanne()

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 0 medication"
        medications.getValues().size() == 0
    }

    def "Get medication for patient with dispensed medication without dosage instructions"() {
        given: "A patient susanne with medication dispensed without dosage instructions"
        stubMedicationDispense("0_dosage_instruction")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 0 medication"
        medications.getValues().size() == 0
    }

    def "Get medication for patient with dispensed medication without dosage"() {
        given: "A patient susanne with medication dispensed without dosage"
        stubMedicationDispense("without_dosage")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
    }

    def "Get medication for patient with dispensed medication without dosage quantity"() {
        given: "A patient susanne with medication dispensed without dosage quantity"
        stubMedicationDispense("without_dosage_quantity")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
    }

    def "Get medication dispensed for susanne"() {
        given: "A patient suzanne dispensed with Amoxicillin 80 mg Oral Tablet"
        stubMedicationDispense("amoxicillin")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "medication is Trazodone 50 MG Oral Tablet and administrated to Susanne"
        def medication = medications.getValues().get(0)
        medication.medicationConcept.concept.code == AMOXICILLIN_CODE
        medication.medicationConcept.name == AMOXICILLIN_NAME
        medication.doseForm == null
        medication.subjectId == SUSANNE_SUBJECT_ID
        medication.getAdministrationRoute() == ORAL_ROUTE_SNOMED_CODE
        medication.startDate == LocalDateTime.parse("2020-01-01T00:00:00")
        medication.endDate == LocalDateTime.parse("2020-01-01T00:00:00")
        medication.dosage.value == BigDecimal.valueOf(50)
        medication.dosage.unit == "ml"
        medication.dosingFrequency == null
        medication.eventType == DISPENSION
    }

    def "Get medication dispensed with 2 dosage instructions for susanne"() {
        given: "A patient suzanne dispensed with Tylenol PM Oral Tablet with 2 dosage instructions (1 pill and 2 pills)"
        stubMedicationDispense("tylenol_with_2_dosage_instructions")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 2 medications"
        medications.getValues().size() == 2
        and: "medication is Tylenol PM Oral Tablet is administrated with 1 pill to Susanne"
        def medicationWith1Pill = medications.getValues().stream().filter ({ m -> m.dosage.value == BigDecimal.valueOf(1) }).findFirst().get()
        medicationWith1Pill.medicationConcept.concept.code == TYLENOL_CODE
        medicationWith1Pill.medicationConcept.name == TYLENOL_NAME
        medicationWith1Pill.doseForm == null
        medicationWith1Pill.subjectId == SUSANNE_SUBJECT_ID
        medicationWith1Pill.administrationRoute == GASTROSTOMY_USE
        medicationWith1Pill.startDate == LocalDateTime.parse("2020-01-01T00:00:00")
        medicationWith1Pill.endDate == LocalDateTime.parse("2020-01-01T00:00:00")
        medicationWith1Pill.dosage.value == BigDecimal.valueOf(1)
        medicationWith1Pill.dosage.unit == "pill"
        medicationWith1Pill.dosingFrequency == null
        medicationWith1Pill.eventType == DISPENSION

        and: "medication is Tylenol PM Oral Tablet is administrated with 2 pills to Susanne"
        def medicationWith2Pill = medications.getValues().stream().filter ({ m -> m.dosage.value == BigDecimal.valueOf(2) }).findFirst().get()
        medicationWith2Pill.medicationConcept.concept.code == TYLENOL_CODE
        medicationWith2Pill.medicationConcept.name == TYLENOL_NAME
        medicationWith2Pill.doseForm == null
        medicationWith2Pill.subjectId == SUSANNE_SUBJECT_ID
        medicationWith2Pill.getAdministrationRoute() == ORAL_ROUTE_SNOMED_CODE
        medicationWith2Pill.startDate == LocalDateTime.parse("2020-01-01T00:00:00")
        medicationWith2Pill.endDate == LocalDateTime.parse("2020-01-01T00:00:00")
        medicationWith2Pill.dosage.value == BigDecimal.valueOf(2)
        medicationWith2Pill.dosage.unit == "pill"
        medicationWith2Pill.dosingFrequency == null
        medicationWith2Pill.eventType == DISPENSION
    }

    def "Get medication Codeine phosphate dispensed for susanne administrated"() {
        given: "A patient suzanne is dispensed with Codeine phosphate 12 mg Oral Tablet"
        stubMedicationDispense("codeine_phosphate_with_medication_reference")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication dispensed"
        medications.getValues().size() == 1
        and: "medication Codeine phosphate  and dispensed to Susanne"
        def medication = medications.getValues().get(0)
        medication.medicationConcept.concept.code == CODEINE_PHOSPHATE_CODE
        medication.medicationConcept.name == CODEINE_PHOSPHATE_NAME
        medication.doseForm == ORAL_DOSE_FORM_CODE
        medication.subjectId == SUSANNE_SUBJECT_ID
    }

    def "Get medication dispensed for patient with a reference 'contained' medication"() {
        given: "A patient susanne with medication dispensed containing a reference 'contained' medication aspirin"
        stubMedicationDispense("with_medication_contained")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication of aspirin"
        medications.getValues().size() == 1
        medications.getValues().get(0).medicationConcept.concept.code == ASPIRINE_CODE
        medications.getValues().get(0).medicationConcept.name == ASPIRIN_NAME
    }

    def "Medication dosage defined with range value (low and upper), returns the low value"() {
        given: "A patient suzanne dispensed with medication with a range dosage between 3 and 5 pills"
        stubMedicationDispense("dosage_range_between_3_and_5_pills")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication dispensed"
        medications.getValues().size() == 1
        and: "medication with dosage 3 pills dispensed"
        def medication = medications.getValues().get(0)
        medication.dosage.value == BigDecimal.valueOf(3)
        medication.dosage.unit == "pill"
        medication.eventType == DISPENSION
    }

    def "Get medication with dosing frequency 2 till 3 times every one day or two days (2..3/1..2d)"() {
        given: "A patient suzanne dispensed with medication with frequency 2 till 3 times every one or two days"
        stubMedicationDispense("dosage_frequency_2_till_3_times_every_1_or_2_days")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "medication with frequency 2 till 3 times every one day or two days (2..3/1..2d) "
        def medication = medications.getValues().get(0)
        medication.dosingFrequency == "2..3/1..2d"
    }

    def "Get medication with dosing frequency max 3 times every max two days (..3/..2d)"() {
        given: "A patient suzanne dispensed with medication with max frequency 3 times every max 2 days"
        stubMedicationDispense("dosage_max_frequency_3_times_every_max_2_days")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "medication with frequency max 3 times every max two days (..3/..2d) "
        def medication = medications.getValues().get(0)
        medication.dosingFrequency == "..3/..2d"
    }

    def "Get medication with dosing frequency 2 times a day"() {
        given: "A patient suzanne dispensed with medication with frequency 2 times every day"
        stubMedicationDispense("dosage_frequency_2_times_every_day")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "medication with frequency 2 times a day "
        def medication = medications.getValues().get(0)
        medication.dosingFrequency == "2/1d"
    }

    def "Get medication with empty dosing frequency"() {
        given: "A patient suzanne dispensed with medication without dosage frequency"
        stubMedicationDispense("without_dosage_frequency")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "medication should be null "
        def medication = medications.getValues().get(0)
        medication.dosingFrequency == null
    }

    def "Get medication dosage defined with unit value and unit code, returns the unit code"() {
        given: "A patient suzanne dispensed with medication with a dosage with unit value 'ml' and unit code 'mL'"
        stubMedicationDispense("dosage_with_unit_value_and_unit_code")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "medication with dosage unit is unit code 'mL'"
        def medication = medications.getValues().get(0)
        medication.dosage.unit == "mL"
        medication.eventType == DISPENSION
    }

    def "Get dispensed medication with 3 event times"() {
        given: """A patient suzanne dispensed with medication with events on following time 
                '2019-12-01T00:00:00', 
                '2020-12-31T00:00:00', 
                '2019-12-15T00:00:00'"""
        stubMedicationDispense("with_3_event_times")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "medication with start time '2019-12-01T00:00:00' and end time '2019-12-31T00:00:00'"
        def medication = medications.getValues().get(0)
        medication.startDate == LocalDateTime.parse("2019-12-01T00:00:00")
        medication.endDate == LocalDateTime.parse("2020-12-31T00:00:00")
    }

    def "Get dispensed medication with 1 event time"() {
        given: "A patient suzanne dispensed with medication with a event on '2020-01-01T00:00:00'"
        stubMedicationDispense("with_1_event_time")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "medication with start  and end time is '2020-01-01T00:00:00'"
        LocalDateTime expectedEventTime = LocalDateTime.parse("2020-01-01T00:00:00")
        def medication = medications.getValues().get(0)
        medication.startDate == expectedEventTime
        medication.endDate == expectedEventTime
    }

    def "Get dispensed medication with bound period and 0 event time"() {
        given: """A patient suzanne dispensed with medication with 
                  0 events and with 
                  start bound '2019-12-01T00:00:00' and 
                  end bound '2019-12-31T00:00:00'"""
        stubMedicationDispense("with_bound_period")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "medication with start time '2019-12-01T00:00:00' and end time '2019-12-31T00:00:00'"
        def medication = medications.getValues().get(0)
        medication.startDate == LocalDateTime.parse("2019-12-01T00:00:00")
        medication.endDate == LocalDateTime.parse("2019-12-31T00:00:00")
    }

    def "Get dispensed medication with handed over time and 0 event time"() {
        given: "A patient suzanne dispensed with medication with hand over time '2020-01-01T00:00:00' and 0 event time"
        LocalDateTime handOverTime = LocalDateTime.parse("2020-01-01T00:00:00")
        stubMedicationDispense("with_handed_over_time")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "medication with start time '#handOverTime' and end time '#handOverTime'"
        def medication = medications.getValues().get(0)
        medication.startDate == handOverTime
        medication.endDate == handOverTime
    }

    def "Get dispensed medication with only prepared time"() {
        given: "A patient suzanne dispensed with medication with prepared time '2019-12-27T00:00:00' "
        LocalDateTime preparedTime = LocalDateTime.parse("2019-12-27T00:00:00")
        stubMedicationDispense("with_prepared_time")

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = createMedicationQuery(SUSANNE_SUBJECT_ID, susannePatient)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "medication with start time '#preparedTime' and end time '#preparedTime'"
        def medication = medications.getValues().get(0)
        medication.startDate == preparedTime
        medication.endDate == preparedTime
    }

    def "Get duplicate dispensed medication are filter out"() {
        LocalDate referenceDate = LocalDate.parse("2020-01-20")
        String medicationDispenseId = "12345"
        given: "A patient suzanne dispensed with medication with medication dispense id '12345' returned by query by handed over time"
        queryByHandedOverTimeReturnsMedicationForSusanneWithMedicationDispenseId(medicationDispenseId)
        and: "A patient suzanne dispensed with medication with medication dispense id '12345' returned by query by prepared time"
        queryByPreparedTimeReturnsMedicationForSusanneWithMedicationDispenseId(medicationDispenseId)

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = QueryDsl.medicationQueryDsl()
                .forSubject(SUSANNE_SUBJECT_ID, susannePatient)
                .freshFor(Period.ofDays(10)).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, referenceDate)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
    }

    def "Get dispensed medication by handed over time and prepared time"() {
        LocalDate referenceDate = LocalDate.parse("2020-01-20")
        given: "A patient suzanne dispensed with medication with medication dispense id '12345' returned by query by handed over time"
        queryByHandedOverTimeReturnsMedicationForSusanneWithMedicationDispenseId("12345")
        and: "A patient suzanne dispensed with medication with medication dispense id '56789' returned by query by prepared time"
        queryByPreparedTimeReturnsMedicationForSusanneWithMedicationDispenseId('56789')

        when: "I query the medication for 'Susanne'"
        Query<Medication> query = QueryDsl.medicationQueryDsl()
                .forSubject(SUSANNE_SUBJECT_ID, susannePatient)
                .freshFor(Period.ofDays(10)).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = gateway.execute(query, referenceDate)

        then: "I expect exactly 2 medication"
        medications.getValues().size() == 2
    }

    @Unroll
    def "Get dispensed medication with start and end date matching freshness period"(Period freshnessPeriod,
                                                                                     LocalDateTime startDate,
                                                                                     LocalDateTime endDate,
                                                                                     Integer expectedAmount) {
        given: "A patient suzanne dispensed with medication with events on following time '#startdate', '#endDate'"
        aMedicationForSusanneWithEventOn(startDate, endDate)

        when: "I query the medication for 'Susanne' with fresh period of #freshnessPeriod. Days and de reference date is 2020-01-15"
        Query<Medication> query = QueryDsl.medicationQueryDsl().forSubject(SUSANNE_SUBJECT_ID, susannePatient).freshFor(freshnessPeriod).getQuery()
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        def referenceDate = LocalDate.parse("2020-01-15")
        Medications medications = gateway.execute(query,referenceDate)

        then: "I expect exactly #expectedAmount medication"
        medications.getValues().size() == expectedAmount

        where:
        freshnessPeriod  | startDate                                  | endDate                                    | expectedAmount
        Period.ofDays(5) | LocalDateTime.parse("2019-12-01T00:00:00") | LocalDateTime.parse("2019-12-31T00:00:00") | 0
        Period.ofDays(5) | LocalDateTime.parse("2019-12-01T00:00:00") | LocalDateTime.parse("2020-01-10T23:59:59") | 0
        Period.ofDays(5) | LocalDateTime.parse("2019-12-01T00:00:00") | LocalDateTime.parse("2020-01-11T00:00:00") | 1
        Period.ofDays(5) | LocalDateTime.parse("2019-12-01T00:00:00") | LocalDateTime.parse("2020-01-11T00:00:01") | 1
        Period.ofDays(5) | LocalDateTime.parse("2020-01-11T00:00:00") | LocalDateTime.parse("2020-01-12T00:00:00") | 1
        Period.ofDays(5) | LocalDateTime.parse("2020-01-11T00:00:00") | LocalDateTime.parse("2020-01-31T00:00:00") | 1
        Period.ofDays(5) | LocalDateTime.parse("2020-01-11T00:00:01") | LocalDateTime.parse("2020-01-31T00:00:00") | 1
        Period.ofDays(5) | LocalDateTime.parse("2020-01-12T00:00:00") | LocalDateTime.parse("2020-01-12T00:00:00") | 1
        Period.ofDays(5) | LocalDateTime.parse("2020-01-12T00:00:00") | LocalDateTime.parse("2020-01-13T00:00:00") | 1
        Period.ofDays(5) | LocalDateTime.parse("2020-01-12T00:00:00") | LocalDateTime.parse("2020-01-31T00:00:00") | 1
        Period.ofDays(5) | LocalDateTime.parse("2020-01-15T23:59:59") | LocalDateTime.parse("2020-01-31T00:00:00") | 1
        Period.ofDays(5) | LocalDateTime.parse("2020-01-16T00:00:00") | LocalDateTime.parse("2020-01-31T00:00:00") | 0
        Period.ofDays(5) | LocalDateTime.parse("2020-01-01T00:00:00") | LocalDateTime.parse("2020-01-31T00:00:00") | 1
        Period.ofDays(0) | LocalDateTime.parse("2020-01-01T00:00:00") | LocalDateTime.parse("2020-01-31T00:00:00") | 1
    }

    private static MedicationQuery createMedicationQuery(SubjectId subjectId, PatientCDWReference patientId) {
        QueryDsl.medicationQueryDsl().forSubject(subjectId, patientId).freshFor(Period.ofDays(10)).getQuery()
    }

    private void stubMedicationDispense(String fileName) {
        stubFor(get(urlPathEqualTo(MEDICATION_DISPENSE_URL))
                .withQueryParam(MedicationAdministration.SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam(INCLUDE_PARAM_NAME, equalTo(INCLUDE_MEDICATION.getValue()))
                .willReturn(getOkResponse(
                        PATIENT_SUSANNE_RESOURCE_ID, "/samples/medication_dispense/search/template_${fileName}.json")))
    }

    private medicationDispenseForSusanne(MedicationDispense.DosageInstruction aDosageInstruction) {
        def medicationDispense = aDefaultMedicationDispense()
        medicationDispense.setDosageInstruction(asList(aDosageInstruction))
        medicationDispense.setWhenHandedOverWithSecondsPrecision(Date.from(ZonedDateTime.parse("2020-01-01T00:00:00+01:00").toInstant()))
        medicationDispenseForSusanne(medicationDispense)
    }

    private medicationDispenseForSusanne(MedicationDispense medicationDispense) {
        def entry = BundleObjectMother.aDefaultBundleEntry()
        entry.setResource(medicationDispense)
        def bundle = aDefaultBundle()
        bundle.setEntry(asList(entry))

        stubFor(get(urlPathEqualTo(MEDICATION_DISPENSE_URL))
                .withQueryParam(MedicationDispense.SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam(INCLUDE_PARAM_NAME, equalTo(INCLUDE_MEDICATION.getValue()))
                .willReturn(getOkResponse(bundle)))
    }

    def aMedicationForSusanneWithEventOn(LocalDateTime... eventTimes) {
        def aDosageInstruction = aDefaultDosageInstruction()
        aDosageInstruction.setTiming(createTimingWithEvents(eventTimes))
        medicationDispenseForSusanne(aDosageInstruction)
    }

    def createTimingWithEvents( LocalDateTime... eventTimes) {
        TimingDt timing = new TimingDt()
        Arrays.stream(eventTimes).forEach {
            eventTime -> timing.addEvent(Date.from(eventTime.atZone(ZoneId.systemDefault()).toInstant()))
        }
        timing
    }

    def queryByHandedOverTimeReturnsMedicationForSusanneWithMedicationDispenseId(String resourceId) {
        def medicationDispense = aDefaultMedicationDispense()
        medicationDispense.setId(resourceId);
        medicationDispense.setWhenHandedOverWithSecondsPrecision(Date.from(ZonedDateTime.parse("2020-01-15T00:00:00+01:00").toInstant()))
        medicationDispense.setWhenHandedOverWithSecondsPrecision(Date.from(ZonedDateTime.parse("2020-01-15T00:00:00+01:00").toInstant()))

        def entry = BundleObjectMother.aDefaultBundleEntry()
        entry.setResource(medicationDispense)
        def bundle = aDefaultBundle()
        bundle.setEntry(asList(entry))

        stubFor(get(urlPathEqualTo(MEDICATION_DISPENSE_URL))
                .withQueryParam(MedicationDispense.SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam(MedicationDispense.SP_WHENHANDEDOVER, equalTo("ge2020-01-10T00:00:00+01:00"))
                .withQueryParam(MedicationDispense.SP_WHENHANDEDOVER, equalTo("lt2020-01-21T00:00:00+01:00"))
                .withQueryParam(INCLUDE_PARAM_NAME, equalTo(INCLUDE_MEDICATION.getValue()))
                .willReturn(getOkResponse(bundle)))
    }

    def queryByPreparedTimeReturnsMedicationForSusanneWithMedicationDispenseId(String resourceId) {
        def medicationDispense = aDefaultMedicationDispense()
        medicationDispense.setId(resourceId);
        medicationDispense.setWhenHandedOverWithSecondsPrecision(Date.from(ZonedDateTime.parse("2020-01-15T00:00:00+01:00").toInstant()))
        medicationDispense.setWhenHandedOverWithSecondsPrecision(Date.from(ZonedDateTime.parse("2020-01-15T00:00:00+01:00").toInstant()))

        def entry = BundleObjectMother.aDefaultBundleEntry()
        entry.setResource(medicationDispense)
        def bundle = aDefaultBundle()
        bundle.setEntry(asList(entry))

        stubFor(get(urlPathEqualTo(MEDICATION_DISPENSE_URL))
                .withQueryParam(MedicationDispense.SP_PATIENT, equalTo(PATIENT_SUSANNE_RESOURCE_ID))
                .withQueryParam(MedicationDispense.SP_WHENPREPARED, equalTo("ge2020-01-10T00:00:00+01:00"))
                .withQueryParam(MedicationDispense.SP_WHENPREPARED, equalTo("lt2020-01-21T00:00:00+01:00"))
                .withQueryParam(INCLUDE_PARAM_NAME, equalTo(INCLUDE_MEDICATION.getValue()))
                .willReturn(getOkResponse(bundle)))

    }
}
