package com.custodix.insite.local.ehr2edc.query.executor.common.query

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue
import spock.lang.Specification

class ProjectionLoggerSpec extends Specification {
    def "A String value is logged correctly"() {
        given:
        String string = "Test string"

        when:
        String log = ProjectionLogger.mapToString(Optional.of(string))

        then:
        log == "Test string"
    }

    def "A BigDecimal logs the actual value"() {
        given:
        BigDecimal bigDecimal = new BigDecimal("2.5")

        when:
        String log = ProjectionLogger.mapToString(Optional.of(bigDecimal))

        then:
        log == "BigDecimal[2.5]"
    }

    def "A projected value containing a BigDecimal logs the actual value"() {
        given:
        ProjectedValue projectedValue = new ProjectedValue(new BigDecimal("2.5"), "mg", "123")

        when:
        String log = ProjectionLogger.mapToString(Optional.of(projectedValue))

        then:
        log == "ProjectedValue[value=BigDecimal[2.5],unit=mg,code=123]"
    }
}
