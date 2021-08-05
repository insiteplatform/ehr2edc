package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.observationsummary

import com.custodix.insite.mongodb.export.TestConfiguration
import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import com.custodix.insite.mongodb.export.patient.domain.model.*
import com.custodix.insite.mongodb.export.patient.domain.model.medication.MedicationItem
import com.custodix.insite.mongodb.export.patient.domain.model.medication.MedicationRecord
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.ExportPatientRunnerConfiguration
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.clinicalfinding.ClinicalFindingItemReaderFactory
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.laboratory.LaboratoryItemReaderFactory
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.medication.MedicationItemReaderFactory
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.observationsummary.ObservationSummaryDocument
import com.custodix.insite.mongodb.export.patient.main.MigratorMongoDBConfiguration
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier
import com.custodix.insite.mongodb.vocabulary.SubjectId
import com.mongodb.BasicDBObject
import org.springframework.batch.item.support.ListItemReader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.TestPropertySource
import spock.lang.Shared
import spock.lang.Specification

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset

import static java.time.format.DateTimeFormatter.ofPattern
import static java.util.Arrays.asList
import static java.util.Collections.emptyList
import static org.mockito.ArgumentMatchers.any
import static org.mockito.BDDMockito.given

@SpringBootTest(classes = [
        MigratorMongoDBConfiguration,
        ExportPatientRunnerConfiguration,
        TestConfiguration,
        EmbeddedMongoAutoConfiguration]
        , properties = ["spring.main.allow-bean-definition-overriding=true"])
@TestPropertySource(properties = ["export.patient.maleCodes=M", "export.patient.femaleCodes=F"], locations = "classpath:ehr2edc-infra-mongo-migrator.properties")
class ObservationSummaryPersistenceSpec extends Specification {

    private static final String PATIENT_ID = "7"
    private static final String NAMESPACE = "MASTER_PATIENT_INDEX"
    private static final String SUBJECT_ID = "SUBJ_1"
    private static final PatientIdentifier PATIENT_IDENTIFIER = PatientIdentifier.of(PatientId.of(PATIENT_ID), Namespace.of(NAMESPACE), SubjectId.of(SUBJECT_ID))
    public static final String DATE_COMPARISON_PATTERN = "yyyy-MM-dd"

    @MockBean
    private LaboratoryItemReaderFactory laboratoryItemReaderFactory
    @MockBean
    private MedicationItemReaderFactory medicationItemReaderFactory
    @MockBean
    private ClinicalFindingItemReaderFactory clinicalFindingItemReaderFactory
    @MockBean
    private EventPublisher eventPublisher

    @Autowired
    private MongoTemplate mongoTemplate
    @Autowired
    private ExportPatient exportPatient

    @Shared
    ExportPatient.Request request = ExportPatient.Request.newBuilder()
            .withPatientIdentifier(PATIENT_IDENTIFIER)
            .build()

    void setup() {
        DomainEventPublisher.setPublisher(eventPublisher)
        given(laboratoryItemReaderFactory.buildItemReader(any(PatientIdentifier))).willReturn(new ListItemReader<LaboratoryObservationFactRecord>(emptyList()))
        given(medicationItemReaderFactory.buildItemReader(any(PatientIdentifier))).willReturn(new ListItemReader<MedicationRecord>(emptyList()))
        given(clinicalFindingItemReaderFactory.buildItemReader(any(PatientIdentifier))).willReturn(new ListItemReader<ClinicalFactRecord>(emptyList()))
    }

