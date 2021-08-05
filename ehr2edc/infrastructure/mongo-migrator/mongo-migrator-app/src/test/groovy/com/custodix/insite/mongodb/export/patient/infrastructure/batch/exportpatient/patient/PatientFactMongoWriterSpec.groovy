package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patient

import com.custodix.insite.mongodb.export.patient.domain.model.PatientFact
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.BirthInformation
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.DeathInformation
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.PatientGender
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.VitalStatus
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.demographic.DemographicDocument
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.data.mongodb.core.MongoTemplate
import spock.lang.Specification
import spock.lang.Title

import java.time.LocalDate
import java.time.ZoneOffset

@Title("Writing the patient fact to the Demographic collection")
class PatientFactMongoWriterSpec extends Specification {

    MongoTemplate mongoTemplate = Mock()

    PatientFactMongoWriter writer = new PatientFactMongoWriter(mongoTemplate)

    def "write PatientFact without subjectId"() {
        given: "An identifier without subjectId"
        PatientFact fact = PatientFact.newBuilder()
                .build()

        when: "I write the fact"
        writer.write([fact])

        then: "An exception is raised, indicating that SubjectId is required"
        def ex = thrown(IllegalArgumentException)
        ex.getMessage() == "SubjectId is required for generation a Mongo Document."

        where:
        identifier << [null, PatientIdentifier.of(null, null, null)]
    }

    def "persists individual Demographic documents for PatientFact"() {
        given: "A patient fact containing gender and birth information"
        PatientGender gender = PatientGender.newBuilder()
                .withInterpretedValue(PatientGender.Gender.FEMALE)
                .build()
        BirthInformation dob = BirthInformation.newBuilder()
                .withBirthDate(LocalDate.of(1988, 6, 16).atStartOfDay().toInstant(ZoneOffset.UTC))
                .build()
        PatientFact fact = PatientFact.newBuilder()
                .withIdentifier(PatientIdentifier.of(null, null, SubjectId.of("SUBJ")))
                .withBirthInformation(dob)
                .withGender(gender)
                .build()
        def doc1
        def doc2

        when: "I write the fact"
        writer.write([fact])

        then: "I expect 2 calls to the database"
        1 * mongoTemplate.insert(_, DemographicDocument.COLLECTION) >> { arguments -> doc1 = arguments[0] }
        doc1 instanceof DemographicDocument
        doc1.subjectId.id == "SUBJ"
        doc1.demographicType == "GENDER"
        doc1.value == PatientGender.Gender.FEMALE.toString()
        1 * mongoTemplate.insert(_, DemographicDocument.COLLECTION) >> { arguments -> doc2 = arguments[0] }
        doc2 instanceof DemographicDocument
        doc2.subjectId.id == "SUBJ"
        doc2.demographicType == "BIRTH_DATE"
        doc2.value == "1988-06-16"
        0 * _
    }

    def "persists Gender for PatientFact"() {
        given: "A patient fact containing gender"
        PatientGender gender = PatientGender.newBuilder()
                .withInterpretedValue(PatientGender.Gender.FEMALE)
                .build()
        PatientFact fact = PatientFact.newBuilder()
                .withIdentifier(PatientIdentifier.of(null, null, SubjectId.of("SUBJ")))
                .withGender(gender)
                .build()
        def document

        when: "I write the fact"
        writer.write([fact])

        then: "I expect a demographic document for gender to be persisted"
        1 * mongoTemplate.insert(_, DemographicDocument.COLLECTION)>> { arguments -> document = arguments[0] }
        0 * _
        document instanceof DemographicDocument
        document.subjectId.id == "SUBJ"
        document.demographicType == "GENDER"
        document.value == PatientGender.Gender.FEMALE.toString()
    }

    def "persists BirthInformation for PatientFact"() {
        given: "A patient fact containing birth information"
        BirthInformation birthInformation = BirthInformation.newBuilder()
                .withBirthDate(LocalDate.of(1988, 6, 16).atStartOfDay().toInstant(ZoneOffset.UTC))
                .withAccuracy(BirthInformation.Accuracy.DAY)
                .build()
        PatientFact fact = PatientFact.newBuilder()
                .withIdentifier(PatientIdentifier.of(null, null, SubjectId.of("SUBJ")))
                .withBirthInformation(birthInformation)
                .build()
        def document

        when: "I write the fact"
        writer.write([fact])

        then: "I expect a demographic document for birth information to be persisted"
        1 * mongoTemplate.insert(_, DemographicDocument.COLLECTION) >> { arguments -> document = arguments[0] }
        0 * _
        document instanceof DemographicDocument
        document.subjectId.id == "SUBJ"
        document.demographicType == "BIRTH_DATE"
        document.value == "1988-06-16"
    }

    def "persists DeathInformation for PatientFact"() {
        given: "A patient fact containing death information"
        DeathInformation deathInformation = DeathInformation.newBuilder()
                .withDeathDate(LocalDate.of(1988, 6, 16).atStartOfDay().toInstant(ZoneOffset.UTC))
                .withAccuracy(DeathInformation.Accuracy.DAY)
                .build()
        PatientFact fact = PatientFact.newBuilder()
                .withIdentifier(PatientIdentifier.of(null, null, SubjectId.of("SUBJ")))
                .withDeathInformation(deathInformation)
                .build()
        def document

        when: "I write the fact"
        writer.write([fact])

        then: "I expect a demographic document for death information to be persisted"
        1 * mongoTemplate.insert(_, DemographicDocument.COLLECTION) >> { arguments -> document = arguments[0] }
        0 * _
        document instanceof DemographicDocument
        document.subjectId.id == "SUBJ"
        document.demographicType == "DEATH_DATE"
        document.value == "1988-06-16"
    }

    def "persists VitalStatus for PatientFact"() {
        given: "A patient fact containing a vitalstatus"
        VitalStatus vitalStatus = VitalStatus.newBuilder()
                .withStatus(VitalStatus.Status.UNKNOWN)
                .build()
        PatientFact fact = PatientFact.newBuilder()
                .withIdentifier(PatientIdentifier.of(null, null, SubjectId.of("SUBJ")))
                .withVitalStatus(vitalStatus)
                .build()
        def document

        when: "I write the fact"
        writer.write([fact])

        then: "I expect a demographic document for vitalstatus to be persisted"
        1 * mongoTemplate.insert(_, DemographicDocument.COLLECTION)>> { arguments -> document = arguments[0] }
        0 * _
        document instanceof DemographicDocument
        document.subjectId.id == "SUBJ"
        document.demographicType == "VITAL_STATUS"
        document.value == VitalStatus.Status.UNKNOWN.name()
    }
}
