package com.custodix.insite.local.ehr2edc.query.executor.medication.projector

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Dosage
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.EventType
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.MedicationConcept
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId

import java.time.LocalDateTime

import static com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode.conceptFor

class MedicationObjectMother {
    public static final String ADMINISTRATION_ROUTE_ORAL = "26643006"
    public static final String DOSE_FORM_CAPSULE = "420692007"
    public static final String DOSING_FREQUENCY_DAILY = "229797004"
    public static final String SUBJECT = "123456"
    public static final LocalDateTime START_DATE = LocalDateTime.now()
    public static final LocalDateTime END_DATE = LocalDateTime.now().plusDays(1)
    public static final String OMEPRAZOLE_CODE = "A10A"
    public static final String OMEPRAZOLE_NAME = "omeprazole"
    public static final int DOSAGE_VALUE = 10
    public static final String DOSAGE_UNIT = "pillen"
    public static final EventType EVENT_TYPE = EventType.ADMINISTRATION;

    static Medication omeprazole() {
        return omeprazoleMedicationBuilder().build()
    }

    static Medication.Builder omeprazoleMedicationBuilder() {
        Medication.newBuilder()
                .withDosage(Dosage.newBuilder().withValue(DOSAGE_VALUE).withUnit(DOSAGE_UNIT).build())
                .withSubjectId(SubjectId.of(SUBJECT))
                .withStartDate(START_DATE)
                .withEndDate(END_DATE)
                .withConcept(omeprazoleMedicationConceptBuilder().build())
                .withAdministrationRoute(ADMINISTRATION_ROUTE_ORAL)
                .withDoseForm(DOSE_FORM_CAPSULE)
                .withDosingFrequency(DOSING_FREQUENCY_DAILY)
                .withEventType(EVENT_TYPE)
    }

    static MedicationConcept.Builder omeprazoleMedicationConceptBuilder() {
        return MedicationConcept.newBuilder().withConcept(conceptFor(OMEPRAZOLE_CODE)).withName(OMEPRAZOLE_NAME)
    }
}
