package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patient

import com.custodix.insite.mongodb.export.patient.domain.model.PatientFact
import com.custodix.insite.mongodb.export.patient.domain.model.PatientRecord
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.BirthInformation
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.DeathInformation
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.PatientGender.Gender
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.VitalStatus
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

import java.sql.Timestamp
import java.time.Instant

import static com.custodix.insite.mongodb.export.patient.domain.model.demographic.PatientGender.Gender.*

@Title("Processing a patient record")
class PatientRecordProcessorSpec extends Specification {
    private static final Instant NOW = Instant.now()
    private static final String FEMALE_CODE = "F"
    private static final String MALE_CODE = "M"
    private static final String OTHER_GENDER_CODE = "O"
    private static final String PATIENT_ID = "1"
    private static final String NAMESPACE = "namespace"

    private final PatientExportGenderCodesSettings patientExportSettings = createPatientExportSettings()
    private PatientRecordProcessor patientRecordProcessor

    void setup() {
        patientRecordProcessor = new PatientRecordProcessor(this.patientExportSettings)
    }

    def "The patient identifier gets processed well"() {
        given: "A patient record"
        PatientRecord record = buildPatientRecord(FEMALE_CODE, "")

        when: "The record gets processed"
        PatientFact fact = patientRecordProcessor.process(record)

        then: "The patient identifier matches"
        verifyAll(fact) {
            identifier != null
            identifier.patientId.id == PATIENT_ID
            identifier.namespace.name == NAMESPACE
        }
    }

    @Unroll("Interpret gender code #genderCode into #expectedGender")
    def "Gender code interpretation"(String genderCode, Gender expectedGender) {
        given: "A patient record with a #genderCode gender code"
        PatientRecord record = buildPatientRecord(genderCode, "")

        when: "The record gets processed"
        PatientFact fact = patientRecordProcessor.process(record)

        then: "The patient is marked as #expectedGender"
        verifyAll(fact) {
            gender.interpretedValue == expectedGender
        }

        where:
        genderCode        || expectedGender
        MALE_CODE         || MALE
        FEMALE_CODE       || FEMALE
        OTHER_GENDER_CODE || UNKNOWN
    }

    @Unroll("Processing birth information with the vital status set to '#vitalStatus'")
    def "Birth date information gets interpreted correctly"(
            String vitalStatus, BirthInformation.Accuracy expectedAccuracy) {
        given: "A patient record"
        PatientRecord record = buildPatientRecord(FEMALE_CODE, vitalStatus)

        when: "The record gets processed"
        PatientFact fact = patientRecordProcessor.process(record)

        then: "The birth accuracy matches #expectedAccuracy"
        verifyAll(fact) {
            vitalStatus == vitalStatus
            birthInformation != null
            birthInformation.birthDate == NOW
            birthInformation.accuracy == expectedAccuracy
        }

        where:
        vitalStatus                                             || expectedAccuracy
        "*" + BirthInformation.Accuracy.UNSPECIFIED.code        || BirthInformation.Accuracy.UNSPECIFIED
        "*" + BirthInformation.Accuracy.BIRTH_DATE_UNKNOWN.code || BirthInformation.Accuracy.BIRTH_DATE_UNKNOWN
        "*" + BirthInformation.Accuracy.DAY.code                || BirthInformation.Accuracy.DAY
        "*" + BirthInformation.Accuracy.HOUR.code               || BirthInformation.Accuracy.HOUR
        "*" + BirthInformation.Accuracy.MINUTE.code             || BirthInformation.Accuracy.MINUTE
        "*" + BirthInformation.Accuracy.MONTH.code              || BirthInformation.Accuracy.MONTH
        "*" + BirthInformation.Accuracy.SECOND.code             || BirthInformation.Accuracy.SECOND
        "*" + BirthInformation.Accuracy.YEAR.code               || BirthInformation.Accuracy.YEAR
        BirthInformation.Accuracy.YEAR.code                     || BirthInformation.Accuracy.UNSPECIFIED
        ""                                                      || BirthInformation.Accuracy.UNSPECIFIED
        "Mini"                                                  || BirthInformation.Accuracy.MINUTE
    }

