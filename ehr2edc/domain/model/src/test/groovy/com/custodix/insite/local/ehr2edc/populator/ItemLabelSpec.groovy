package com.custodix.insite.local.ehr2edc.populator

import com.custodix.insite.local.ehr2edc.metadata.model.ItemLabel
import com.custodix.insite.local.ehr2edc.metadata.model.Question
import spock.lang.Specification

class ItemLabelSpec extends Specification {
    def "A label with no associated question is displayed by its name"() {
        given: "a label with no associated question"
        def label = ItemLabel.newBuilder().withName("name").build()

        when: "getting the representation for English"
        def displayLabel = label.getRepresentationFor(Locale.ENGLISH)

        then: "the name  is returned"
        displayLabel == "name"
    }

    def "A label with an associated question is displayed by the question's translated text"() {
        given: "a question with translated text"
        def question = new Question(Collections.singletonMap(Locale.ENGLISH, "Question"))
        and: "a label with the question associated"
        def label = ItemLabel.newBuilder().withName("name").withQuestion(question).build()

        when: "getting the representation for English"
        def displayLabel = label.getRepresentationFor(Locale.ENGLISH)

        then: "the english translation is returned"
        displayLabel == "Question"
    }

    def "A label with an associated question without suitable translated text is displayed by its name"() {
            given: "a question with translated text"
            def question = new Question(Collections.singletonMap(Locale.ENGLISH, "Question"))
            and: "a label with the question associated"
            def label = ItemLabel.newBuilder().withName("name").withQuestion(question).build()

            when: "getting the representation in a locale for which no suitable translated text is available"
            def displayLabel = label.getRepresentationFor(Locale.FRENCH)

            then: "the name is returned"
            displayLabel == "name"
        }
    }