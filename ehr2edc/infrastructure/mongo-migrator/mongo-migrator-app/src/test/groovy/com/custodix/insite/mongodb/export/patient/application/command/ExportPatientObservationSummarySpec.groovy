package com.custodix.insite.mongodb.export.patient.application.command

import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFindingFact
import com.custodix.insite.mongodb.export.patient.domain.model.LaboratoryFact
import com.custodix.insite.mongodb.export.patient.domain.model.SummarizableObservationFact
import com.custodix.insite.mongodb.export.patient.domain.model.medication.MedicationFact
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.observationsummary.ObservationSummaryCounter
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.observationsummary.ObservationSummaryDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.ObservationSummaryDocumentRepository
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import spock.lang.Title
import spock.lang.Unroll

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.stream.Collectors

@Title("Export patient manage observation summary")
@TestPropertySource(properties = [
        "batch.exportpatient.factChunkSize=1",
])
class ExportPatientObservationSummarySpec extends AbstractExportPatientSpec {

    private static final String PATIENT_ID = "7"
    private static final String NAMESPACE = "MASTER_PATIENT_INDEX"
    private static final String SUBJECT_ID = "999-888"

    @Autowired
    private ObservationSummaryDocumentRepository observationSummaryDocumentRepository
    @Autowired
    private ObservationSummaryCounter observationSummaryCounter

    @Autowired
    private ExportPatient exportPatient

