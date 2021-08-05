package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.FormatProjectedValueNumber
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectedValueObjectMother.aProjectedValueWithNumericValue

class FormatProjectedValueNumberSpec extends Specification {

    @Shared
    def POINT = '.' as char
    @Shared
    def COMMA = ',' as char

    @Unroll
    def "Formatting a projected value #sourceValue to its formatted value with #maximumFractionDigits maximum fraction digits and decimal separator #decimalSeparator"(
            BigDecimal sourceValue, int maximumFractionDigits, Character decimalSeparator, String expectedValue) {
        given: "A projected value #sourceValue"
        ProjectedValue projectedValue = aProjectedValueWithNumericValue(sourceValue)

        when: "I format the value with maximum fraction digits #maximumFractionDigits and decimal seperator #decimalSeparator"
        Optional<ProjectedValue> value = formatNumber(maximumFractionDigits, decimalSeparator).project(Optional.of(projectedValue), aProjectionContext())

        then: "The value #expectedValue is returned"
        value.isPresent()
        value.get().value == expectedValue

        where:
        sourceValue               | maximumFractionDigits | decimalSeparator || expectedValue
        new BigDecimal("1.56789") | 2                     | null             || "1.57"
        new BigDecimal("1.5")     | 2                     | null             || "1.5"
        new BigDecimal("1.56789") | 2                     | POINT            || "1.57"
        new BigDecimal("1.5")     | 2                     | POINT            || "1.5"
        new BigDecimal("1.56789") | 2                     | COMMA            || "1,57"
        new BigDecimal("1.5")     | 2                     | COMMA            || "1,5"
    }

    @Unroll
    def "Formatting an empty projected value"() {
        when: "I format an empty value"
        Optional<ProjectedValue> value = formatNumber(2, COMMA).project(Optional.ofNullable(projectedValue), aProjectionContext())

        then: "An empty result is returned"
        !value.isPresent()

        where:
        projectedValue                        | _
        null                                  | _
        aProjectedValueWithNumericValue(null) | _
    }

    def formatNumber(int maximumFractionDigits, Character decimalSeparator) {
        return new FormatProjectedValueNumber(maximumFractionDigits, decimalSeparator)
    }
}
