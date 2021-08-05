package com.custodix.insite.local.ehr2edc.query.executor.medication.projector

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Dosage
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class ExtractValueFromDosageSpec extends Specification {
    def "Projecting a dosage"() {
        given: "A dosage #value and unit dosageUnit"
        Dosage dosage = aDosage(value)

        when: "I project for dosage value"
        Optional<BigDecimal> dosageResult = new ExtractValueFromDosage().project(Optional.of(dosage), aProjectionContext())

        then: "#value gets returned"
        dosageResult.isPresent()
        dosageResult.get() == BigDecimal.TEN

        where:
        value = BigDecimal.TEN
    }


    @Unroll
    def "Projecting an empty dosage"() {
        given: "An empty value"

        when: "I project for the dosage value"
        Optional<BigDecimal> dosageResult = new ExtractValueFromDosage().project(Optional.ofNullable(dosage), aProjectionContext())

        then: "An empty result is returned"
        !dosageResult.isPresent()

        where:
        dosage        | _
        null          | _
        aDosage(null) | _
    }

    def aDosage(BigDecimal value) {
        Dosage.newBuilder().withValue(value).withUnit("dosageUnit").build()
    }
}
