package com.custodix.insite.local.ehr2edc.mongo.app.review

import spock.lang.Specification

class ReviewedMeasurementUnitReferenceMongoSnapShotSpec extends Specification {

    def "a unit without submittedToEDC value is always submitted to EDC"() {
        given: "Create ReviewedMeasurementUnitReferenceMongoSnapShot without submittedToEDC"
        def reviewedMeasurementUnitReferenceMongoSnapShot = new ReviewedMeasurementUnitReferenceMongoSnapShot("id", null)

        when: "getting the submittedToEDC value"
        def submittedToEDC = reviewedMeasurementUnitReferenceMongoSnapShot.isSubmittedToEDC()

        then: "is submitted to EDC"
        submittedToEDC
    }

    def "a unit with submittedToEDC value false is not submitted to EDC"() {
        given: "Create ReviewedMeasurementUnitReferenceMongoSnapShot with submittedToEDC false "
        def reviewedMeasurementUnitReferenceMongoSnapShot = new ReviewedMeasurementUnitReferenceMongoSnapShot("id", false)

        when: "getting the submittedToEDC value"
        def submittedToEDC = reviewedMeasurementUnitReferenceMongoSnapShot.isSubmittedToEDC()

        then: "is NOT submitted to EDC"
        !submittedToEDC
    }


    def "a unit with submittedToEDC value true is submitted to EDC"() {
        given: "Create ReviewedMeasurementUnitReferenceMongoSnapShot with submittedToEDC false "
        def reviewedMeasurementUnitReferenceMongoSnapShot = new ReviewedMeasurementUnitReferenceMongoSnapShot("id", true)

        when: "getting the submittedToEDC value"
        def submittedToEDC = reviewedMeasurementUnitReferenceMongoSnapShot.isSubmittedToEDC()

        then: "is submitted to EDC"
        submittedToEDC
    }
}
