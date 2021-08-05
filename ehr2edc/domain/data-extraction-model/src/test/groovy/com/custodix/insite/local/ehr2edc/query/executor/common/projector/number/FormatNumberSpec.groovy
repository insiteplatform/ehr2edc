package com.custodix.insite.local.ehr2edc.query.executor.common.projector.number

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class FormatNumberSpec extends Specification {

    @Shared
    def POINT = '.' as char
    @Shared
    def COMMA = ',' as char

    @Unroll
    def "Format the decimal '#decimal' with '#fraction' maximum fraction digits using #decimalSeparator as decimal separator"(Number decimal, int fraction, Character decimalSeparator, String result) {
        expect: " to see the decimal '#result' when I project the decimal '#decimal' with '#fraction' maximum fraction digits and decimal seperator '#decimalSeparator'"
        format(decimal, decimalSeparator, fraction) == result

        where:
        decimal                             | fraction | decimalSeparator || result
        new BigDecimal(1.56789)             | 2        | POINT            || "1.57"
        new BigDecimal(1.56789)             | 4        | POINT            || "1.5679"
        new BigDecimal(1.56789)             | 1        | POINT            || "1.6"
        new BigDecimal(1.51789)             | 1        | POINT            || "1.5"
        new Double(1.51789)                 | 1        | POINT            || "1.5"
        new Double(1.51000)                 | 3        | POINT            || "1.51"
        new BigDecimal(123456789.987654321) | 2        | POINT            || "123456789.99"
        new BigDecimal(123456789.987654321) | 5        | POINT            || "123456789.98765"
        new BigDecimal(1.56789)             | 2        | COMMA            || "1,57"
        new BigDecimal(1.56789)             | 4        | COMMA            || "1,5679"
        new BigDecimal(1.56789)             | 1        | COMMA            || "1,6"
        new BigDecimal(1.51789)             | 1        | COMMA            || "1,5"
        new Double(1.51789)                 | 1        | COMMA            || "1,5"
        new Double(1.51000)                 | 3        | COMMA            || "1,51"
        new BigDecimal(123456789.987654321) | 2        | COMMA            || "123456789,99"
        new BigDecimal(123456789.987654321) | 5        | COMMA            || "123456789,98765"
        new BigDecimal(1.56789)             | 2        | null             || "1.57"
        new BigDecimal(1.56789)             | 4        | null             || "1.5679"
        new BigDecimal(1.56789)             | 1        | null             || "1.6"
        new BigDecimal(1.51789)             | 1        | null             || "1.5"
        new Double(1.51789)                 | 1        | null             || "1.5"
        new Double(1.51000)                 | 3        | null             || "1.51"
        new BigDecimal(123456789.987654321) | 2        | null             || "123456789.99"
        new BigDecimal(123456789.987654321) | 5        | null             || "123456789.98765"
    }

    def format(Number decimal, Character decimalSeparator, int fraction) {
        new FormatNumber(fraction, decimalSeparator).project(Optional.of(decimal), aProjectionContext()).get()
    }

    def "Projecting an empty decimal value"() {
        given: "An empty decimal"
        Optional<BigDecimal> emptyDecimal = Optional.empty()

        when: "I project for the value"
        Optional<String> value = new FormatNumber(1, POINT).project(emptyDecimal, aProjectionContext())

        then: "An empty result is returned"
        !value.isPresent()
    }
}
