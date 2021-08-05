package com.custodix.insite.mongodb.export.patient.domain.model

import com.custodix.insite.mongodb.export.patient.domain.model.demographic.BirthInformation
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.DeathInformation
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.PatientGender
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.VitalStatus
import com.custodix.insite.mongodb.vocabulary.SubjectId
import groovy.json.JsonSlurper
import spock.lang.Specification

import java.time.LocalDate
import java.time.ZoneOffset

class DemographicFactSpec extends Specification {

    public static final SubjectId SUBJECT_ID = SubjectId.of("subject")

    def "Can generate Mongo Document for PatientGender"() {
        given: "A PatientGender 'FEMALE'"
        PatientGender gender = PatientGender.newBuilder()
                .withInterpretedValue(PatientGender.Gender.FEMALE)
                .build()

        when: "I retrieve the MongoDocument representation, for a subject 'subject'"
        def result = gender.toDemographicDocumentFor(SUBJECT_ID)

        then: "I expect valid JSON to be generated"
        def slurper = new JsonSlurper()
        def json = slurper.parseText(result)
        json instanceof Map
        and: "I expect the subjectId to be 'subject'"
        json.subjectId == 'subject'
        and: "I expect the demographicType to be 'GENDER'"
        json.demographicType == 'GENDER'
        and: "I expect the value to be 'female'"
        json.value == 'female'
    }

    def "Can generate Mongo Document for PatientGender without interpreted value"() {
        given: "A PatientGender without interpreted value"
        PatientGender gender = PatientGender.newBuilder()
                .build()

        when: "I retrieve the MongoDocument representation, for a subject 'subject'"
        def result = gender.toDemographicDocumentFor(SUBJECT_ID)

        then: "I expect valid JSON to be generated"
        def slurper = new JsonSlurper()
        def json = slurper.parseText(result)
        json instanceof Map
        and: "I expect the subjectId to be 'subject'"
        json.subjectId == 'subject'
        and: "I expect the demographicType to be 'GENDER'"
        json.demographicType == 'GENDER'
        and: "I expect the value to be empty: ''"
        json.value == ''
    }

    def "Can generate Mongo Document for BirthInformation"() {
        given: "BirthInformation with a birthdate of 10/12/2018"
        BirthInformation birthInformation = BirthInformation.newBuilder()
                .withBirthDate(LocalDate.of(2018, 12, 10)
                        .atStartOfDay()
                        .toInstant(ZoneOffset.UTC))
                .build()

        when: "I retrieve the MongoDocument representation, for a subject 'subject'"
        def result = birthInformation.toDemographicDocumentFor(SUBJECT_ID)

        then: "I expect valid JSON to be generated"
        def slurper = new JsonSlurper()
        def json = slurper.parseText(result)
        json instanceof Map
        and: "I expect the subjectId to be 'subject'"
        json.subjectId == 'subject'
        and: "I expect the demographicType to be 'BIRTH_DATE'"
        json.demographicType == 'BIRTH_DATE'
        and: "I expect the value to be '2018-12-10'"
        json.value == '2018-12-10'
    }

    def "Can generate Mongo Document for BirthInformation without birthdate"() {
        given: "BirthInformation without a birthdate"
        BirthInformation birthInformation = BirthInformation.newBuilder().build()

        when: "I retrieve the MongoDocument representation, for a subject 'subject'"
        def result = birthInformation.toDemographicDocumentFor(SUBJECT_ID)

        then: "I expect valid JSON to be generated"
        def slurper = new JsonSlurper()
        def json = slurper.parseText(result)
        json instanceof Map
        and: "I expect the subjectId to be 'subject'"
        json.subjectId == 'subject'
        and: "I expect the demographicType to be 'BIRTH_DATE'"
        json.demographicType == 'BIRTH_DATE'
        and: "I expect the value to be empty: ''"
        json.value == ''
    }

    def "Can generate Mongo Document for DeathInformation"() {
        given: "DeathInformation with a deathdate of 31/03/2019"
        DeathInformation deathInformation = DeathInformation.newBuilder()
                .withDeathDate(LocalDate.of(2019, 3, 31)
                        .atStartOfDay()
                        .toInstant(ZoneOffset.UTC))
                .build()

        when: "I retrieve the MongoDocument representation, for a subject 'subject'"
        def result = deathInformation.toDemographicDocumentFor(SUBJECT_ID)

        then: "I expect valid JSON to be generated"
        def slurper = new JsonSlurper()
        def json = slurper.parseText(result)
        json instanceof Map
        and: "I expect the subjectId to be 'subject'"
        json.subjectId == 'subject'
        and: "I expect the demographicType to be 'DEATH_DATE'"
        json.demographicType == 'DEATH_DATE'
        and: "I expect the value to be '2019-03-31'"
        json.value == '2019-03-31'
    }

    def "Can generate Mongo Document for DeathInformation without deathDate"() {
        given: "DeathInformation without a date of death"
        DeathInformation deathInformation = DeathInformation.newBuilder().build()

        when: "I retrieve the MongoDocument representation, for a subject 'subject'"
        def result = deathInformation.toDemographicDocumentFor(SUBJECT_ID)

        then: "I expect valid JSON to be generated"
        def slurper = new JsonSlurper()
        def json = slurper.parseText(result)
        json instanceof Map
        and: "I expect the subjectId to be 'subject'"
        json.subjectId == 'subject'
        and: "I expect the demographicType to be 'DEATH_DATE'"
        json.demographicType == 'DEATH_DATE'
        and: "I expect the value to be empty"
        json.value == ''
    }

    def "Can generate Mongo Document for VitalStatus"() {
        given: "A VitalStatus with status value"
        VitalStatus vitalStatus = VitalStatus.newBuilder()
                .withStatus(VitalStatus.Status.DECEASED)
                .build()

        when: "I retrieve the MongoDocument representation, for a subject 'subject'"
        def result = vitalStatus.toDemographicDocumentFor(SUBJECT_ID)

        then: "I expect valid JSON to be generated"
        def slurper = new JsonSlurper()
        def json = slurper.parseText(result)
        json instanceof Map
        and: "I expect the subjectId to be 'subject'"
        json.subjectId == 'subject'
        and: "I expect the demographicType to be 'VITAL_STATUS'"
        json.demographicType == 'VITAL_STATUS'
        and: "I expect the value to be 'true'"
        json.value == 'DECEASED'
    }

    def "Can generate Mongo Document for VitalStatus without value"() {
        given: "A VitalStatus without value"
        VitalStatus vitalStatus = VitalStatus.newBuilder()
                .build()

        when: "I retrieve the MongoDocument representation, for a subject 'subject'"
        def result = vitalStatus.toDemographicDocumentFor(SUBJECT_ID)

        then: "I expect valid JSON to be generated"
        def slurper = new JsonSlurper()
        def json = slurper.parseText(result)
        json instanceof Map
        and: "I expect the subjectId to be 'subject'"
        json.subjectId == 'subject'
        and: "I expect the demographicType to be 'VITAL_STATUS'"
        json.demographicType == 'VITAL_STATUS'
        and: "I expect no value"
        json.value == "null"
    }

}
