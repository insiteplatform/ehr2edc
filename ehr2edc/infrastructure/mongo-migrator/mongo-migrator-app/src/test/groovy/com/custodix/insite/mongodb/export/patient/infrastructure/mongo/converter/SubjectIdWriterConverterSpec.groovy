package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.converter

import com.custodix.insite.mongodb.export.patient.domain.exceptions.DomainException
import com.custodix.insite.mongodb.vocabulary.SubjectId
import spock.lang.Specification
import spock.lang.Unroll

class SubjectIdWriterConverterSpec extends Specification {

    private SubjectIdWriterConverter subjectIdConverter = new SubjectIdWriterConverter()

    @Unroll
    def "Convert subject id correctly to string"(String subjectIdValue) {
        given: "Subject id with value #subjectIdValue"
        SubjectId subjectId = SubjectId.of(subjectIdValue)

        when: "converting"
        def convertedSubjectId = subjectIdConverter.convert(subjectId)

        then: "the converted subject id value is #subjectIdValue"
        convertedSubjectId == subjectIdValue

        where:
        subjectIdValue  | _
        "123-5678"      | _
    }

    @Unroll
    def "Throw exception when the subject id is null"() {
        given: "Subject id is null"
        SubjectId subjectId = null

        when: "converting"
        subjectIdConverter.convert(subjectId)

        then: "a domain exception is thrown"
        DomainException domainException = thrown(DomainException)
        domainException.getMessage() == "Cannot convert a null subject id."
    }
}
