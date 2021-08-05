package com.custodix.insite.local.ehr2edc.query.executor.medication.projector

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Dosage
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.MedicationConcept
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.medication.projector.MedicationField.*
import static com.custodix.insite.local.ehr2edc.query.executor.medication.projector.MedicationObjectMother.omeprazoleMedicationBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class ProjectMedicationSpec extends Specification {
    @Unroll
    def "Projecting the '#field' field of a medication to its projected value"() {
        given: "An omeprazole prescription"
        Medication medication = aMedication()

        when: "I project for the value from field '#field'"
        Optional<ProjectedValue> result = new ProjectMedication(field).project(Optional.of(medication), aProjectionContext())

        then: "The value '#expectedValue' is projected"
        result.isPresent()
        with(result.get()) {
            it.value == expectedValue
            it.unit == MedicationObjectMother.DOSAGE_UNIT
            it.code == MedicationObjectMother.OMEPRAZOLE_CODE
        }

        where:
        field                || expectedValue
        SUBJECT              || MedicationObjectMother.SUBJECT
        START_DATE           || MedicationObjectMother.START_DATE
        END_DATE             || MedicationObjectMother.END_DATE
        CODE                 || MedicationObjectMother.OMEPRAZOLE_CODE
        NAME                 || MedicationObjectMother.OMEPRAZOLE_NAME
        DOSAGE_VALUE         || MedicationObjectMother.DOSAGE_VALUE
        DOSAGE_UNIT          || MedicationObjectMother.DOSAGE_UNIT
        ADMINISTRATION_ROUTE || MedicationObjectMother.ADMINISTRATION_ROUTE_ORAL
        DOSAGE_FORM          || MedicationObjectMother.DOSE_FORM_CAPSULE
        DOSAGE_FREQUENCY     || MedicationObjectMother.DOSING_FREQUENCY_DAILY
        EVENT_TYPE           || MedicationObjectMother.EVENT_TYPE
    }

    @Unroll
    def "Projecting the '#field' field of a medication to its projected value which is empty"() {
        given: "A medication with an empty value for the projected field '#field'"

        when: "I project for the value from field '#field'"
        Optional<ProjectedValue> result = new ProjectMedication(field).project(Optional.ofNullable(med), aProjectionContext())

        then: "The result is empty"
        !result.isPresent()

        where:
        field                || med
        SUBJECT              || null
        SUBJECT              || omeprazoleMedicationBuilder().withSubjectId(null).build()
        SUBJECT              || omeprazoleMedicationBuilder().withSubjectId(SubjectId.of(null)).build()
        START_DATE           || null
        START_DATE           || omeprazoleMedicationBuilder().withStartDate(null).build()
        END_DATE             || null
        END_DATE             || omeprazoleMedicationBuilder().withEndDate(null).build()
        CODE                 || null
        CODE                 || omeprazoleMedicationBuilder().withConcept(null).build()
        CODE                 || omeprazoleMedicationBuilder().withConcept(MedicationConcept.newBuilder().withConcept(null).build()).build()
        CODE                 || omeprazoleMedicationBuilder().withConcept(MedicationConcept.newBuilder().withConcept(ConceptCode.conceptFor(null)).build()).build()
        DOSAGE_VALUE         || null
        DOSAGE_VALUE         || omeprazoleMedicationBuilder().withDosage(null).build()
        DOSAGE_VALUE         || omeprazoleMedicationBuilder().withDosage(Dosage.newBuilder().withUnit(MedicationObjectMother.DOSAGE_UNIT).withValue(null).build()).build()
        DOSAGE_UNIT          || null
        DOSAGE_UNIT          || omeprazoleMedicationBuilder().withDosage(null).build()
        DOSAGE_UNIT          || omeprazoleMedicationBuilder().withDosage(Dosage.newBuilder().withUnit(null).withValue(MedicationObjectMother.DOSAGE_VALUE).build()).build()
        ADMINISTRATION_ROUTE || null
        ADMINISTRATION_ROUTE || omeprazoleMedicationBuilder().withAdministrationRoute(null).build()
        DOSAGE_FORM          || null
        DOSAGE_FORM          || omeprazoleMedicationBuilder().withDoseForm(null).build()
        DOSAGE_FREQUENCY     || null
        DOSAGE_FREQUENCY     || omeprazoleMedicationBuilder().withDosingFrequency(null).build()
        EVENT_TYPE           || null
        EVENT_TYPE           || omeprazoleMedicationBuilder().withEventType(null).build()
    }

    def "Projecting a medication with empty unit"() {
        given: "A lab value with empty unit"
        Medication med = omeprazoleMedicationBuilder().withDosage(Dosage.newBuilder().withUnit(null).withValue(MedicationObjectMother.DOSAGE_VALUE).build()).build()

        when: "I project for the value from field 'SUBJECT'"
        Optional<ProjectedValue> measurement = new ProjectMedication(SUBJECT).project(Optional.ofNullable(med), aProjectionContext())

        then: "The projected unit is empty"
        measurement.isPresent()
        with(measurement.get()) {
            value == med.subjectId.id
            unit == null
            code == med.concept.code
        }
    }

    @Unroll
    def "Projecting a medication with empty code"() {
        given: "A lab value with empty code"

        when: "I project for the value from field 'SUBJECT'"
        Optional<ProjectedValue> measurement = new ProjectMedication(SUBJECT).project(Optional.ofNullable(med), aProjectionContext())

        then: "The projected code is empty"
        measurement.isPresent()
        with(measurement.get()) {
            value == med.subjectId.id
            unit == med.dosage.unit
            code == null
        }

        where:
        med                                                                                                                                 | _
        omeprazoleMedicationBuilder().withConcept(null).build()                                                                             | _
        omeprazoleMedicationBuilder().withConcept(MedicationConcept.newBuilder().withConcept(null).build()).build()                         | _
        omeprazoleMedicationBuilder().withConcept(MedicationConcept.newBuilder().withConcept(ConceptCode.conceptFor(null)).build()).build() | _
    }

    static aMedication() {
        return omeprazoleMedicationBuilder()
                .build()
    }
}