    @Unroll("Processing death information with the vital status set to '#givenVitalStatus'")
    def "Death date information is interpreted correctly"(String givenVitalStatus,
                                                          DeathInformation.Accuracy expectedAccuracy,
                                                          VitalStatus.Status expectedDeceased) {
        given: "A patient record"
        PatientRecord record = buildPatientRecord(FEMALE_CODE, givenVitalStatus)

        when: "The record gets processed"
        PatientFact fact = patientRecordProcessor.process(record)

        then: "The death accuracy matches #expectedAccuracy"
        verifyAll(fact) {
            deathInformation != null
            deathInformation.deathDate == NOW
            deathInformation.accuracy == expectedAccuracy
        }
        and: "The status status is #expectedDeceased"
        fact.vitalStatus.status == expectedDeceased

        where:
        givenVitalStatus                                       || expectedAccuracy                                  | expectedDeceased
        DeathInformation.Accuracy.UNSPECIFIED.code             || DeathInformation.Accuracy.UNSPECIFIED             | VitalStatus.Status.UNKNOWN
        DeathInformation.Accuracy.PATIENT_ALIVE.code           || DeathInformation.Accuracy.PATIENT_ALIVE           | VitalStatus.Status.ALIVE
        DeathInformation.Accuracy.UNKNOWN.code                 || DeathInformation.Accuracy.UNKNOWN                 | VitalStatus.Status.DECEASED
        DeathInformation.Accuracy.DECEASED_STATUS_UNKNOWN.code || DeathInformation.Accuracy.DECEASED_STATUS_UNKNOWN | VitalStatus.Status.UNKNOWN
        DeathInformation.Accuracy.DAY.code                     || DeathInformation.Accuracy.DAY                     | VitalStatus.Status.DECEASED
        DeathInformation.Accuracy.HOUR.code                    || DeathInformation.Accuracy.HOUR                    | VitalStatus.Status.DECEASED
        DeathInformation.Accuracy.MINUTE.code                  || DeathInformation.Accuracy.MINUTE                  | VitalStatus.Status.DECEASED
        DeathInformation.Accuracy.MONTH.code                   || DeathInformation.Accuracy.MONTH                   | VitalStatus.Status.DECEASED
        DeathInformation.Accuracy.SECOND.code                  || DeathInformation.Accuracy.SECOND                  | VitalStatus.Status.DECEASED
        DeathInformation.Accuracy.YEAR.code                    || DeathInformation.Accuracy.YEAR                    | VitalStatus.Status.DECEASED
        ""                                                     || DeathInformation.Accuracy.UNSPECIFIED             | VitalStatus.Status.UNKNOWN
        "Mini"                                                 || DeathInformation.Accuracy.MONTH                   | VitalStatus.Status.DECEASED
    }

    def "Patient record without a death date gets processed well"() {
        given: "A patient record without a death date"
        PatientRecord record = buildAlivePatientRecord()

        when: "The record gets processed"
        PatientFact fact = patientRecordProcessor.process(record)

        then: "The death accuracy matches 'N', without date of death"
        verifyAll(fact) {
            deathInformation != null
            deathInformation.deathDate == null
            deathInformation.accuracy == DeathInformation.Accuracy.PATIENT_ALIVE
        }
        and: "The vitalStatus is ALIVE"
        fact.vitalStatus.status == VitalStatus.Status.ALIVE
    }

    private PatientExportGenderCodesSettings createPatientExportSettings() {
        def patientExportSettings = new PatientExportGenderCodesSettingsProperties()
        patientExportSettings.setFemale(FEMALE_CODE)
        patientExportSettings.setMale(MALE_CODE)
        patientExportSettings
    }

    private PatientRecord buildPatientRecord(String genderCode, String vitalStatus) {
        PatientRecord.newBuilder().withId(PATIENT_ID)
                .withNamespace(NAMESPACE)
                .withBirthDate(new Timestamp(NOW.toEpochMilli()))
                .withDeathDate(new Timestamp(NOW.toEpochMilli()))
                .withGender(genderCode)
                .withVitalStatus(vitalStatus)
                .build()
    }

    private PatientRecord buildAlivePatientRecord() {
        PatientRecord.newBuilder().withId(PATIENT_ID)
                .withBirthDate(new Timestamp(NOW.toEpochMilli()))
                .withGender(FEMALE_CODE)
                .withVitalStatus("" + DeathInformation.Accuracy.PATIENT_ALIVE.code)
                .build()
    }
}