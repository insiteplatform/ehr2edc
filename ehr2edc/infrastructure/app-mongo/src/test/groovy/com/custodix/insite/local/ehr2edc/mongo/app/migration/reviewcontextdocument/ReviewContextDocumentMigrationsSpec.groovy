package com.custodix.insite.local.ehr2edc.mongo.app.migration.reviewcontextdocument

import com.custodix.insite.local.ehr2edc.SpecConfiguration
import com.custodix.insite.local.ehr2edc.mongo.app.migration.DocumentJsonComparator
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
class ReviewContextDocumentMigrationsSpec extends Specification {
    private static final String GENERATED_UUID_FIELD_PATH = "reviewedEvent.reviewedForms[*].reviewedItemGroups[*].reviewedItems[*].instanceId"
    private static final String UNMIGRATED_DOCUMENT_ID = "3e8b6a10-3f29-4b87-922f-5b5207adfa5f"
    private static final String MIGRATED_DOCUMENT_ID = "aac8f848-2825-4cdf-a0bc-03a914d92321"
    private static final DocumentJsonComparator COMPARATOR = createComparator()

    @Autowired
    @Qualifier("ehr2edcMongoAppMongoTemplate")
    private MongoTemplate mongoTemplate

    def "All ReviewContextDocument migrations are applied"() {
        expect: "All migrations to be applied to an unmigrated document"
        def reviewedItemDocument = mongoTemplate.getCollection("reviewContextMongoSnapshot_v2")
                .find(eq("_id", UNMIGRATED_DOCUMENT_ID)).first()
        JSONAssert.assertEquals(readResultFile("after"), reviewedItemDocument.toJson(), COMPARATOR)
    }

    def "A document that is already fully migrated remains unchanged"() {
        expect: "A migrated Document to remain unchanged"
        def reviewedItemDocument = mongoTemplate.getCollection("reviewContextMongoSnapshot_v2")
                .find(eq("_id", MIGRATED_DOCUMENT_ID)).first()
        JSONAssert.assertEquals(readResultFile("init/migrated"), reviewedItemDocument.toJson(), JSONCompareMode.STRICT)
    }

    private static createComparator() {
        DocumentJsonComparator.newBuilder()
                .withGeneratedUUID(GENERATED_UUID_FIELD_PATH)
                .build()
    }

    private static readResultFile(String filename) {
        new String(
                FileCopyUtils.copyToByteArray(new ClassPathResource("migrations/reviewcontextdocument/" + filename + ".json").getFile()))
    }
}
