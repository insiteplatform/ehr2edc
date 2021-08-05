package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector

import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.insulinLabValueBuilder
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class LabValueToVendorProjectorSpec extends Specification {
    def "Projecting the vendor"() {
        given: "A lab value with vendor #labVendor"
        LabValue labValue = aLabValue(labVendor)

        when: "I project the lab value to its vendor"
        Optional<String> vendor = new LabValueToVendorProjector().project(Optional.of(labValue), aProjectionContext())

        then: "The vendor #labVendor is returned"
        vendor.isPresent()
        vendor.get() == labVendor

        where:
        labVendor = "LAB_1"
    }

    @Unroll
    def "Projecting an empty vendor"() {
        given: "An empty vendor"

        when: "I project for the vendor"
        Optional<String> vendor = new LabValueToVendorProjector().project(Optional.ofNullable(labValue), aProjectionContext())

        then: "An empty result is returned"
        !vendor.isPresent()

        where:
        labValue        | _
        null            | _
        aLabValue(null) | _
    }

    static aLabValue(String vendor) {
        return insulinLabValueBuilder()
                .withVendor(vendor)
                .build()
    }
}
