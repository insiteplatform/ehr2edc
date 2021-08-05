package com.custodix.insite.mongodb.export.patient.application.command

import com.custodix.insite.mongodb.export.patient.application.TestContextConfiguration
import com.custodix.insite.mongodb.export.patient.domain.repository.PatientNamespaceRepository
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.EmbeddedMongoConfig
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.activesubject.ActiveSubjectDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.ActiveSubjectDocumentRepository
import com.custodix.insite.mongodb.export.patient.main.ActiveSubjectConfiguration
import com.custodix.insite.mongodb.export.patient.main.MigratorMongoDBConfiguration
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@SpringBootTest(
        classes = [
            ActiveSubjectConfiguration,
            MigratorMongoDBConfiguration,
            EmbeddedMongoAutoConfiguration,
            EmbeddedMongoConfig,
            TestContextConfiguration
        ]
)
@TestPropertySource(
        locations = "classpath:ehr2edc-infra-mongo-migrator.properties")
class AbstractSubjectActivationSpec extends Specification{

    @Autowired
    MongoTemplate mongoTemplate
    @Autowired
    ActiveSubjectDocumentRepository activeSubjectDocumentRepository
    @SpringBean
    PatientNamespaceRepository patientNamespaceRepository = Mock()

    def setup() {
        clearMongoDb()
    }

    void assertActiveSubject(final SubjectId subjectId, final PatientId patientId, final Namespace patientIdSource) {
        activeSubjectDocumentRepository.save(ActiveSubjectDocument.newBuilder()
            .withPatientId(patientId.getId())
            .withSubjectId(subjectId)
            .withPatientIdSource(patientIdSource.getName())
            .build())

        assert activeSubjectDocumentRepository.findBySubjectId(subjectId).isPresent()
    }

    void assertNoActiveSubject(final SubjectId subjectId) {
        assert !activeSubjectDocumentRepository.findBySubjectId(subjectId).isPresent()
    }

    void canHandlerPatientNamespace(Namespace namespace) {
        1 * patientNamespaceRepository.exists({
            it.name == namespace.name
        }) >> true
    }

    void cannotHandlerPatientNamespace(Namespace namespace) {
        1 * patientNamespaceRepository.exists({
            it.name == namespace.name
        }) >> false
    }

    void clearMongoDb() {
        clearDocumentsFor(ActiveSubjectDocument.class)
    }

    private void clearDocumentsFor(Class<?> documentClass) {
        mongoTemplate.remove(new Query(), documentClass)
    }

}