    def "Export of the patient should migrate the observation correctly for vital sign"(PatientId patientId,
                                                                                        Namespace namespace,
                                                                                        SubjectId subjectId,
                                                                                        LocalDate observationSummaryDate,
                                                                                        int observationSummaryCount) {
        when: "Exporting patient with subject id #subjectId"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        then: "there is only one observation summary for vital sign"
        List<ObservationSummaryDocument> observationSummaryDocumentList = observationSummaryDocumentRepository.findAll()
        def summaryDocuments = observationSummaryDocumentList.stream()
                .filter { o -> (subjectId == o.getSubjectId()) }
                .filter { o -> (ClinicalFindingFact.CLINICAL_FINDING_CATEGORY == o.getCategory()) }
                .collect(Collectors.toList())
        summaryDocuments.size() == 1
        and: "the observation summary has date #observationSummaryDate, count #observationSummaryCount and subject id #subjectId"
        def observationSummaryDocument = summaryDocuments.get(0)
        observationSummaryDocument.subjectId == subjectId
        observationSummaryDocument.date == observationSummaryDate
        observationSummaryDocument.amountOfObservations == observationSummaryCount

        where:
        patientId                | namespace               | subjectId                || observationSummaryDate        | observationSummaryCount
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) || LocalDate.parse("2016-03-02") | 2
    }

    @Unroll
    def "Export of the patient should migrate the observation correctly for lab value"(PatientId patientId,
                                                                                       Namespace namespace,
                                                                                       SubjectId subjectId,
                                                                                       LocalDate observationSummaryDate,
                                                                                       int observationSummaryCount) {
        when: "Exporting patient with subject id #subjectId"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        then: "7 observation summary for lab value"
        List<ObservationSummaryDocument> observationSummaryDocumentList = observationSummaryDocumentRepository.findAll()
        def summaryDocuments = observationSummaryDocumentList.stream()
                .filter { o -> (subjectId == o.getSubjectId()) }
                .filter { o -> (LaboratoryFact.LABORATORY_CATEGORY == o.getCategory()) }
                .collect(Collectors.toList())
        summaryDocuments.size() == 7
        and: "the observation summary has date #observationSummaryDate, count #observationSummaryCount and subject id #subjectId"
        summaryDocuments.any {
            it.subjectId == subjectId
            it.date == observationSummaryDate
            it.amountOfObservations == observationSummaryCount
        }

        where:
        patientId                | namespace               | subjectId                || observationSummaryDate        | observationSummaryCount
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) || LocalDate.parse("2005-11-09") | 1
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) || LocalDate.parse("2006-05-03") | 1
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) || LocalDate.parse("2006-08-03") | 1
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) || LocalDate.parse("2006-10-19") | 1
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) || LocalDate.parse("2007-04-18") | 1
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) || LocalDate.parse("2007-10-18") | 1
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) || LocalDate.parse("2008-05-09") | 1
    }

    @Unroll
    def "Export of the patient should migrate the observation correctly for medication"(PatientId patientId,
                                                                                        Namespace namespace,
                                                                                        SubjectId subjectId,
                                                                                        LocalDate observationSummaryDate,
                                                                                        int observationSummaryCount) {
        when: "Exporting patient with subject id #subjectId"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        then: "there is only 1 observation summary for medication"
        List<ObservationSummaryDocument> observationSummaryDocumentList = observationSummaryDocumentRepository.findAll()
        def summaryDocuments = observationSummaryDocumentList.findAll {
            it.subjectId == subjectId
            it.category == MedicationFact.MEDICATION_CATEGORY
        }
        summaryDocuments.size() == 1
        and: "the observation summary has date #observationSummaryDate, count #observationSummaryCount and subject id #subjectId"
        summaryDocuments.every {
            it.subjectId == subjectId
            it.date == observationSummaryDate
            it.amountOfObservations == observationSummaryCount
        }

        where:
        patientId                | namespace               | subjectId                || observationSummaryDate        | observationSummaryCount
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) || LocalDate.parse("2016-03-02") | 4
    }

    def "Export of the patient should overwrite, instead of increment, the observation summary count"(PatientId patientId,
                                                                                                      Namespace namespace,
                                                                                                      SubjectId subjectId,
                                                                                                      LocalDate observationSummaryDate) {
        given: "An observation sumary for a patient with subject id #subjectId"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())
        List<ObservationSummaryDocument> initialSummary = observationSummaryDocumentRepository.findAll()

        when: "Exporting patient with subject id #subjectId again with the same observations"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        then: "The observation summary contains all observations"
        List<ObservationSummaryDocument> updatedSummary = observationSummaryDocumentRepository.findAll()
        updatedSummary.size() == initialSummary.size()
        and: "All items are equivalent to the original summary"
        def equivalentItemsInUpdatedSummary = updatedSummary.intersect(initialSummary, observationSummaryEquivalence)
        equivalentItemsInUpdatedSummary.size() == updatedSummary.size()

        where:
        patientId                | namespace               | subjectId                || observationSummaryDate
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) || LocalDate.parse("2016-03-02")
    }

    def "When clearing the observations for a subject, ongoing exports are not impacted"(PatientId patientId,
                                                                                         Namespace namespace,
                                                                                         SubjectId subjectId,
                                                                                         LocalDate observationSummaryDate) {
        given: "A summary count is ongoing for a subject"
        def ongoingSubject = "ONGOING_SUBJECT"
        def ongoingCategory = "laboratory"
        observationSummaryCounter.incrementFor(
                factFor(ongoingSubject, observationSummaryDate, ongoingCategory))

        when: "The export for another subject is run and completed"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        then: "There are no more summary counters for patient with subject id #subjectId"
        observationSummaryCounter.getSummaryItemsDocumentsFor(subjectId.id).empty
        and: "The counter for the ongoing subject is unaffected"
        observationSummaryCounter.getSummaryItemsDocumentsFor(ongoingSubject).any {
            it.date == observationSummaryDate
            it.category == ongoingCategory
            it.subjectId.id == ongoingSubject
        }

        where:
        patientId                | namespace               | subjectId                || observationSummaryDate
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) || LocalDate.parse("2016-03-02")
    }

    def observationSummaryEquivalence = { left, right ->
        left.subjectId == right.subjectId
        left.date == right.date
        left.category == right.category
        left.amountOfObservations == right.amountOfObservations ? 0 : -1
    } as Comparator<ObservationSummaryDocument>

    SummarizableObservationFact factFor(String subjectId, LocalDate observationDate, String category) {
        return new SummarizableObservationFact() {
            @Override
            Instant getObservationInstant() {
                return observationDate.atStartOfDay().toInstant(ZoneOffset.UTC)
            }

            @Override
            String getCategory() {
                return category
            }

            @Override
            String getSubjectIdentifier() {
                return subjectId
            }
        }
    }

}
