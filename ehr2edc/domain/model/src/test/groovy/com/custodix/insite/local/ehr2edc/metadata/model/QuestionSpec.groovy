package com.custodix.insite.local.ehr2edc.metadata.model

import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import spock.lang.Specification


class QuestionSpec extends Specification {
    def "A question must have at least one associated translated text"() {
        when: "an item with no associated question is built"
            def question = new Question(Collections.emptyMap())
        then: "a domain exception is thrown"
            thrown DomainException
    }

    def "the ODM rules for finding appropriate translated text for a target language are used"(String locale, String translatedText) {
        given: "a question with associated translated texts"
        def map = new HashMap<Locale,String>()
        map.put(Locale.CANADA_FRENCH, "Date de appointement")
        map.put(Locale.FRENCH, "Date de rendez-vous")
        map.put(Locale.forLanguageTag("nl"), "Datum van afspraak")
        map.put(Locale.forLanguageTag(""), "Appointment date")
        def question = new Question(map)

                expect:
        question.getTranslatedText(Locale.forLanguageTag(locale)).get() == translatedText

        where:
        locale  | translatedText
        "fr-CA" | "Date de appointement"
        "fr-FR" | "Date de rendez-vous"
        "fr"    | "Date de rendez-vous"
        "jp"    | "Appointment date"
        "nl-BE" | "Datum van afspraak"
    }

    def "if there is no matching translated text return an empty translated text"() {
        given: "a question with no default (empty language tag) translated text"
        def map = new HashMap<Locale,String>()
        map.put(Locale.CANADA_FRENCH, "Date de appointement")
        map.put(Locale.FRENCH, "Date de rendez-vous")
        map.put(Locale.forLanguageTag("nl"), "Datum van afspraak")
        def question = new Question(map)
        when: "I request the translated text for a language without available translation"
        def translatedText = question.getTranslatedText(Locale.ENGLISH)

        then: "empty is returned"
        translatedText == Optional.empty()
    }
}
