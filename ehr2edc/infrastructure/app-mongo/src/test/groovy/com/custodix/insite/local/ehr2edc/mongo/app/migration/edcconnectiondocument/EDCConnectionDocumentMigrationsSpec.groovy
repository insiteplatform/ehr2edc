package com.custodix.insite.local.ehr2edc.mongo.app.migration.edcconnectiondocument

import com.custodix.insite.local.ehr2edc.SpecConfiguration
import com.custodix.insite.local.ehr2edc.mongo.app.document.EDCConnectionDocument
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.util.FileCopyUtils
import spock.lang.Specification

import static com.mongodb.client.model.Filters.eq

@SpringBootTest(classes = SpecConfiguration.class)
class EDCConnectionDocumentMigrationsSpec extends Specification {
    private static final String UNMIGRATED_DOCUMENT_STUDY_ID = "TEST_STUDY"
    private static final String MIGRATED_DOCUMENT_STUDY_ID = "TEST_STUDY_MIGRATED"

    @Autowired
    @Qualifier("ehr2edcMongoAppMongoTemplate")
    private MongoTemplate mongoTemplate

    def "All EDCConnectionDocument migrations are applied"() {
        expect: "All migrations to be applied to an unmigrated document"
        def document = mongoTemplate.getCollection(EDCConnectionDocument.COLLECTION)
                .find(eq("_id.studyId", UNMIGRATED_DOCUMENT_STUDY_ID)).first()
        JSONAssert.assertEquals(readResultFile("after"), document.toJson(), JSONCompareMode.STRICT)
    }

    def "A document that is already fully migrated remains unchanged"() {
        expect: "A migrated Document to remain unchanged"
        def document = mongoTemplate.getCollection(EDCConnectionDocument.COLLECTION)
                .find(eq("_id.studyId", MIGRATED_DOCUMENT_STUDY_ID)).first()
        JSONAssert.assertEquals(readResultFile("init/migrated"), document.toJson(), JSONCompareMode.STRICT)
    }

    private static readResultFile(String filename) {
        new String(
                FileCopyUtils.copyToByteArray(new ClassPathResource("migrations/edcconnectiondocument/" + filename + ".json").getFile()))
    }
}
