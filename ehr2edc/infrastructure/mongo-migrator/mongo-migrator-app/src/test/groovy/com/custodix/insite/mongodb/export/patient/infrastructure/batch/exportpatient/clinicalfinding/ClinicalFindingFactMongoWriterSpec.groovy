package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.clinicalfinding

import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFindingFact
import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFindingFactObjectMother
import com.custodix.insite.mongodb.export.patient.domain.model.common.*
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.EmbeddedMongoConfig
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.VitalSignDocumentRepository
import com.custodix.insite.mongodb.export.patient.main.MigratorMongoDBConfiguration
import com.custodix.insite.mongodb.vocabulary.PatientIdentifierObjectMother
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification
import spock.lang.Title

import java.time.LocalDateTime
import java.time.ZoneId

import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.MONGO_TEMPLATE_MONGO_MIGRATOR

@Title("ClinicalFindingFactMongoWriter")
@ContextConfiguration(classes = [EmbeddedMongoConfig, MigratorMongoDBConfiguration, EmbeddedMongoAutoConfiguration])
@EnableMongoRepositories(
        basePackages = "com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository",
        mongoTemplateRef = MONGO_TEMPLATE_MONGO_MIGRATOR)
@TestPropertySource(locations = "classpath:ehr2edc-infra-mongo-migrator.properties")
class ClinicalFindingFactMongoWriterSpec extends Specification {

    public static final String SUBJECT_ID = "123-567"
    public static final String BMI_SNOMED_CODE = "60621009"
    public static final String BMI_UNIT = "kg/m²"
    public static final LocalDateTime YESTERDAY = LocalDateTime.now().minusDays(1)

    @Autowired
    private VitalSignDocumentRepository vitalSignDocumentRepository

    private ClinicalFindingFactMongoWriter clinicalFindingFactMongoWriter

    def setup() {
        clinicalFindingFactMongoWriter = new ClinicalFindingFactMongoWriter(vitalSignDocumentRepository)
    }

    def "As a system, I can store the clinical find fact correctly"(
            String subjectId,
            String referenceConceptCode,
            String referenceLaterality,
            String referenceLocation,
            String referencePosition,
            LocalDateTime effectiveDateTime,
            BigDecimal measurementLowerLimit,
            BigDecimal measurementUpperLimit,
            BigDecimal measurementValue,
            String measurementUnit
    ) {
        given:
        "A clinical  finding fact with " +
                "reference concept code '#referenceConceptCode', " +
                "reference laterality '#referenceLaterality', " +
                "reference location '#referenceLocation', " +
                "reference position  '#referencePosition'" +
                "effective date time '#effectiveDateTime'" +
                "measurement lowe limit '#measurementLowerLimit'" +
                "measurement upper limit '#measurementUpperLimit'" +
                "measurement value '#measurementValue'" +
                "measurement unit '#measurementUnit'"
        ClinicalFindingFact clinicalFindingFact = ClinicalFindingFactObjectMother.aDefaultClinicalFindingFact().toBuilder()
                .withPatientIdentifier(PatientIdentifierObjectMother.aDefaultPatientIdentifier().toBuilder().withSubjectId(SubjectId.of(subjectId)).build())
                .withReferenceConcept(ConceptObjectMother.aDefaultConcept().toBuilder().withCode(referenceConceptCode).build())
                .withLaterality(ModifierObjectMother.laterality())
                .withPosition(ModifierObjectMother.position())
                .withLocation(ModifierObjectMother.location())
                .withEffectiveDate(effectiveDateTime.atZone(ZoneId.systemDefault()).toInstant())
                .withLlnObservation(Observation.newBuilder().withType(ObservationType.NUMERIC).withValues(Collections.singletonList(ObservationValue.newBuilder().withValue(measurementLowerLimit).withUnit(measurementUnit).build())).build())
                .withUlnObservation(Observation.newBuilder().withType(ObservationType.NUMERIC).withValues(Collections.singletonList(ObservationValue.newBuilder().withValue(measurementUpperLimit).withUnit(measurementUnit).build())).build())
                .withValueObservation(Observation.newBuilder().withType(ObservationType.NUMERIC).withValues(Collections.singletonList(ObservationValue.newBuilder().withValue(measurementValue).withUnit(measurementUnit).build())).build())
                .build()

        when: "storing clinical find fact"
        clinicalFindingFactMongoWriter.write([clinicalFindingFact])

        then: "the clinical find fact is stored correctly."
        def vitalSignDocuments = vitalSignDocumentRepository.findAll()
        vitalSignDocuments.size() == 1
        def vitalSignDocument = vitalSignDocuments.get(0)
        vitalSignDocument.subjectId.id == subjectId
        vitalSignDocument.concept.concept.code == referenceConceptCode
        vitalSignDocument.concept.laterality == referenceLaterality
        vitalSignDocument.concept.location == referenceLocation
        vitalSignDocument.concept.position == referencePosition
        vitalSignDocument.effectiveDateTime == effectiveDateTime
        vitalSignDocument.measurement.lowerLimit == measurementLowerLimit
        vitalSignDocument.measurement.upperLimit == measurementUpperLimit
        vitalSignDocument.measurement.value == measurementValue
        vitalSignDocument.measurement.unit == measurementUnit

        where:
        subjectId  | referenceConceptCode | referenceLaterality | referenceLocation | referencePosition | effectiveDateTime | measurementLowerLimit  | measurementUpperLimit  | measurementValue          | measurementUnit | _
        SUBJECT_ID | BMI_SNOMED_CODE      | "7771000"           | "40983000"        | "33586001"        | YESTERDAY         | BigDecimal.valueOf(10) | BigDecimal.valueOf(35) | BigDecimal.valueOf(18.23) | BMI_UNIT        | _
    }
}
