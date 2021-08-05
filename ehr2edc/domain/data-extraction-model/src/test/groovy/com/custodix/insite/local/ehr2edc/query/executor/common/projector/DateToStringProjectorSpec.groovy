package com.custodix.insite.local.ehr2edc.query.executor.common.projector

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.LocalDateTime

class DateToStringProjectorSpec extends Specification {
    @Unroll
    def "DateToStringProjector converts local date objects to String of requested pattern"() {
        given: "A date-time representing 10/12/2019"
        LocalDate date = LocalDate.of(2019, 12, 10)

        when: "I apply the projection to pattern '#pattern'"
        def result = new DateToStringProjector(pattern).project(Optional.of(date), ProjectionContext.newBuilder().build())

        then: "I expect '#expected' to be returned"
        result.isPresent()
        result.get() == expected

        where:
        pattern      | expected
        "yyyy-MM-dd" | "2019-12-10"
        "dd/MM/yyyy" | "10/12/2019"
        "yyyy"       | "2019"
    }

    @Unroll
    def "DateToStringProjector converts local datetime objects to String of requested pattern"() {
        given: "A date-time representing 10/12/2019 at 9:13 in the morning"
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 10, 9, 13)

        when: "I apply the projection to pattern '#pattern'"
        def result = new DateToStringProjector(pattern).project(Optional.of(dateTime), ProjectionContext.newBuilder().build())

        then: "I expect '#expected' to be returned"
        result.isPresent()
        result.get() == expected

        where:
        pattern            | expected
        "yyyy-MM-dd"       | "2019-12-10"
        "dd/MM/yyyy"       | "10/12/2019"
        "yyyy"             | "2019"
        "yyyy-MM-dd hh:mm" | "2019-12-10 09:13"
    }
}
