package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.clinicalfinding

import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFactRecord
import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFindingFact
import com.custodix.insite.mongodb.export.patient.domain.model.ClinicalFindingItem
import com.custodix.insite.mongodb.export.patient.domain.model.common.ConceptPath
import com.custodix.insite.mongodb.export.patient.domain.model.common.Modifier
import com.custodix.insite.mongodb.export.patient.domain.model.common.ModifierObjectMother
import spock.lang.Specification

import java.sql.Timestamp
import java.time.Instant

import static java.util.Arrays.asList

class ClinicalFindingItemProcessorTest extends Specification {
    private static final String PATIENT_ID = "PATIENT-123"
    private static final String NAMESPACE = "TEST-NAMESPACE"
    private static final Instant NOW = Instant.now()
    private static final BigDecimal OBSERVATION_VALUE = 1.0
    private static final String OBSERVATION_UNIT = "UNIT"
    private static final BigDecimal ULN_VALUE = 1.5
    private static final BigDecimal LLN_VALUE = 0.5
    private static final String CONCEPT_SCHEMA = "TEST-SCHEMA"
    private static final String CONCEPT_CODE = "code"
    private static final String CONCEPT_LABEL = "Test label"
    private static final String CONCEPT_PATH = "TEST-SCHEMA_a\\TEST-SCHEMA_b\\TEST-SCHEMA_c\\TEST-SCHEMA_code"
    private static final Modifier LATERALITY = ModifierObjectMother.laterality()
    private static final Modifier LOCATION = ModifierObjectMother.location()
    private static final Modifier POSITION = ModifierObjectMother.position()
    private static final Map<String, Optional<Modifier>> MODIFIERS = createModifiers()

    private ClinicalFindingItemProcessor processor

    void setup() {
        ClinicalFindingExportSettings exportSettings = new ClinicalFindingExportSettings()
        exportSettings.setConceptNamespaces(asList(CONCEPT_SCHEMA))
        processor = new ClinicalFindingItemProcessor({ code -> buildConceptPaths() }, { code -> getModifier(code) }, exportSettings)
    }

    def "An item patient identifier gets processed correctly"() {
        given: "A clinical finding item"
        ClinicalFindingItem item = buildItem()

        when: "The item gets processed"
        ClinicalFindingFact fact = processor.process(item)

        then: "The patient identifier is processed correctly"
        verifyAll(fact) {
            patientIdentifier.patientId.id == PATIENT_ID
            patientIdentifier.namespace.name == NAMESPACE
        }
    }

    def "An item effective date gets processed correctly"() {
        given: "A clinical finding item"
        ClinicalFindingItem item = buildItem()

        when: "The item gets processed"
        ClinicalFindingFact fact = processor.process(item)

        then: "The effective date get processed correctly"
        verifyAll(fact) {
            effectiveDate == NOW
        }
    }

    def "An item label gets processed correctly"() {
        given: "A clinical finding item"
        ClinicalFindingItem item = buildItem()

        when: "The item gets processed"
        ClinicalFindingFact fact = processor.process(item)

        then: "The fact label is processed correctly"
        verifyAll(fact) {
            label == CONCEPT_LABEL
        }
    }

    def "An item observations gets processed correctly"() {
        given: "A clinical finding item"
        ClinicalFindingItem item = buildItem()

        when: "The item gets processed"
        ClinicalFindingFact fact = processor.process(item)

        then: "The observations are processed correctly"
        verifyAll(fact) {
            valueObservation.values.size() == 1
            verifyAll(valueObservation.values.get(0)) {
                value == OBSERVATION_VALUE
                unit == OBSERVATION_UNIT
            }
            ulnObservation.values.size() == 1
            verifyAll(ulnObservation.values.get(0)) {
                value == ULN_VALUE
                unit == OBSERVATION_UNIT
            }
            llnObservation.values.size() == 1
            verifyAll(llnObservation.values.get(0)) {
                value == LLN_VALUE
                unit == OBSERVATION_UNIT
            }
        }
    }

    def "An item concepts gets processed correctly"() {
        given: "A clinical finding item"
        ClinicalFindingItem item = buildItem()

        when: "The item gets processed"
        ClinicalFindingFact fact = processor.process(item)

        then: "The concepts are processed correctly"
        verifyAll(fact) {
            conceptPaths.size() == 1
            verifyAll(conceptPaths.get(0)) {
                path == CONCEPT_PATH
                concepts.size() == 4
                concepts.get(3).code == CONCEPT_CODE
                concepts.get(3).schema == CONCEPT_SCHEMA
            }
            localConcept.schema == CONCEPT_SCHEMA
            localConcept.code == CONCEPT_CODE
            referenceConcept.schema == CONCEPT_SCHEMA
            referenceConcept.code == CONCEPT_CODE
        }
    }

    def "An item modifiers get processed correctly"() {
        given: "A clinical finding item"
        ClinicalFindingItem item = buildItem()

        when: "The item gets processed"
        ClinicalFindingFact fact = processor.process(item)

        then: "The modifiers are processed correctly"
        verifyAll(fact.laterality.get()) {
            category == LATERALITY.category
            referenceCode == LATERALITY.referenceCode
        }
        verifyAll(fact.position.get()) {
            category == POSITION.category
            referenceCode == POSITION.referenceCode
        }
        verifyAll(fact.location.get()) {
            category == LOCATION.category
            referenceCode == LOCATION.referenceCode
        }
    }

    def "An item without modifiers gets processed correctly"() {
        given: "A clinical finding item without modifiers"
        ClinicalFindingItem item = buildItemWithoutModifiers()

        when: "The item gets processed"
        ClinicalFindingFact fact = processor.process(item)

        then: "The fact has no modifiers"
        verifyAll(fact) {
            !laterality.present
            !position.present
            !location.present
        }
    }

    List<ConceptPath> buildConceptPaths() {
        ConceptPath conceptPath = ConceptPath.newBuilder()
                .withPath(CONCEPT_PATH)
                .build()
        asList(conceptPath)
    }

    Optional<Modifier> getModifier(String modifierCode) {
        return MODIFIERS.get(modifierCode)
    }

    ClinicalFindingItem buildItem() {
        def item = buildItemWithoutModifiers()
        item.addModifierCode(LATERALITY.referenceCode)
        item.addModifierCode(LOCATION.referenceCode)
        item.addModifierCode(POSITION.referenceCode)
        return item
    }

    ClinicalFindingItem buildItemWithoutModifiers() {
        def record = ClinicalFactRecord.newBuilder()
                .withPatientId(PATIENT_ID)
                .withNamespace(NAMESPACE)
                .withStartDate(new Timestamp(NOW.toEpochMilli()))
                .withValue(OBSERVATION_VALUE)
                .withUlnValue(ULN_VALUE)
                .withLlnValue(LLN_VALUE)
                .withUnit(OBSERVATION_UNIT)
                .withLabel(CONCEPT_LABEL)
                .build()
        return new ClinicalFindingItem(record)
    }

    private static Map<String, Optional<Modifier>> createModifiers() {
        def modifiers = new HashMap()
        modifiers.put(LATERALITY.referenceCode, Optional.of(LATERALITY))
        modifiers.put(LOCATION.referenceCode, Optional.of(LOCATION))
        modifiers.put(POSITION.referenceCode, Optional.of(POSITION))
        return modifiers
    }
}