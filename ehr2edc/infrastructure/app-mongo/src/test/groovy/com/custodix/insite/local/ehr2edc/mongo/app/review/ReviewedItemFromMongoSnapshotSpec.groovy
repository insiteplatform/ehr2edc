package com.custodix.insite.local.ehr2edc.mongo.app.review

import com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.ReviewedProvenanceLabValueDocumentObjectMother
import com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.ReviewedProvenanceMedicationDocumentObjectMother
import com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.ReviewedProvenanceVitalSignDocumentObjectMother
import com.custodix.insite.local.ehr2edc.provenance.model.*
import com.custodix.insite.local.ehr2edc.submitted.SubmittedLabel
import com.custodix.insite.local.ehr2edc.submitted.SubmittedLabeledValue
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.mongo.app.review.ReviewedItemMongoSnapshotObjectMother.aDefaultReviewedItemMongoSnapshotBuilder
import static com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.ReviewedProvenanceDemographicDocumentObjectMother.aDefaultReviewedProvenanceDemographicDocument
import static java.util.Collections.emptyList
import static java.util.Collections.singletonList

class ReviewedItemFromMongoSnapshotSpec extends Specification {
    @Unroll
    def "Converting a reviewed item mongo snapshot to reviewed item correctly"(String instanceId, String itemId, String value, Locale locale, String label) {
        given: "a reviewed item mongo snapshot"
        def reviewedLabelValue = ReviewedLabeledValueDocument.newBuilder()
                .withValue(value)
                .withLabels(singletonList(
                        ReviewedLabelDocument.newBuilder()
                                .withLocale(locale)
                                .withText(label).build()))
                .build()
        def reviewedItemMongoSnapshot = aDefaultReviewedItemMongoSnapshotBuilder()
                .withInstanceId(instanceId)
                .withId(itemId)
                .withLabeledValue(reviewedLabelValue)
                .withKey(true)
                .build()

        when: "converting to reviewed item"
        def reviewedItem = reviewedItemMongoSnapshot.toReviewedItem()

        then: "converted correctly"
        reviewedItem.instanceId.id == instanceId
        reviewedItem.id.id == itemId
        reviewedItem.value == SubmittedLabeledValue.newBuilder()
                .withValue("10.097")
                .withLabels(singletonList(SubmittedLabel.newBuilder()
                        .withLocale(Locale.FRENCH)
                        .withText("label")
                        .build()))
                .build()
        reviewedItem.key

        where:
        instanceId   | itemId      | value    | locale        | label
        "instance-1" | "123-12309" | "10.097" | Locale.FRENCH | "label"
    }

    @Unroll
    def "toReviewedItem convert labeled value correctly with value from labeled value"(String value, ReviewedLabeledValueDocument labeledValue) {
        given: "Document item with value #value and labeledValue #labeledValue"
        def itemDocument = aDefaultReviewedItemMongoSnapshotBuilder()
                .withLabeledValue(labeledValue)
                .build()

        when: "convert to reviewedItem"
        def reviewedItem = itemDocument.toReviewedItem()

        then: "labeled value is correctly converted"
        reviewedItem.getLabeledValue().getValue() == labeledValue.value

        where:
        value   | labeledValue
        "value" | ReviewedLabeledValueDocumentObjectMother.aDefaultReviewedLabeledValueDocument()
    }

    def "toReviewedItem convert demographic datapoint correctly"() {
        given: "Document item with demographic datapoint"
        def itemDocument = aDefaultReviewedItemMongoSnapshotBuilder()
                .withDataPoint(aDefaultReviewedProvenanceDemographicDocument())
                .build()

        when: "convert to reviewedItem"
        def reviewedItem = itemDocument.toReviewedItem()

        then: "datapoint is correctly converted"
        with(reviewedItem.dataPoint, ProvenanceDemographic) {
            demographicType == DemographicType.GENDER
            value == "M"
        }
    }

