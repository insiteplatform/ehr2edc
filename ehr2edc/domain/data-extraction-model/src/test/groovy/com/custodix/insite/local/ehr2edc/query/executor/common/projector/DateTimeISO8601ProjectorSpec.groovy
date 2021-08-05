package com.custodix.insite.local.ehr2edc.query.executor.common.projector


import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.ZonedDateTime


class DateTimeISO8601ProjectorSpec extends Specification {

    def "toISO8601DateTime converts datetime objects to expected String"() {
        given: "A date-time representing 10/12/2019 at 9:12 in the morning"
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDate.of(2019, 12, 10),
                LocalTime.of(9, 12),
                ZoneOffset.UTC)

        when: "I apply the projection"
        def result = toISO8601DateTime().project(Optional.of(dateTime), ProjectionContext.newBuilder().build())

        then: "I expect the '#expected' to be returned"
        result.isPresent()
        result.get() == expected

        where:
        expected = "2019-12-10T09:12:00Z"
    }

    def toISO8601DateTime() {
        return new DateTimeISO8601Projector()
    }
}
