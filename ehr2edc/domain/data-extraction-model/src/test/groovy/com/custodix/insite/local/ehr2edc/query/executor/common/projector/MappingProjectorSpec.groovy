package com.custodix.insite.local.ehr2edc.query.executor.common.projector


import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext
import spock.lang.Specification
import spock.lang.Unroll

class MappingProjectorSpec extends Specification {

    def "mapToString maps input to matching output"() {
        given: "A mapping dictionary"
        def mapping = ["key"        : "value",
                       "another key": "another value"]

        when: "I project 'key' with the MappingProjector"
        Optional<String> result = new MapToStringProjector(mapping)
                .project(Optional.ofNullable("key"), ProjectionContext.newBuilder().build())

        then: "'value' is returned"
        result.isPresent()
        result.get() == 'value'
    }

    @Unroll("Maps invalid input '#input' to empty result")
    def "mapToString maps missing input or mapping to empty result"() {
        given: "A mapping dictionary"
        def mapping = ["key"        : "value",
                       "another key": "another value"]

        when: "I project '#input' with the MappingProjector"
        Optional<String> result = new MapToStringProjector(mapping)
                .project(Optional.ofNullable(input), ProjectionContext.newBuilder().build())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        input << ["not-present", null]
    }

    def "mapToBoolean maps input to matching output"() {
        given: "A mapping dictionary"
        def mapping = ["key"        : Boolean.TRUE,
                       "another key": Boolean.TRUE]

        when: "I project 'key' with the MappingProjector"
        Optional<Boolean> result = new MapToBooleanProjector(mapping)
                .project(Optional.ofNullable("key"), ProjectionContext.newBuilder().build())

        then: "'value' is returned"
        result.isPresent()
        result.get()
    }

    @Unroll("Maps invalid input '#input' to false")
    def "mapToBoolean maps missing input or mapping to empty result"() {
        given: "A mapping dictionary"
        def mapping = ["key"        : Boolean.TRUE,
                       "another key": Boolean.TRUE]

        when: "I project '#input' with the MappingProjector"
        Optional<Boolean> result = new MapToBooleanProjector(mapping)
                .project(Optional.ofNullable(input), ProjectionContext.newBuilder().build())

        then: "An empty result is returned"
        !result.isPresent()

        where:
        input << ["not-present", null]
    }
}
