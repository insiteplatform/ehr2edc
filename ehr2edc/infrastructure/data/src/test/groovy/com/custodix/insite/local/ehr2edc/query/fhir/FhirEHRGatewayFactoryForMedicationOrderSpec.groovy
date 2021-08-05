package com.custodix.insite.local.ehr2edc.query.fhir

import com.custodix.insite.local.ehr2edc.query.executor.common.query.dsl.QueryDsl
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.EventType
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.MedicationQuery
import com.custodix.insite.local.ehr2edc.query.executor.medication.query.Medications
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

import static com.custodix.insite.local.ehr2edc.vocabulary.SubjectIdObjectMother.aRandomSubjectId

class FhirEHRGatewayFactoryForMedicationOrderSpec extends AbstractFhirEHRGatewayWithWiremockSpec {

    def PATIENT_SYSTEM = FhirJson.Identity.aDefaultSystem()

    def "Get a medication order with a medication concept for a patient"() {
        given: "A patient with an order for Amoxicillin 80 mg Oral Tablet"
        def aPatientRef = aRandomPatientCDWReference()
        def aSubjectId = aRandomSubjectId()
        def aFhirPatient = FhirJson.Patient.aRandomPatientWithSystemAndId(aPatientRef.source, aPatientRef.id)
        def aFhirMedicationOrder = FhirJson.MedicationOrder.amoxicillinWithConcept(aFhirPatient.id)
        aSearchForAMedicationOrderReturnsResults(aFhirPatient, [ aFhirMedicationOrder ])

        when: "I query the medication for the patient"
        MedicationQuery query = createMedicationQuery(aSubjectId, aPatientRef)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = (Medications) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "the medication matches"
        def medication = medications.getValues().get(0)
        medication.medicationConcept.concept.code == aFhirMedicationOrder.medicationCodeableConcept.coding[0].code
        medication.medicationConcept.name == aFhirMedicationOrder.medicationCodeableConcept.coding[0].display
        medication.subjectId == aSubjectId
        medication.startDate == LocalDateTime.parse("2013-08-04T00:00:00")
        medication.endDate == LocalDateTime.parse("2013-08-04T00:00:00")
        medication.eventType == EventType.PRESCRIPTION
    }

    def "Get a medication order with medication for a patient"() {
        given: "A patient with an order for Amoxicillin 80 mg Oral Tablet"
        def aPatientRef = aRandomPatientCDWReference()
        def aSubjectId = aRandomSubjectId()
        def aFhirPatient = FhirJson.Patient.aRandomPatientWithSystemAndId(aPatientRef.source, aPatientRef.id)
        def aFhirMedication = FhirJson.Medication.amoxicillin()
        def aFhirMedicationOrder = FhirJson.MedicationOrder.amoxicillinWithMedication(aFhirPatient.id, aFhirMedication.id)
        aSearchForAMedicationOrderReturnsResults(aFhirPatient, [ aFhirMedicationOrder, aFhirMedication ])

        when: "I query the medication for the patient"
        MedicationQuery query = createMedicationQuery(aSubjectId, aPatientRef)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = (Medications) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "the medication matches"
        def medication = medications.getValues().get(0)
        medication.doseForm == aFhirMedication.product.form.coding[0].code
    }

    def "Get a medication order with dosage instruction for a patient"() {
        given: "A patient with an order for Amoxicillin 80 mg Oral Tablet"
        def aPatientRef = aRandomPatientCDWReference()
        def aSubjectId = aRandomSubjectId()
        def aFhirPatient = FhirJson.Patient.aRandomPatientWithSystemAndId(aPatientRef.source, aPatientRef.id)
        def aFhirMedicationOrder = FhirJson.MedicationOrder.amoxicillinWithConcept(aFhirPatient.id)
        aSearchForAMedicationOrderReturnsResults(aFhirPatient, [ aFhirMedicationOrder ])

        when: "I query the medication for the patient"
        MedicationQuery query = createMedicationQuery(aSubjectId, aPatientRef)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = (Medications) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "the medication matches"
        def medication = medications.getValues().get(0)
        medication.dosage.value == BigDecimal.valueOf(aFhirMedicationOrder.dosageInstruction[0].doseQuantity.value)
        medication.dosage.unit == aFhirMedicationOrder.dosageInstruction[0].doseQuantity.unit
    }

