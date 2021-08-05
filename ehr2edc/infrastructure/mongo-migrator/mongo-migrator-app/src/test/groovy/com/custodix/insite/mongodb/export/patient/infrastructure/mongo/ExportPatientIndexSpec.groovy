package com.custodix.insite.mongodb.export.patient.infrastructure.mongo

import com.custodix.insite.mongodb.export.TestConfiguration
import com.custodix.insite.mongodb.export.patient.application.command.AbstractExportPatientSpec
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.ExportPatientRunnerConfiguration
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patientsearchcriteriainformation.PatientSearchCriteriaInformationMongoWriter
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.demographic.DemographicDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue.LabValueDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.medication.MedicationDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.vitalsign.VitalSignDocument
import com.custodix.insite.mongodb.export.patient.main.MigratorMongoDBConfiguration
import com.custodix.insite.mongodb.export.patient.main.SubjectMigrationConfiguration
import com.mongodb.client.MongoCollection
import groovy.json.JsonSlurper
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate

@SpringBootTest(classes = [
        MigratorMongoDBConfiguration,
        ExportPatientRunnerConfiguration,
        SubjectMigrationConfiguration,
        TestConfiguration,
        EmbeddedMongoAutoConfiguration]
        , properties = ["spring.main.allow-bean-definition-overriding=true"])
class ExportPatientIndexSpec extends AbstractExportPatientSpec {

    @Autowired
    MongoTemplate mongoTemplate

    def "Has unique compound index for PatientId-collection"() {
        given: "The PatientId-collection"
        MongoCollection<Document> collection = mongoTemplate.getCollection(PatientSearchCriteriaInformationMongoWriter.COLLECTION_NAME)

        when: "Retrieving the indices"
        List<String> jsons = []
        for (Document doc : collection.listIndexes()) {
            jsons.add(doc.toJson())
        }

        then: "Indices should be present"
        jsons.size == 3
        jsons.each {
            defaultIndex(it) || assertPatientIdIndex(it) || assertPatientSearchCriteriaInformationIndex(it)
        }
    }

    def "Has subjectId index for Demographic-collection"() {
        given: "The PatientId-collection"
        MongoCollection<Document> collection = mongoTemplate.getCollection(DemographicDocument.COLLECTION)

        when: "Retrieving the indices"
        List<String> jsons = []
        for (Document doc : collection.listIndexes()) {
            jsons.add(doc.toJson())
        }

        then: "Indices should be present"
        jsons.size == 2
        jsons.each {
            defaultIndex(it) || assertSubjectIdIndex(it)
        }
    }

    def "Has subjectId index for Medication-collection"() {
        given: "The PatientId-collection"
        MongoCollection<Document> collection = mongoTemplate.getCollection(MedicationDocument.COLLECTION)

        when: "Retrieving the indices"
        List<String> jsons = []
        for (Document doc : collection.listIndexes()) {
            jsons.add(doc.toJson())
        }

        then: "Indices should be present"
        jsons.size == 2
        jsons.each {
            defaultIndex(it) || assertSubjectIdIndex(it)
        }
    }

    def "Has subjectId index for VitalSign-collection"() {
        given: "The PatientId-collection"
        MongoCollection<Document> collection = mongoTemplate.getCollection(VitalSignDocument.COLLECTION)

        when: "Retrieving the indices"
        List<String> jsons = []
        for (Document doc : collection.listIndexes()) {
            jsons.add(doc.toJson())
        }

        then: "Indices should be present"
        jsons.size == 2
        jsons.each {
            defaultIndex(it) || assertSubjectIdIndex(it)
        }
    }

    def "Has compound index for LabValue-collection"() {
        given: "The PatientId-collection"
        MongoCollection<Document> collection = mongoTemplate.getCollection(LabValueDocument.COLLECTION)

        when: "Retrieving the indices"
        List<String> jsons = []
        for (Document doc : collection.listIndexes()) {
            jsons.add(doc.toJson())
        }

        then: "Indices should be present"
        jsons.size == 2
        jsons.each {
            defaultIndex(it) || assertLabValueIndex(it)
        }
    }

    private assertPatientIdIndex(String it) {
        def json = new JsonSlurper().parseText(it)
        return json.unique &&
                json.key.size() == 2 &&
                json.key.source == 1 &&
                json.key.identifier == 1
    }

    private assertPatientSearchCriteriaInformationIndex(String it) {
        def json = new JsonSlurper().parseText(it)
        return json.unique &&
                json.key.size() == 3 &&
                json.key.source == 1 &&
                json.key.identifier == 1  &&
                json.key.birthDate == 1
    }

    private assertSubjectIdIndex(String it) {
        def json = new JsonSlurper().parseText(it)
        return json.key.size() == 1 &&
                json.key.subjectId == 1
    }

    private assertLabValueIndex(String it) {
        def json = new JsonSlurper().parseText(it)
        return json.key.size() == 4 &&
                json.key.subjectId == 1 &&
                json.key["labConcept.concept"] == 1 &&
                json.key.startDate == -1 &&
                json.key.endDate == -1
    }

    private defaultIndex(String it) {
        def json = new JsonSlurper().parseText(it)
        return json.unique &&
                json.key.size() == 1 &&
                json.key._id == 1
    }
}
