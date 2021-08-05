package com.custodix.insite.local.ehr2edc.query.executor.common.projector

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.Label
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class MapToLabeledValueSpec extends Specification {
    @Unroll
    def "MapToLabeledValue adds human-readable labels to a value"(Optional<String> input, Optional<LabeledValue> output) {
        given: "A mapping that maps a code list to a set of labels"
        def mapping = [
                "C25301": [new Label(Locale.ENGLISH, "DAYS"), new Label(Locale.FRENCH, "JOURS")],
        ]

        when: "I map '#input' to a LabeledValue"
        Optional<LabeledValue> result = new MapToLabeledValue(mapping).project(input, aProjectionContext())

        then: "'#output' is returned"
        result == output

        where:
        input                           | output
        Optional.empty()                | Optional.empty()
        Optional.of("")                 | Optional.of(new LabeledValue("", []))
        Optional.of("unexpected value") | Optional.of(new LabeledValue("unexpected value", []))
        Optional.of("C25301")           | Optional.of(new LabeledValue("C25301", [new Label(Locale.ENGLISH, "DAYS"), new Label(Locale.FRENCH, "JOURS")]))
    }
}
