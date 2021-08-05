package com.custodix.insite.mongodb.export.patient.infrastructure.mongo.converter

import com.custodix.insite.mongodb.export.patient.domain.exceptions.DomainException
import com.custodix.insite.mongodb.vocabulary.SubjectId
import spock.lang.Specification
import spock.lang.Unroll

class SubjectIdReaderConverterSpec extends Specification {

    private SubjectIdReaderConverter subjectIdConverter = new SubjectIdReaderConverter()

    @Unroll
    def "Convert string correctly to subjectId"(String subjectIdValue) {
        given: "Subject id with value #subjectIdValue"

        when: "converting"
        def convertedSubjectId = subjectIdConverter.convert(subjectIdValue)

        then: "the converted subject id value is #subjectIdValue"
        convertedSubjectId == SubjectId.of(subjectIdValue)

        where:
        subjectIdValue  | _
        "123-5678"      | _
    }

    @Unroll
    def "Throw exception when the subject id value is null or blank"(String subjectIdValue) {
        given: "Subject id with value #subjectIdValue"

        when: "converting"
        subjectIdConverter.convert(subjectIdValue)

        then: "a domain exception is thrown"
        DomainException domainException = thrown(DomainException)
        domainException.getMessage() == "Cannot convert a null or blank subject id value."

        where:
        subjectIdValue  | _
        null            | _
        "   "           | _
        ""              | _
    }
}
