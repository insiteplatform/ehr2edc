package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.interpretation

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectedDataPoint
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionChain
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValueInterpretation
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.laboratory.LabValueObjectMother.insulinLabValueBuilder

class InterpretationToOriginalProjectorSpec extends Specification {
    @Unroll("Projecting the original interpretation '#value' of a labvalue returns: #expected")
    def "Projecting the original interpretation when a qualitative result is present"() {
        given: "A lab value with parsed interpretation '#value'"
        LabValue labValue = insulinLabValueBuilder()
                .withQualitativeResult(LabValueInterpretation.newBuilder()
                        .withOriginalInterpretation(value)
                        .build())
                .build()

        when: "I project the labvalue to original"
        def projectors = [new LabValueToInterpretationProjector(), new InterpretationToOriginalProjector()]
        ProjectedDataPoint projected = ProjectionChain.of(projectors).project(labValue, ProjectionContext.newBuilder().build())

        then: "The expected result is returned"
        with(projected) {
            it.result == expected
            it.projectionSteps.size() == projectors.size()
        }

        where:
        value      || expected
        "positief" || "positief"
        "negatief" || "negatief"
        "+++"      || "+++"
        ""         || ""
        null       || null
    }

    def "Projecting the interpretation when no original interpretation is present"() {
        given: "A lab value without parsed interpretation"
        LabValue labValue = insulinLabValueBuilder()
                .withQualitativeResult(LabValueInterpretation.newBuilder()
                        .build())
                .build()

        when: "I project the labvalue to original"
        def projectors = [new LabValueToInterpretationProjector(), new InterpretationToOriginalProjector()]
        ProjectedDataPoint projected = ProjectionChain.of(projectors).project(labValue, ProjectionContext.newBuilder().build())

        then: "No result is present"
        with(projected) {
            !it.result
            it.projectionSteps.size() == projectors.size()
        }
    }
}
