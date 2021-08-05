package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.mapper

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException
import spock.lang.Specification

import static com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPointObjectMother.aUnknownProvenanceDataPoint

class ProvenanceMedicationDocumentMapperSpec extends Specification {
    def "An object other than ProvenanceMedication cannot be mapped to ProvenanceDataPointDocument"() {
        given: "An object other than ProvenanceMedication"
        ProvenanceDataPoint provenanceDataPoint = aUnknownProvenanceDataPoint()

        when: "I map the object to ProvenanceDataPointDocument"
        new ProvenanceMedicationDocumentMapper().map(provenanceDataPoint)

        then: "A SystemException is thrown"
        thrown SystemException
    }
}
