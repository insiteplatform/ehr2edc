package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.laboratory

import com.custodix.insite.mongodb.export.patient.domain.model.FastingStatus
import com.custodix.insite.mongodb.export.patient.domain.model.LaboratoryFact
import com.custodix.insite.mongodb.export.patient.domain.model.LaboratoryObservationFactRecord
import com.custodix.insite.mongodb.export.patient.domain.model.common.Concept
import com.custodix.insite.mongodb.export.patient.domain.model.common.ConceptPath
import com.custodix.insite.mongodb.export.patient.domain.model.common.ObservationType
import com.custodix.insite.mongodb.export.patient.domain.model.labvalue.LaboratoryConceptInfo
import com.custodix.insite.mongodb.export.patient.domain.repository.ConceptPathRepository
import com.custodix.insite.mongodb.export.patient.domain.repository.LaboratoryConceptInfoRepository
import spock.lang.Specification

import java.sql.Timestamp
import java.time.Instant

import static java.util.Arrays.asList

class LaboratoryObservationFactProcessorSpec extends Specification {
    private static final String PATIENT_ID = "1"
    private static final String NAMESPACE = "namespace"
    private static final Instant NOW = Instant.now()
    private static final String UNIT = "unit"
    private static final BigDecimal VALUE = 5.0
    private static final BigDecimal ULN_VALUE = 6.0
    private static final BigDecimal LLN_VALUE = 4.0
    private static final String CONCEPT_CODE = "code"
    private static final String CONCEPT_PATH = "LOINC_a\\LOINC_b\\LOINC_c\\LOINC_code"
    private static final String CONCEPT_SCHEMA = "LOINC"
    private static final String CONCEPT_LABEL = "Test concept label"
    private static final String CONCEPT_SPECIMEN = "Test specimen"
    private static final String CONCEPT_METHOD = "Test method"
    private static final FastingStatus CONCEPT_FASTING_STATUS = FastingStatus.UNDEFINED

    private LaboratoryObservationFactProcessor processor

    void setup() {
        ConceptPathRepository conceptPathRepository = buildLaboratoryConceptsRepository()
        LaboratoryConceptInfoRepository laboratoryConceptInfoRepository = buildLaboratoryConceptInfoRepository()
        LaboratoryExportSettings laboratoryExportSettings = new LaboratoryExportSettings()
        laboratoryExportSettings.conceptNamespace = CONCEPT_SCHEMA
        processor = new LaboratoryObservationFactProcessor(conceptPathRepository, laboratoryConceptInfoRepository, laboratoryExportSettings)
    }

    def "General properties are processed well"() {
        given: "A laboratory fact record"
        LaboratoryObservationFactRecord record = buildLaboratoryObservationFactRecord()

        when: "That record gets processed"
        LaboratoryFact fact = processor.process(record)

        then: "The general properties of the fact record gets processed correctly"
        verifyAll(fact) {
            type == "laboratory"
            patientIdentifier != null
            patientIdentifier.patientId.id == PATIENT_ID
            patientIdentifier.namespace.name == NAMESPACE
            startDate == NOW
            endDate == NOW
        }
    }

    def "Laboratory observation values gets processed well"() {
        given: "A laboratory fact record"
        LaboratoryObservationFactRecord record = buildLaboratoryObservationFactRecord()

        when: "That record gets processed"
        LaboratoryFact fact = processor.process(record)

        then: "The general properties of the fact record gets processed correctly"
        verifyAll(fact) {
            valueObservation != null
            verifyAll(valueObservation) {
                type == ObservationType.NUMERIC
                values.size() == 1
                values.get(0).unit == UNIT
                values.get(0).value == VALUE
            }
            llnObservation != null
            verifyAll(llnObservation) {
                llnObservation.type == ObservationType.NUMERIC
                llnObservation.values.size() == 1
                llnObservation.values.get(0).unit == UNIT
                llnObservation.values.get(0).value == LLN_VALUE
            }
            ulnObservation != null
            verifyAll(ulnObservation) {
                ulnObservation.type == ObservationType.NUMERIC
                ulnObservation.values.size() == 1
                ulnObservation.values.get(0).unit == UNIT
                ulnObservation.values.get(0).value == ULN_VALUE
            }
        }
    }

    def "Concepts are processed well"() {
        given: "A laboratory fact record"
        LaboratoryObservationFactRecord record = buildLaboratoryObservationFactRecord()

        when: "That record gets processed"
        LaboratoryFact fact = processor.process(record)

        then: "The conceptPaths of the fact record gets processed correctly"
        verifyAll(fact) {
            verifyAll(referenceConcept) {
                code == CONCEPT_CODE
                schema == CONCEPT_SCHEMA
            }
            verifyAll(localConcept) {
                code == CONCEPT_CODE
                schema == CONCEPT_SCHEMA
            }
            conceptPaths.size() == 1
            pathContainsConcept(conceptPaths.get(0))
            label == CONCEPT_LABEL
            conceptInfo != null
            verifyAll(conceptInfo) {
                specimenValue == CONCEPT_SPECIMEN
                methodValue.present
                methodValue.get() == CONCEPT_METHOD
                fastingStatus == CONCEPT_FASTING_STATUS
            }
        }
    }

    ConceptPathRepository buildLaboratoryConceptsRepository() {
        return { code -> asList(buildConceptPath()) }
    }

    ConceptPath buildConceptPath() {
        ConceptPath.newBuilder()
                .withPath(CONCEPT_PATH)
                .build()
    }

    LaboratoryConceptInfoRepository buildLaboratoryConceptInfoRepository() {
        return { concept -> Optional.of(buildLaboratoryConceptInfo()) }
    }

    LaboratoryConceptInfo buildLaboratoryConceptInfo() {
        LaboratoryConceptInfo.newBuilder()
                .withSpecimenValue(CONCEPT_SPECIMEN)
                .withMethodValue(CONCEPT_METHOD)
                .withFastingStatus(CONCEPT_FASTING_STATUS)
                .build()
    }

    List<Concept> buildMappedConcepts() {
        Concept concept = Concept.newBuilder()
                .withCode(CONCEPT_CODE)
                .withSchema(CONCEPT_SCHEMA)
                .build()
        return asList(concept)
    }

    LaboratoryObservationFactRecord buildLaboratoryObservationFactRecord() {
        LaboratoryObservationFactRecord.newBuilder()
                .withPatientNum(PATIENT_ID)
                .withPatientMasterIndex(NAMESPACE)
                .withStartDate(new Timestamp(NOW.toEpochMilli()))
                .withEndDate(new Timestamp(NOW.toEpochMilli()))
                .withConceptCD(CONCEPT_CODE)
                .withConceptLabel(CONCEPT_LABEL)
                .withValue(VALUE)
                .withUlnValue(ULN_VALUE)
                .withLlnValue(LLN_VALUE)
                .withUnit(UNIT)
                .build()
    }

    void pathContainsConcept(ConceptPath conceptPath) {
        verifyAll(conceptPath) {
            path == CONCEPT_PATH
            concepts.size() == 4
            concepts.get(3).code == CONCEPT_CODE
            concepts.get(3).schema == CONCEPT_SCHEMA
        }
    }
}