    def "toReviewedItem convert lab value datapoint correctly"() {
        given: "Document item with lab value datapoint"
        def itemDocument = aDefaultReviewedItemMongoSnapshotBuilder()
                .withDataPoint(ReviewedProvenanceLabValueDocumentObjectMother.aDefaultReviewedProvenanceLabValueDocument())
                .build()

        when: "convert to reviewedItem"
        def reviewedItem = itemDocument.toReviewedItem()

        then: "datapoint is correctly converted"
        with(ReviewedProvenanceLabValueDocumentObjectMother) {
            with(reviewedItem.dataPoint, ProvenanceLabValue) {
                startDate == INSULIN_START_DATE
                endDate == INSULIN_END_DATE
                vendor == INSULIN_VENDOR
                with(labConcept) {
                    concept.code == INSULIN_CODE
                    component == INSULIN_COMPONENT
                    method == INSULIN_METHOD
                    fastingStatus == INSULIN_FASTING_STATUS
                    specimen == INSULIN_SPECIMEN
                }
                with(quantitativeResult) {
                    lowerLimit == INSULIN_LLN
                    upperLimit == INSULIN_ULN
                    value == INSULIN_VALUE
                    unit == INSULIN_UNIT
                }
                with(qualitativeResult) {
                    parsedInterpretation == INSULIN_QUALITATIVE_PARSED
                    originalInterpretation == INSULIN_QUALITATIVE_ORIGINAL
                }
            }
        }
    }

    def "toReviewedItem convert medication datapoint correctly"() {
        given: "Document item with medication datapoint"
        def itemDocument = aDefaultReviewedItemMongoSnapshotBuilder()
                .withDataPoint(ReviewedProvenanceMedicationDocumentObjectMother.omeprazole())
                .build()

        when: "convert to reviewedItem"
        def reviewedItem = itemDocument.toReviewedItem()

        then: "datapoint is correctly converted"
        with(ReviewedProvenanceMedicationDocumentObjectMother) {
            with(reviewedItem.dataPoint, ProvenanceMedication) {
                concept.concept.code == OMEPRAZOLE_CODE
                concept.name == OMEPRAZOLE_NAME
                startDate == OMEPRAZOLE_START_DATE
                endDate == OMEPRAZOLE_END_DATE
                dosage.value == OMEPRAZOLE_DOSAGE_VALUE
                dosage.unit == OMEPRAZOLE_DOSAGE_UNIT
                administrationRoute == OMEPRAZOLE_ADMINISTRATION_ROUTE_ORAL
                doseForm == OMEPRAZOLE_DOSE_FORM_CAPSULE
                dosingFrequency == OMEPRAZOLE_DOSING_FREQUENCY_DAILY
                eventType == OMEPRAZOLE_EVENT_TYPE
            }
        }
    }

    def "toReviewedItem convert vital sign datapoint correctly"() {
        given: "Document item with vital sign datapoint"
        def itemDocument = aDefaultReviewedItemMongoSnapshotBuilder()
                .withDataPoint(ReviewedProvenanceVitalSignDocumentObjectMother.diastolicBloodPressureProvenance())
                .build()

        when: "convert to reviewedItem"
        def reviewedItem = itemDocument.toReviewedItem()

        then: "datapoint is correctly converted"
        with(ReviewedProvenanceVitalSignDocumentObjectMother) {
            with(reviewedItem.dataPoint, ProvenanceVitalSign) {
                with(concept) {
                    concept.code == DIABP_CODE
                    component == DIABP_COMPONENT
                    location == DIABP_LOCATION_ARM
                    laterality == DIABP_LATERALITY_LEFT
                    position == DIABP_POSITION_SITTING
                }
                effectiveDateTime == DIABP_TIMESTAMP
                with(measurement) {
                    lowerLimit == DIABP_LLN
                    upperLimit == DIABP_ULN
                    value == DIABP_VALUE
                    unit == DIABP_UNIT
                }
            }
        }
    }

    @Unroll
    def "toReviewedItem convert labels correctly "(List<ReviewedLabelDocument> labels, List<SubmittedLabel> expectedLabels) {
        given: "Document item with labels #labels"
        def itemDocument = aDefaultReviewedItemMongoSnapshotBuilder()
                .withLabeledValue(ReviewedLabeledValueDocumentObjectMother.aDefaultReviewedLabeledValueDocumentBuilder()
                        .withLabels(labels).build())
                .build()

        when: "convert to reviewedItem"
        def reviewedItem = itemDocument.toReviewedItem()

        then: "labels is correctly converted"
        reviewedItem.getLabeledValue().labels == expectedLabels

        where:
        labels            || expectedLabels
        null              || emptyList()
        emptyList()       || emptyList()
        singletonList(ReviewedLabelDocument.newBuilder()
                .withText("text-098")
                .withLocale(Locale.CANADA_FRENCH)
                .build()) || singletonList(SubmittedLabel.newBuilder().withText("text-098").withLocale(Locale.CANADA_FRENCH).build())
    }
}
