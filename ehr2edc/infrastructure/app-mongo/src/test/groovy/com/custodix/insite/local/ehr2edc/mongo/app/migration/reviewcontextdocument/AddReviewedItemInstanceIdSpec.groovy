package com.custodix.insite.local.ehr2edc.mongo.app.migration.reviewcontextdocument

import com.custodix.insite.local.ehr2edc.mongo.app.migration.DocumentJsonComparator
import org.bson.Document
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.core.io.ClassPathResource
import org.springframework.util.FileCopyUtils
import spock.lang.Specification

class AddReviewedItemInstanceIdSpec extends Specification {
    private static final String GENERATED_UUID_FIELD_PATH = "reviewedEvent.reviewedForms[*].reviewedItemGroups[*].reviewedItems[*].instanceId"

    def "An instanceId is added to ReviewedItemMongoSnapshot"() {
        given: "a document with a ReviewedItemMongoSnapshot without an instanceId field"
        Document document = readDocumentFromFile("before")

        when: "migration is performed"
        Document migrated = AddReviewedItemInstanceId.forReviewContext(document).migrate()

        then: "the instanceId field is populated with a generated UUID"
        def comparator = DocumentJsonComparator.newBuilder()
                .withGeneratedUUID(GENERATED_UUID_FIELD_PATH)
                .build()
        JSONAssert.assertEquals(readFile("after"), migrated.toJson(), comparator)
    }

    private Document readDocumentFromFile(String filename) {
        def documentString = readFile(filename)
        return Document.parse(documentString)
    }

    private String readFile(String filename) {
        return new String(
                FileCopyUtils.copyToByteArray(new ClassPathResource("migrations/reviewcontextdocument/add-reviewed-item-instance-id/" + filename + ".json").getFile()))
    }
}
