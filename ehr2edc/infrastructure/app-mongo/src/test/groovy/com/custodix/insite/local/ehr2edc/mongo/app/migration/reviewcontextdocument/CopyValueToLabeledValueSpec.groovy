package com.custodix.insite.local.ehr2edc.mongo.app.migration.reviewcontextdocument

import com.custodix.insite.local.ehr2edc.mongo.app.migration.reviewcontextdocument.CopyValueToLabeledValue
import org.bson.Document
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.core.io.ClassPathResource
import org.springframework.util.FileCopyUtils
import spock.lang.Specification

class CopyValueToLabeledValueSpec extends Specification {
    def "The value field of ReviewedItemMongoSnapshot is migrated to the labeledValue field"() {
        given: "a document with a ReviewedItemMongoSnapshot with a value field"
        Document document = readDocumentFromFile("before")

        when: "migration is performed"
        Document migrated = CopyValueToLabeledValue.forReviewContext(document).migrate()

        then: "the value field is copied to the labeledValue field"
        JSONAssert.assertEquals(
                readFile("after"), migrated.toJson(),
                JSONCompareMode.STRICT)
    }

    private Document readDocumentFromFile(String filename) {
        def documentString = readFile(filename)
        return Document.parse(documentString)
    }

    private String readFile(String filename) {
        return new String(
                FileCopyUtils.copyToByteArray(new ClassPathResource("migrations/reviewcontextdocument/value-to-labeled-value/" + filename + ".json").getFile()))
    }
}
