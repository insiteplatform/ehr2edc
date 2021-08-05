package com.custodix.insite.mongodb.export.patient.main

import com.mongodb.client.MongoCollection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@ContextConfiguration(classes = [MigratorMongoDBConfiguration, EmbeddedMongoAutoConfiguration])
@TestPropertySource(locations = "classpath:ehr2edc-infra-mongo-migrator.properties")
class MigratorMongoDBConfigurationSpec extends Specification {
    private static final String TEST_DOCUMENT_COLLECTION = "DocumentCollection"

    @Autowired
    private MongoTemplate mongoTemplate

    def "By default a type discriminator field is not included"() {
        given: "A document"
        TestDocument document = new TestDocument("id", "test")

        when: "I persist the document"
        mongoTemplate.insert(document)

        then: "The persisted json does not contain a type discriminator field"
        String json = getDocumentJson(TEST_DOCUMENT_COLLECTION)
        json == '{ "_id" : "id", "field" : "test" }'

        and: "The document can be deserialized correctly"
        def query = Query.query(Criteria.where("_id").is("id"))
        def result = mongoTemplate.findOne(query, TestDocument.class, TEST_DOCUMENT_COLLECTION)
        result.id == "id"
        result.field == "test"
    }

    private String getDocumentJson(String collection) {
        mongoTemplate.execute(collection, { MongoCollection<org.bson.Document> col ->
            return col.find().asList().get(0).toJson()
        })
    }

    @Document(value = MigratorMongoDBConfigurationSpec.TEST_DOCUMENT_COLLECTION)
    static final class TestDocument {
        @Id
        private final String id
        private final String field

        @PersistenceConstructor
        TestDocument(String id, String field) {
            this.id = id
            this.field = field
        }
    }

}
