package com.custodix.insite.local.ehr2edc.query.executor.common.projector

import com.custodix.insite.local.ehr2edc.query.executor.common.domain.FormItem
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.formitem.ProjectedValueToFormItem
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValueField
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.Label
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue
import spock.lang.Specification
import spock.lang.Unroll

import static com.custodix.insite.local.ehr2edc.query.executor.common.projector.formitem.ProjectedValueToFormItem.UnitMapping.*
import static com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValueField.*
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectedValueObjectMother.*
import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext
import static java.util.Collections.emptyMap

class ProjectedValueToFormItemSpec extends Specification {
    @Unroll
    def "Projecting the '#field' field of a projected value to a form item"(ProjectedValueField field, LabeledValue expectedValue) {
        given: "A projected value"
        def projectedValue = aProjectedValue()

        when: "I project the '#field' of the projected value to a form item"
        Optional<FormItem> formItem = new ProjectedValueToFormItem(emptyMap(), field, IGNORE, false, false)
                .project(Optional.of(projectedValue), aProjectionContext())

        then: "A form item is returned with value '#expectedValue'"
        formItem.isPresent()
        with(formItem.get()) {
            it.value == expectedValue
            it.unit == null
            it.index == null
            !it.readOnly
            !it.outputUnit
            !it.key
        }

        where:
        field || expectedValue
        VALUE || new LabeledValue("2.0")
        UNIT  || new LabeledValue("mg")
        CODE  || new LabeledValue("123456")
    }

    def "Projecting a projected value with labeled value to a form item"() {
        given: "A projected value with labeled value '#labeledValue'"
        def projectedValue = aProjectedValueWithLabeledValue(labeledValue)

        when: "I project the projected value to a form item"
        Optional<FormItem> formItem = new ProjectedValueToFormItem(emptyMap(), VALUE, IGNORE, false, false)
                .project(Optional.of(projectedValue), aProjectionContext())

        then: "A form item is returned with value '#labeledValue'"
        formItem.isPresent()
        with(formItem.get()) {
            it.value == labeledValue
        }

        where:
        labeledValue = new LabeledValue("C25301",
                Arrays.asList(new Label(Locale.ENGLISH, "DAYS"), new Label(Locale.FRENCH, "JOURS")))
    }

    def "Projecting a projected value with code to a form item with an index mapping"() {
        given: "A projected value with code '#code'"
        def projectedValue = aProjectedValueWithCode(code)

        when: "I project the projected value to a form item with an index mapping '#indexMapping'"
        Optional<FormItem> formItem = new ProjectedValueToFormItem(indexMapping, VALUE, IGNORE, false, false)
                .project(Optional.of(projectedValue), aProjectionContext())

        then: "A form item is returned with index '#expectedIndex' without a unit"
        formItem.isPresent()
        with(formItem.get()) {
            it.index == expectedIndex
        }

        where:
        code = "123"
        indexMapping = ["123": "index-1", "456": "index-2"]
        expectedIndex = "index-1"
    }

    @Unroll
    def "Projecting a projected value with missing or unknown code to a form item with an index mapping"() {
        given: "A projected value with missing or unknown code"

        when: "I project the projected value to a form item using the index mapping '#indexMapping'"
        Optional<FormItem> result = new ProjectedValueToFormItem(indexMapping, VALUE, IGNORE, false, false)
                .project(Optional.ofNullable(projectedValue), aProjectionContext())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        projectedValue                         | _
        null                                   | _
        aProjectedValueWithCode(null)          | _
        aProjectedValueWithCode("not-present") | _

        and:
        indexMapping = ["123": "index-1", "456": "index-2"]
    }

    def "Projecting a projected value to a form item with read-only"() {
        given: "A projected value"
        def projectedValue = aProjectedValue()

        when: "I project the projected value to a form item with read-only"
        Optional<FormItem> formItem = new ProjectedValueToFormItem(emptyMap(), VALUE, IGNORE, true, false)
                .project(Optional.of(projectedValue), aProjectionContext())

        then: "A read-only form item is returned"
        formItem.isPresent()
        with(formItem.get()) {
            it.readOnly
        }
    }

    def "Projecting a projected value to a form item with key enabled"() {
        given: "A projected value"
        def projectedValue = aProjectedValue()

        when: "I project the projected value to a form item with key enabled"
        Optional<FormItem> formItem = new ProjectedValueToFormItem(emptyMap(), VALUE, IGNORE, false, true)
                .project(Optional.of(projectedValue), aProjectionContext())

        then: "A form item with key enabled is returned"
        formItem.isPresent()
        with(formItem.get()) {
            it.key
        }
    }

    @Unroll
    def "Projecting a projected value to a form item with unit-mapping '#unitMapping'"() {
        given: "A projected value"
        def projectedValue = aProjectedValue()

        when: "I project the projected value to a form item with unit-mapping '#unitMapping'"
        Optional<FormItem> formItem = new ProjectedValueToFormItem(emptyMap(), VALUE, unitMapping, false, false)
                .project(Optional.of(projectedValue), aProjectionContext())

        then: "A form item with unit '#expectedUnit' and outputUnit '#expectedOutputUnit' is returned"
        formItem.isPresent()
        with(formItem.get()) {
            it.unit == expectedUnit
            it.outputUnit == expectedOutputUnit
        }

        where:
        unitMapping || expectedUnit | expectedOutputUnit
        IGNORE      || null         | false
        DISPLAY     || "mg"         | false
        EXPORT      || "mg"         | true
    }

    @Unroll
    def "Projecting a projected value with invalid code, value and unit to a form item with unit-mapping"(
            ProjectedValueToFormItem.UnitMapping unitMapping,
            String code, Object value, String unit,
            String expectedValue, String expectedUnit) {
        given: "A projected value with code '#code', value '#value' and unit '#unit'"
        def projectedValue = aProjectedValue(code, value, unit)

        when: "I project the projected value to a form item with unit-mapping '#unitMapping'"
        Optional<FormItem> formItem = new ProjectedValueToFormItem(emptyMap(), VALUE, unitMapping, false, false)
                .project(Optional.of(projectedValue), aProjectionContext())

        then: "A form item with value '#expectedValue' and unit '#expectedUnit' is returned"
        formItem.isPresent()
        with(formItem.get()) {
            it.value == expectedValue
            it.unit == expectedUnit
        }

        where:
        unitMapping | code  | value | unit        || expectedValue | expectedUnit
        IGNORE      | null  | null  | null        || null          | null
        IGNORE      | ""    | ""    | ""          || null          | null
        IGNORE      | "   " | "  "  | "         " || null          | null

        DISPLAY     | null  | null  | null        || null          | null
        DISPLAY     | ""    | ""    | ""          || null          | null
        DISPLAY     | "   " | "  "  | "         " || null          | null

        EXPORT      | null  | null  | null        || null          | null
        EXPORT      | ""    | ""    | ""          || null          | null
        EXPORT      | "   " | "  "  | "         " || null          | null
    }
}
