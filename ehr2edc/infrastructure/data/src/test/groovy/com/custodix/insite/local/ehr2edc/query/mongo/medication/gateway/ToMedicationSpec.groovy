package com.custodix.insite.local.ehr2edc.query.mongo.medication.gateway

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.EventType
import com.custodix.insite.local.ehr2edc.query.mongo.medication.model.MedicationConceptField
import com.custodix.insite.local.ehr2edc.query.mongo.medication.model.MedicationDocument
import com.custodix.insite.local.ehr2edc.query.mongo.medication.model.MedicationDocumentObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

import java.time.LocalDateTime

import static com.custodix.insite.local.ehr2edc.query.mongo.medication.model.MedicationDocumentObjectMother.*
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

@Title("ToMedication")
class ToMedicationSpec extends Specification {

    private final ToMedication toMedication = new ToMedication()

    @Unroll
    def "Maps subject id and effective date correctly."(String subjectId, String startDate, String endDate) {
        given: "Medication document with subject id '#subjectId'and start date '#startDate' and end date '#endDate' and concept is null."
        def medicationDocument = MedicationDocument.newBuilder()
                .withSubjectId(SubjectId.of(subjectId))
                .withStartDate(LocalDateTime.parse(startDate, ISO_LOCAL_DATE_TIME))
                .withEndDate(LocalDateTime.parse(endDate, ISO_LOCAL_DATE_TIME))
                .withConcept(null)
                .build()

        when: "mapping to medication"
        def medication = toMedication.apply(medicationDocument)

        then: "medication subject id is '#subjectId'"
        medication.subjectId.id == subjectId

        and: "medication start date is '#startDate'"
        ISO_LOCAL_DATE_TIME.format(medication.startDate) == startDate

        and: "medication end date is '#endDate'"
        ISO_LOCAL_DATE_TIME.format(medication.endDate) == endDate

        and: "medication concept is null"
        medication.getMedicationConcept() == null

        where:
        subjectId       | startDate             | endDate               | _
        "MY_SUBJECT_ID" | "2011-12-03T10:15:30" | "2012-12-03T10:15:30" | _
    }

    @Unroll
    def "Maps concept correctly."(String conceptCode, String conceptName) {
        given: "Medication with concept containing conceptCode '#conceptCode' and '#conceptName'"
        def medicationDocument = MedicationDocumentObjectMother.aDefaultMedicationDocument().toBuilder()
                .withConcept(MedicationConceptField.newBuilder()
                        .withConcept(ConceptCode.conceptFor(conceptCode))
                        .withName(conceptName)
                        .build()
                )
                .build()

        when: "mapping to medication"
        def medication = toMedication.apply(medicationDocument)

        then: "concept code is '#conceptCode'"
        medication.concept.code == conceptCode
        and: "concept name is '#conceptName'"
        medication.medicationConcept.name == conceptName

        where:
        conceptCode | conceptName   | _
        "1234-6"    | "conceptName" | _
    }

    @Unroll
    def "Maps modifiers correctly."(String administrationRoute, String doseForm, String dosingFrequency, EventType eventType) {
        given:
        def medicationDocument = MedicationDocumentObjectMother.anOmeprazoleMedicationDocument()

        when: "mapping to medication"
        def medication = toMedication.apply(medicationDocument)

        then: "administration route is '#administrationRoute'"
        medication.administrationRoute == administrationRoute
        and: "dose form is '#doseForm'"
        medication.doseForm == doseForm
        and: "dosing frequency is '#dosingFrequency'"
        medication.dosingFrequency == dosingFrequency
        and: "event type is '#eventType'"
        medication.eventType == eventType

        where:
        administrationRoute                  | doseForm                     | dosingFrequency                   | eventType
        OMEPRAZOLE_ADMINISTRATION_ROUTE_ORAL | OMEPRAZOLE_DOSE_FORM_CAPSULE | OMEPRAZOLE_DOSING_FREQUENCY_DAILY | OMEPRAZOLE_EVENT_TYPE
    }
}
