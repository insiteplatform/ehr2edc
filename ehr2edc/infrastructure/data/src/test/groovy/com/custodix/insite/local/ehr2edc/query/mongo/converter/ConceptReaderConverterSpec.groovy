package com.custodix.insite.local.ehr2edc.query.mongo.converter

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException
import org.bson.Document
import spock.lang.Specification

class ConceptReaderConverterSpec extends Specification {

    ConceptReaderConverter conceptReaderConverter = new ConceptReaderConverter()

    def "Convert concept document to concept"() {
        given: "Concept document"
        Document conceptDocument = Document.parse("{ code: '123-123'}")

        when: "converting document"
        ConceptCode concept = conceptReaderConverter.convert(conceptDocument)

        then: "the concept is correctly converted"
        concept.getCode() == '123-123'
    }

    def "throw an exception when the document is invalid" (Document conceptDocument) {
        given: "Concept document #conceptDocument"

        when: "converting document"
        conceptReaderConverter.convert(conceptDocument)

        then: "an domain exception is thrown"
        Exception  exception = thrown(SystemException)
        exception.getMessage() == "Cannot convert a null or blank concept document."

        where:
        conceptDocument                           | _
        null                                      | _
        Document.parse("{ code: '   '}")    | _
        Document.parse("{ code: ''}")       | _
        Document.parse("{ code: null}")       | _
    }
}
