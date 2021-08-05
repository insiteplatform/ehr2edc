package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.mapper

import com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.ReviewedProvenanceDemographicDocumentMapper
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException
import spock.lang.Specification

class ReviewedProvenanceDemographicDocumentMapperSpec extends Specification {

    def "An object other than ProvenanceDemographic cannot be mapped to ProvenanceDataPointDocument"() {
        given: "An object other than ProvenanceDemographic"
        ProvenanceDataPoint provenanceDataPoint = new ProvenanceDataPoint() {}

        when: "I map the object to ReviewedProvenanceDataPointDocument"
        new ReviewedProvenanceDemographicDocumentMapper().map(provenanceDataPoint)

        then: "A SystemException is thrown"
        thrown SystemException
    }
}
