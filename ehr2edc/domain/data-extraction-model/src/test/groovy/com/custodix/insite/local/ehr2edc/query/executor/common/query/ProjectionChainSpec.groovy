package com.custodix.insite.local.ehr2edc.query.executor.common.query


import spock.lang.Specification

import static com.custodix.insite.local.ehr2edc.query.executor.objectmother.ProjectionContexts.aProjectionContext

class ProjectionChainSpec extends Specification {

    def LowerCaseProjector = [
            project: { Optional<String> input, ctx -> input.map { s -> s.toLowerCase() }},
            getName: { "toLowerCase" }
    ] as Projector<String, String>
    def UpperCaseProjector = [
            project: { Optional<String> input, ctx -> input.map { s -> s.toUpperCase() }},
            getName: { "toUpperCase" }
    ] as Projector<String, String>
    def EmptyToValueProjector = [
            project: { Optional<Object> input, ctx -> input.isPresent() ? Optional.empty() : Optional.of("value") },
            getName: { "emptyToValue" }
    ] as Projector<Object, String>

    def "It creates a ProjectedDataPoint containing all projection steps"() {
        given: "An input 'an Input String'"
        DataPoint input = "an Input String" as DataPoint
        and: "A projection chain ['toLowerCase' > 'toUppercase']"
        ProjectionChain chain = ProjectionChain.of([LowerCaseProjector, UpperCaseProjector]);

        when: "Executing the projection chain"
        ProjectedDataPoint projected = chain.project(input, aProjectionContext())

        then: "The result is 'AN INPUT STRING'"
        projected.result == "AN INPUT STRING"
        and: "There are two projection steps"
        projected.projectionSteps.size() == 2
        and: "The first step was 'toLowerCase' with input 'an Input String' and output 'an input string'"
        with(projected.projectionSteps[0]) {
            it.projector == "toLowerCase"
            it.input == input
            it.output == "an input string"
        }
        and: "The second step was 'toUpperCase' with input 'an input string' and output 'AN INPUT STRING'"
        with(projected.projectionSteps[1]) {
            it.projector == "toUpperCase"
            it.input == "an input string"
            it.output == "AN INPUT STRING"
        }
    }

    def "It unwraps empty inputs and outputs in the projection steps"() {
        given: "A projection chain ['emptyToValue']"
        ProjectionChain chain = ProjectionChain.of([LowerCaseProjector])

        when: "Projecting without an input"
        ProjectedDataPoint projected = chain.project(null, aProjectionContext())

        then: "The result is empty"
        !projected.result
        and: "A projection step 'toLowerCase' is present with input 'null' and output 'null'"
        projected.projectionSteps.size() == 1
        with(projected.projectionSteps[0]) {
            it.projector == "toLowerCase"
            it.input == null
            it.output == null
        }
    }
}
