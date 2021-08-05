package com.custodix.insite.local.ehr2edc.mongo.app.migration.edcconnectiondocument

import com.custodix.insite.local.ehr2edc.SpecConfiguration
import com.custodix.insite.local.ehr2edc.mongo.app.document.EDCConnectionDocument
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.util.FileCopyUtils
import spock.lang.Specification

import static com.mongodb.client.model.Filters.eq

@SpringBootTest(classes = SpecConfiguration.class)
class AddEDCSystemSpec extends Specification {

    @Autowired
    private MongoClient mongoClient
    private MongoDatabase mongoDatabase

    def setup() {
        mongoDatabase = mongoClient.getDatabase("insite")
    }

    def "An edcSystem with value RAVE is added to EDCConnectionDocument"() {
        given: "two documents missing edcSystem attribute"
        String firstDocumentStudyId = "TEST_STUDY_1"
        String secondDocumentStudyId = "TEST_STUDY_2"

        when: "migration is performed"
        AddEDCSystem.forDatabase(mongoDatabase).migrate()

        then: "both documents are updated with a new attribute 'edcSystem' having value 'RAVE'"
        validateMigration(firstDocumentStudyId, "after-1")
        validateMigration(secondDocumentStudyId, "after-2")
    }

    void validateMigration(String actualDocumentStudyId, String expectedDocumentFileName) {
        Document document = mongoDatabase.getCollection(EDCConnectionDocument.COLLECTION)
                .find(eq("_id.studyId", actualDocumentStudyId)).first()
        JSONAssert.assertEquals(readFile(expectedDocumentFileName), document.toJson(), JSONCompareMode.STRICT)
    }

    private String readFile(String filename) {
        return new String(
                FileCopyUtils.copyToByteArray(new ClassPathResource("migrations/edcconnectiondocument/add-edc-system/" + filename + ".json").getFile()))
    }
}
