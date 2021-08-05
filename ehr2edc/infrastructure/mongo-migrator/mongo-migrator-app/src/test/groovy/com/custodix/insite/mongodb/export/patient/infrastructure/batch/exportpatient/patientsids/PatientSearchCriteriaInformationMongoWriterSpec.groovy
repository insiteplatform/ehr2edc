package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patientsids

import com.custodix.insite.mongodb.export.patient.domain.model.patientsearchcriteriainformation.PatientSearchCriteriaInformation
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patientsearchcriteriainformation.PatientSearchCriteriaInformationMongoWriter
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.EmbeddedMongoConfig
import com.custodix.insite.mongodb.export.patient.main.MigratorMongoDBConfiguration
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@ContextConfiguration(classes = [EmbeddedMongoConfig, MigratorMongoDBConfiguration, EmbeddedMongoAutoConfiguration])
@TestPropertySource(locations = "classpath:ehr2edc-infra-mongo-migrator.properties")
class PatientSearchCriteriaInformationMongoWriterSpec extends Specification {
    private static final PATIENT_ID = "abc-123"
    private static final NAMESPACE = "MASTER_PATIENT_INDEX"

    @Autowired
    MongoTemplate mongoTemplate

    PatientSearchCriteriaInformationMongoWriter writer

    def setup() {
        writer = new PatientSearchCriteriaInformationMongoWriter(mongoTemplate)
    }

    def "A patient identification is written as a Mongo document with expected format"() {
        given: "A patient identification"
        PatientSearchCriteriaInformation patientIdentification = aPatientIdentification()

        when: "The patient identification is written to Mongo"
        writer.write([patientIdentification])

        then: "A Mongo document with expected format is persisted"
        def documents = mongoTemplate.findAll(PatientIdDocument, "PatientId")
        documents.size() == 1
        with(documents.get(0)) {
            source == NAMESPACE
            identifier == PATIENT_ID
        }
    }

    def "A patient identification is not written as a new Mongo document when a document for that identifier already exists"() {
        given: "A patient identification"
        PatientSearchCriteriaInformation patientIdentification = aPatientIdentification()

        and: "The patient identification has a corresponding document in Mongo"
        writer.write([patientIdentification])

        when: "The patient identification is written to Mongo"
        writer.write([patientIdentification])

        then: "There is still only one document for the patient identification"
        def documents = mongoTemplate.findAll(PatientIdDocument, "PatientId")
        documents.size() == 1
        with(documents.get(0)) {
            source == NAMESPACE
            identifier == PATIENT_ID
        }
    }

    static final class PatientIdDocument {
        public String source
        public String identifier
    }

    private aPatientIdentification() {
        PatientId patientId = PatientId.of(PATIENT_ID)
        Namespace namespace = Namespace.of(NAMESPACE)
        PatientIdentifier patientIdentifier = PatientIdentifier.newBuilder()
                .withPatientId(patientId)
                .withNamespace(namespace)
                .build()
        PatientSearchCriteriaInformation.newBuilder().withPatientIdentifier(patientIdentifier).build()
    }
}
