package com.custodix.insite.mongodb.export.patient.infrastructure.embedded

import com.custodix.insite.mongodb.export.TestConfiguration
import com.custodix.insite.mongodb.export.patient.domain.model.FastingStatus
import com.custodix.insite.mongodb.export.patient.domain.model.common.Concept
import com.custodix.insite.mongodb.export.patient.domain.model.labvalue.LaboratoryConceptInfo
import com.custodix.insite.mongodb.export.patient.domain.repository.LaboratoryConceptInfoRepository
import com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = [EmbeddedConceptConfiguration, TestConfiguration, MongoMigratorBatchConfiguration], properties = ["spring.main.allow-bean-definition-overriding=true"])
class EmbeddedLaboratoryConceptInfoRepositoryTest extends Specification {
    private static final String CONCEPT_SPECIMEN = "Test specimen"
    private static final String CONCEPT_METHOD_FULL = "Test method long"
    private static final String CONCEPT_COMPONENT = "Test component"

    @Autowired
    private LaboratoryConceptInfoRepository repository

    def "Querying for a laboratory concept that isn't known in the terminology"() {
        given: "A laboratory concept that isn't known in the terminology"
        Concept concept = Concept.newBuilder().withCode("UNKNOWN-CODE").build()

        when: "I ask for additional laboratory information for that term"
        Optional<LaboratoryConceptInfo> info = repository.findByConcept(concept)

        then: "No additional laboratory information can be found"
        assert !info.present
    }

    def "Querying for a laboratory concept with no method or fasting status"() {
        given: "A laboratory concept"
        Concept concept = Concept.newBuilder().withCode("CODE-1").build()

        when: "I ask for additional laboratory information for that term"
        Optional<LaboratoryConceptInfo> info = repository.findByConcept(concept)

        then: "The information about the method and fasting status is marked as missing"
        assert info.present
        verifyAll(info.get()) {
            specimenValue == CONCEPT_SPECIMEN
            specimenText == CONCEPT_SPECIMEN
            !methodValue.present
            !methodText.present
            fastingStatus == FastingStatus.UNDEFINED
            component == CONCEPT_COMPONENT
        }
    }

    def "Querying for a laboratory concept with a method value present"() {
        given: "A laboratory concept"
        Concept concept = Concept.newBuilder().withCode("CODE-2").build()

        when: "I ask for additional laboratory information for that term"
        Optional<LaboratoryConceptInfo> info = repository.findByConcept(concept)

        then: "The information about the method is marked as missing"
        assert info.present
        verifyAll(info.get()) {
            methodValue.present
            methodValue.get() == CONCEPT_METHOD_FULL
            methodText.present
            methodText.get() == CONCEPT_METHOD_FULL
        }
    }

    def "Querying for a laboratory concept with a negative fasting status"() {
        given: "A laboratory concept"
        Concept concept = Concept.newBuilder().withCode("CODE-3").build()

        when: "I ask for additional laboratory information for that term"
        Optional<LaboratoryConceptInfo> info = repository.findByConcept(concept)

        then: "The information about the fasting status indicates that it is not fasting"
        assert info.present
        verifyAll(info.get()) {
            fastingStatus == FastingStatus.NOT_FASTING
        }
    }

    def "Querying for a laboratory concept with a positive fasting status"() {
        given: "A laboratory concept"
        Concept concept = Concept.newBuilder().withCode("CODE-4").build()

        when: "I ask for additional laboratory information for that term"
        Optional<LaboratoryConceptInfo> info = repository.findByConcept(concept)

        then: "The information about the fasting status indicates that it is fasting"
        assert info.present
        verifyAll(info.get()) {
            fastingStatus == FastingStatus.FASTING
        }
    }

    def "Querying for a laboratory concept with all data present"() {
        given: "A laboratory concept"
        Concept concept = Concept.newBuilder().withCode("CODE-5").build()

        when: "I ask for additional laboratory information for that term"
        Optional<LaboratoryConceptInfo> info = repository.findByConcept(concept)

        then: "All information is being retrieved correctly"
        assert info.present
        verifyAll(info.get()) {
            specimenValue == CONCEPT_SPECIMEN
            methodValue.present
            methodValue.get() == CONCEPT_METHOD_FULL
            fastingStatus == FastingStatus.FASTING
            component == CONCEPT_COMPONENT
        }
    }

    def "Querying for a laboratory concept in a non-active state returns results"(String code) {
        given: "A laboratory concept"
        Concept concept = Concept.newBuilder().withCode(code).build()

        when: "I ask for additional laboratory information for that term"
        Optional<LaboratoryConceptInfo> info = repository.findByConcept(concept)

        then: "All information is being retrieved correctly"
        assert info.present
        verifyAll(info.get()) {
            specimenValue == CONCEPT_SPECIMEN
            specimenText == CONCEPT_SPECIMEN
            methodValue.present
            methodValue.get() == CONCEPT_METHOD_FULL
            methodText.present
            methodText.get() == CONCEPT_METHOD_FULL
            fastingStatus == FastingStatus.FASTING
            component == CONCEPT_COMPONENT
        }

        where:
        code << ["DEPRECATED-CODE", "DISCOURAGED-CODE", "TRIAL-CODE"]
    }
}