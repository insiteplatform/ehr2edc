package com.custodix.insite.local.ehr2edc.query.executor.medication.projector

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Dosage
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class ExtractUnitFromDosageSpec extends Specification {
    def "Projecting a dosage unit"() {
        given: "A dosage of value 1.0 and unit #unit"
        Dosage dosage = aDosage(unit)

        when: "I project for dosage unit"
        Optional<String> result = new ExtractUnitFromDosage().project(Optional.of(dosage), aProjectionContext())

        then: "#unit gets returned"
        result.isPresent()
        result.get() == unit

        where:
        unit = "unit"
    }

    @Unroll
    def "Projecting an empty dosage"() {
        given: "An empty unit"

        when: "I project for the dosage unit"
        Optional<String> result = new ExtractUnitFromDosage().project(Optional.ofNullable(dosage), aProjectionContext())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        dosage        | _
        null          | _
        aDosage(null) | _
    }

    def aDosage(String unit) {
        Dosage.newBuilder().withValue(BigDecimal.ONE).withUnit(unit).build()
    }

}