    def "Exporting a laboratory fact creates a single summary item on the start date"() {
        given: "A patient has laboratory fact for code HB1C that starts at 01-01-2015 and ends at 06-01-2015"
        LaboratoryObservationFactRecord factRecord = LaboratoryObservationFactRecord.newBuilder()
                .withStartDate(timestampFor(2015, 1, 1))
                .withEndDate(timestampFor(2015, 1, 6))
                .withConceptCD("Laboratory:HB1C")
                .build()
        given(laboratoryItemReaderFactory.buildItemReader(PATIENT_IDENTIFIER)).willReturn(
                new ListItemReader<LaboratoryObservationFactRecord>(asList(factRecord)))

        when: "I export the patient"
        exportPatient.export(request)

        then: "A single observation summary for the category 'laboratory' gets persisted at 01-01-2015"
        List<String> savedObservationSummaries = getAllSummaryItemsForCategory("laboratory")
        savedObservationSummaries.size() == 1
        summaryItemDocumentMatches(savedObservationSummaries.get(0), LocalDate.of(2015, 1, 1), 1)
    }

    def "Exporting a medication fact creates a single summary item on the start date"() {
        given: "A patient has medication fact for code 0601012X0BBACAB that starts at 01-01-2015 and ends at 06-01-2015"
        MedicationItem medicationItem = new MedicationItem(MedicationRecord.newBuilder()
                .withStartDate(timestampFor(2015, 1, 1))
                .withEndDate(timestampFor(2015, 1, 6))
                .withConceptCode("medication:0601012X0BBACAB")
                .build())
        given(medicationItemReaderFactory.buildItemReader(PATIENT_IDENTIFIER)).willReturn(
                new ListItemReader<MedicationItem>(asList(medicationItem)))
        when: "I export the patient"
        exportPatient.export(request)

        then: "A single observation summary for the category 'medication' gets persisted at 01-01-2015"
        List<String> savedObservationSummaries = getAllSummaryItemsForCategory("medication")
        savedObservationSummaries.size() == 1
        summaryItemDocumentMatches(savedObservationSummaries.get(0), LocalDate.of(2015, 1, 1), 1)
    }

    def "Exporting a clinical finding fact creates a single summary item on the start date"() {
        given: "A patient has clinical finding fact for code GleasonGrading that has an effective date set to 01-01-2015"
        ClinicalFactRecord factRecord = ClinicalFactRecord.newBuilder()
                .withStartDate(timestampFor(2015, 1, 1))
                .withConceptCode("clinifinding:GleasonGrading")
                .build()
        ClinicalFindingItem findingItem = new ClinicalFindingItem(factRecord)
        given(clinicalFindingItemReaderFactory.buildItemReader(PATIENT_IDENTIFIER)).willReturn(
                new ListItemReader<ClinicalFindingItem>(asList(findingItem)))
        when: "I export the patient"
        exportPatient.export(request)

        then: "A single observation summary for the category 'medication' gets persisted at 01-01-2015"
        List<String> savedObservationSummaries = getAllSummaryItemsForCategory("clinical finding")
        savedObservationSummaries.size() == 1
        summaryItemDocumentMatches(savedObservationSummaries.get(0), LocalDate.of(2015, 1, 1), 1)
    }

    private Timestamp timestampFor(int year, int month, int day) {
        Timestamp.from(LocalDate.of(year, month, day).atStartOfDay(ZoneOffset.UTC).toInstant())
    }

    private List<String> getAllSummaryItemsForCategory(String category) {
        Query query = new Query()
        query.addCriteria(Criteria.where("category").is(category))
        query.addCriteria(Criteria.where("subjectId").is(SUBJECT_ID))
        mongoTemplate.find(query, String, ObservationSummaryDocument.COLLECTION)
    }

    void summaryItemDocumentMatches(String summaryItemDocument, LocalDate expectedDate, Integer expectedAmountOfObservations) {
        def document = BasicDBObject.parse(summaryItemDocument)
        assert compareObservationDate(document, expectedDate)
        assert document.getInt("amountOfObservations") == expectedAmountOfObservations
    }

    private boolean compareObservationDate(BasicDBObject document, LocalDate expectedDate) {
        def documentDateFormatted = new SimpleDateFormat(DATE_COMPARISON_PATTERN).format(document.getDate("date"))
        def expectedDateFormatted = expectedDate.format(ofPattern(DATE_COMPARISON_PATTERN))
        documentDateFormatted == expectedDateFormatted
    }
}