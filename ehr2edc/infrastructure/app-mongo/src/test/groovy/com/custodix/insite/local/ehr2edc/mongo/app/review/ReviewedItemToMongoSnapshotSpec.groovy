package com.custodix.insite.local.ehr2edc.mongo.app.review

import com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.ReviewedProvenanceDataPointDocumentFactory
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItem
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItemObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedItemId
import spock.lang.Specification
import spock.lang.Unroll

class ReviewedItemToMongoSnapshotSpec extends Specification {
    @Unroll
    def "Converting a reviewed item to mongo snapshot correctly"(String instanceId) {
        given: "a reviewed item"
        SubmittedItem reviewedItem = SubmittedItemObjectMother.aDefaultSubmittedItemBuilder()
                .withInstanceId(SubmittedItemId.of(instanceId))
                .build()

        when: "converting to reviewed item mongo snapshot"
        def reviewedItemMongoSnapshot = ReviewedItemMongoSnapshot.of(reviewedItem, Mock(ReviewedProvenanceDataPointDocumentFactory))

        then: "converted correctly"
        reviewedItemMongoSnapshot.instanceId == instanceId

        where:
        instanceId = "instance-1"
    }
}
