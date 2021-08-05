package com.custodix.insite.local.ehr2edc.mongo.app.configuration


import com.mongodb.client.MongoCollection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@ContextConfiguration(classes = [EHR2EDCMongoDBAppConfiguration, EmbeddedMongoAutoConfiguration])
@TestPropertySource(locations = "classpath:ehr2edc-infra-app-mongo.properties")
class EHR2EDCMongoDBAppConfigurationSpec extends Specification {
    private static final String TEST_DOCUMENT_COLLECTION = "DocumentCollection"
    private static final String TEST_DOCUMENT_WITH_INTERFACE_FIELD_COLLECTION = "DocumentWithInterfaceFieldTestCollection"

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

    def "When annotating a class with @TypeAlias, a type discriminator field '_type' is included"() {
        given: "An instance of a class that implements an interface and which has been annotated with @TypeAlias"
        def testImplementation = new TestImplementation("test")
        and: "An instance of a class that has the interface as a field"
        TestDocumentWithInterfaceField document = new TestDocumentWithInterfaceField("id", testImplementation)

        when: "I persist the document"
        mongoTemplate.insert(document)

        then: "The persisted json contains the type discriminator '_type' with value from @TypeAlias"
        String json = getDocumentJson(TEST_DOCUMENT_WITH_INTERFACE_FIELD_COLLECTION)
        json == '{ "_id" : "id", "interfaceField" : { "field" : "test", "_type" : "TestDiscriminator" } }'

        and: "The document can be deserialized correctly"
        def query = Query.query(Criteria.where("_id").is("id"))
        def result = mongoTemplate.findOne(query, TestDocumentWithInterfaceField.class, TEST_DOCUMENT_WITH_INTERFACE_FIELD_COLLECTION)
        result.id == "id"
        (result.interfaceField as TestImplementation).field == "test"
    }

    private String getDocumentJson(String collection) {
        mongoTemplate.execute(collection, { MongoCollection<org.bson.Document> col ->
            return col.find().asList().get(0).toJson()
        })
    }

    @Document(value = EHR2EDCMongoDBAppConfigurationSpec.TEST_DOCUMENT_COLLECTION)
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

    @Document(value = EHR2EDCMongoDBAppConfigurationSpec.TEST_DOCUMENT_WITH_INTERFACE_FIELD_COLLECTION)
    static final class TestDocumentWithInterfaceField {
        @Id
        private String id
        private TestInterface interfaceField

        @PersistenceConstructor
        TestDocumentWithInterfaceField(String id, TestInterface interfaceField) {
            this.id = id
            this.interfaceField = interfaceField
        }
    }

    static interface TestInterface {

    }

    @TypeAlias("TestDiscriminator")
    static final class TestImplementation implements TestInterface {
        private final String field

        @PersistenceConstructor
        TestImplementation(String field) {
            this.field = field
        }
    }
}
