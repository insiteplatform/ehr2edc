package com.custodix.insite.local.ehr2edc.shared.annotations

import com.custodix.insite.local.ehr2edc.shared.annotations.util.CorrelatorLookup
import spock.lang.Specification

class CorrelatorLookupSpec extends Specification {

    CorrelatorLookup correlatorLookup = CorrelatorLookup.ofType(AuthorizationCorrelator.class);

    def "Returns no correlator for empty input"() {
        expect: "No correlators are found on empty input"
        correlatorLookup.findAllRecursively(input) == []

        where:
        input << [null, [], [null]]
    }

    def "Returns no correlator for an object without correlator field"() {
        given: "A object without a correlator"
        def given = new WithoutCorrelator("example")

        when: "Retrieving the correlators"
        def result = correlatorLookup.findAllRecursively(given)

        then: "No correlators are returned"
        result.isEmpty()
    }

    def "Returns a correlator for an object with correlator field"() {
        given: "A correlator"
        String correlator = "correlator"
        and: "An object with correlator field"
        def given = new WithCorrelator(correlator)

        when: "Retrieving the correlators"
        def result = correlatorLookup.findAllRecursively(given)

        then: "The result contains the correlator"
        result.size() == 1
        result == [correlator]
    }

    def " Returns a correlator for an object with correlator field and cyclic transient fields (eg. LinkedHashMap)"() {
        given: "A correlator"
        String correlator = "correlator"
        and: "An object with correlator field and a linkedhashmap"
        def given = new WithCorrelatorAndLinkedHashMap(correlator)

        when: "Retrieving the correlators"
        def result = correlatorLookup.findAllRecursively(given)

        then: "The result contains the correlator"
        result.size() == 1
        result == [correlator]
    }

    def "Returns a correlator for an object within a Collection"() {
        given: "A correlator"
        String correlator = "correlator"
        and: "An object with correlator field"
        def given = new WithCorrelator(correlator)

        when: "Retrieving the correlators"
        def result = correlatorLookup.findAllRecursively([given])

        then: "The result contains the correlator"
        result.size() == 1
        result == [correlator]
    }

    def "Returns all correlators for objects within a Collection"() {
        given: "A correlator"
        String correlator = "correlator"
        and: "A collection containing several correlators"
        def with = new WithCorrelator(correlator)
        def without = new WithoutCorrelator("other")
        def collection = [with, without, with, without, with]

        when: "Retrieving the correlators"
        def result = correlatorLookup.findAllRecursively(collection)

        then: "The result contains all correlator"
        result.size() == 3
        result.each { it == correlator }
    }

    def "Returns a correlator for a nested object with correlator field"() {
        given: "A correlator"
        String correlator = "correlator"
        and: "An object with correlator field"
        def withCorrelator = new WithCorrelator(correlator)
        and: "A parent object"
        def given = new WithoutCorrelator(withCorrelator)

        when: "Retrieving the correlators"
        def result = correlatorLookup.findAllRecursively(given)

        then: "The result contains the correlator"
        result.size() == 1
        result == [correlator]
    }

    def "Returns a correlator for a nested object in a collection"() {
        given: "A correlator"
        String correlator = "correlator"
        and: "An object with correlator field"
        def withCorrelator = new WithCorrelator(correlator)
        and: "A parent object"
        def given = new WithoutCorrelator(withCorrelator)

        when: "Retrieving the correlators"
        def result = correlatorLookup.findAllRecursively([given])

        then: "The result contains the correlator"
        result.size() == 1
        result == [correlator]
    }

    class WithoutCorrelator {
        private final Object field

        WithoutCorrelator(Object field) {
            this.field = field;
        }
    }

    class WithCorrelator {
        @AuthorizationCorrelator
        private final Object correlatorField

        WithCorrelator(Object correlatorField) {
            this.correlatorField = correlatorField
        }
    }

    class WithCorrelatorAndLinkedHashMap {
        @AuthorizationCorrelator
        private final Object correlatorField
        private final LinkedHashMap<String, String> linkedHashMap

        WithCorrelatorAndLinkedHashMap(Object correlatorField) {
            this.correlatorField = correlatorField
            this.linkedHashMap = new LinkedHashMap<>()
            linkedHashMap.put("test-1", "contains transient field: test-2")
            linkedHashMap.put("test-2", "contains transient field: test-1")
        }
    }
}