    def "Get a medication order with timing events for a patient"() {
        given: "A patient with an order for Amoxicillin 80 mg Oral Tablet"
        def aPatientRef = aRandomPatientCDWReference()
        def aSubjectId = aRandomSubjectId()
        def aFhirPatient = FhirJson.Patient.aRandomPatientWithSystemAndId(aPatientRef.source, aPatientRef.id)
        def aFhirMedicationOrder = FhirJson.MedicationOrder.amoxicillinWithConcept(aFhirPatient.id)
        def aTiming = FhirJson.MedicationOrder.DosageInstruction.Timing.events("2018-12-12T10:06:55.663+00:00",
                "2018-12-13T10:06:55.663+00:00", "2018-12-14T10:06:55.663+00:00", "2018-12-15T10:06:55.663+00:00")
        aFhirMedicationOrder.dosageInstruction = [ FhirJson.MedicationOrder.DosageInstruction.amoxicillinWithTiming(aTiming) ]
        aSearchForAMedicationOrderReturnsResults(aFhirPatient, [ aFhirMedicationOrder ])

        when: "I query the medication for the patient"
        MedicationQuery query = createMedicationQuery(aSubjectId, aPatientRef)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = (Medications) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "the medication matches"
        def medication = medications.getValues().get(0)
        medication.startDate == OffsetDateTime.parse(aTiming.event.first()).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        medication.endDate == OffsetDateTime.parse(aTiming.event.last()).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
    }

    def "Get a medication order with timing repeat for a patient"() {
        given: "A patient with an order for Amoxicillin 80 mg Oral Tablet"
        def aPatientRef = aRandomPatientCDWReference()
        def aSubjectId = aRandomSubjectId()
        def aFhirPatient = FhirJson.Patient.aRandomPatientWithSystemAndId(aPatientRef.source, aPatientRef.id)
        def aFhirMedicationOrder = FhirJson.MedicationOrder.amoxicillinWithConcept(aFhirPatient.id)
        def aTiming = FhirJson.MedicationOrder.DosageInstruction.Timing
                .repeat("2018-12-12T10:06:55.663+00:00", "2018-12-15T10:06:55.663+00:00", 2, 1, "d")
        aFhirMedicationOrder.dosageInstruction = [ FhirJson.MedicationOrder.DosageInstruction.amoxicillinWithTiming(aTiming) ]
        aSearchForAMedicationOrderReturnsResults(aFhirPatient, [ aFhirMedicationOrder ])

        when: "I query the medication for the patient"
        MedicationQuery query = createMedicationQuery(aSubjectId, aPatientRef)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = (Medications) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "the medication matches"
        def medication = medications.getValues().get(0)
        medication.dosingFrequency == "2/1d"
        medication.startDate == OffsetDateTime.parse(aTiming.repeat.boundsPeriod.start).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        medication.endDate == OffsetDateTime.parse(aTiming.repeat.boundsPeriod.end).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
    }

    def "Get a medication order with repeat with only start for a patient"() {
        given: "A patient with an order for Amoxicillin 80 mg Oral Tablet"
        def aPatientRef = aRandomPatientCDWReference()
        def aSubjectId = aRandomSubjectId()
        def aFhirPatient = FhirJson.Patient.aRandomPatientWithSystemAndId(aPatientRef.source, aPatientRef.id)
        def aFhirMedicationOrder = FhirJson.MedicationOrder.amoxicillinWithConcept(aFhirPatient.id)
        def aTiming = FhirJson.MedicationOrder.DosageInstruction.Timing
                .repeat("2018-12-12T10:06:55.663+00:00", 2, 1, "d")
        aFhirMedicationOrder.dosageInstruction = [ FhirJson.MedicationOrder.DosageInstruction.amoxicillinWithTiming(aTiming) ]
        aSearchForAMedicationOrderReturnsResults(aFhirPatient, [ aFhirMedicationOrder ])

        when: "I query the medication for the patient"
        MedicationQuery query = createMedicationQuery(aSubjectId, aPatientRef)
        def gateway = fhirEHRGatewayFactory.create(query, POPULATION_SPECIFICATION)
        Medications medications = (Medications) gateway.execute(query, REFERENCE_DATE)

        then: "I expect exactly 1 medication"
        medications.getValues().size() == 1
        and: "the medication matches"
        def medication = medications.getValues().get(0)
        medication.dosingFrequency == "2/1d"
        medication.startDate == OffsetDateTime.parse(aTiming.repeat.boundsPeriod.start).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        medication.endDate == OffsetDateTime.parse(aTiming.repeat.boundsPeriod.start).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
    }

    private static MedicationQuery createMedicationQuery(SubjectId subjectId, PatientCDWReference patientId) {
        QueryDsl.medicationQueryDsl().forSubject(subjectId, patientId).getQuery()
    }

    def aSearchForAMedicationAdministrationByPatientReturnsNoResults(String fhirPatientId) {
        aSearchReturnsNoResult(resourceUrl(RESOURCE_MEDICATION_ADMINISTRATION),
                [ patient: fhirPatientId, _include: "${RESOURCE_MEDICATION_ADMINISTRATION}:medication" ])
    }

    def aSearchForAMedicationDispenseByPatientReturnsNoResults(String fhirPatientId) {
        aSearchReturnsNoResult(resourceUrl(RESOURCE_MEDICATION_DISPENSE),
                [ patient: fhirPatientId, _include: "${RESOURCE_MEDICATION_DISPENSE}:medication" ])
    }

    def aSearchForAMedicationStatementByPatientReturnsNoResults(String fhirPatientId) {
        aSearchReturnsNoResult(resourceUrl(RESOURCE_MEDICATION_STATEMENT),
                [ patient: fhirPatientId, _include: "${RESOURCE_MEDICATION_STATEMENT}:medication" ])
    }

    def aSearchForAMedicationOrderByPatientReturnsResults(String fhirPatientId, List resources) {
        aSearchReturnsResults(resourceUrl(RESOURCE_MEDICATION_ORDER),
                [ patient: fhirPatientId, _include: "${RESOURCE_MEDICATION_ORDER}:medication" ], resources)
    }

    def aSearchForAMedicationOrderWithMedicationReturnsAResult(FhirJson.Patient fhirPatient,
                                                 FhirJson.Medication fhirMedication,
                                                 FhirJson.MedicationOrder fhirMedicationOrder) {
        aSearchForAMedicationOrderReturnsAResult(fhirPatient, fhirMedicationOrder)
        aSearchForAResourceByIdReturnsResults(fhirMedication)
    }

    def aSearchForAMedicationOrderReturnsResults(FhirJson.Patient fhirPatient,
                                                 List resources) {
        def patientSystem = fhirPatient.identifier[0].system
        def patientId = fhirPatient.identifier[0].value
        aSearchForPatientsByIdOrSystemAndIdentifierReturnsResults(patientSystem, patientId, [ fhirPatient ])
        aSearchForAMedicationAdministrationByPatientReturnsNoResults(fhirPatient.id)
        aSearchForAMedicationDispenseByPatientReturnsNoResults(fhirPatient.id)
        aSearchForAMedicationStatementByPatientReturnsNoResults(fhirPatient.id)
        aSearchForAMedicationOrderByPatientReturnsResults(fhirPatient.id, resources)
    }

    def aRandomPatientCDWReference() {
        return PatientCDWReference.newBuilder()
                .withSource(PATIENT_SYSTEM)
                .withId(UUID.randomUUID().toString())
                .build()
    }
}
