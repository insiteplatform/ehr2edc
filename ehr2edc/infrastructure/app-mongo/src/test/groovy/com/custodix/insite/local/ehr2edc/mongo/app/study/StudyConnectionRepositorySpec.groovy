package com.custodix.insite.local.ehr2edc.mongo.app.study

import com.custodix.insite.local.ehr2edc.SpecConfiguration
import com.custodix.insite.local.ehr2edc.mongo.app.document.EDCConnectionDocument
import com.custodix.insite.local.ehr2edc.mongo.app.document.EDCConnectionDocumentId
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import com.custodix.insite.local.ehr2edc.vocabulary.StudyIdObjectMother
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest(classes = SpecConfiguration.class)
class StudyConnectionRepositorySpec extends Specification {

    @Autowired
    private StudyConnectionMongoRepository connectionRepository

    @Autowired
    private StudyConnectionMongoRepository.StudyConnectionMongoSnapshotRepository connectionDocumentRepository

    def "save Study Connection correctly"() {
        given: "a default study connection"
        def connection = StudyConnectionObjectMother.aDefaultStudyConnection()

        when: "the connection is saved"
        connectionRepository.save(connection)

        then: "an EDC Connection document is created"
        with(connectionDocumentRepository.findById(EDCConnectionDocumentId.from(connection.studyId, connection.connectionType)).get()) {
            id.studyId == connection.studyId.id
            id.connectionType == connection.connectionType
            externalSiteId == connection.externalSiteId.id
            studyIdOverride == connection.studyIdOverride.get().id
            clinicalDataURI == connection.clinicalDataURI.toASCIIString()
            username == connection.username
            password == connection.password
            enabled == connection.enabled
        }

    }

    def "get study connection for type READ_SUBJECTS returns correct connection"() {
        given: "a default study connection to read subjects"
        def defaultConnection = StudyConnectionObjectMother.aDefaultReadSubjectsStudyConnection()
        connectionDocumentRepository.save(EDCConnectionDocument.from(defaultConnection))

        when: "getting the connection by studyId and connectionType"
        def connection = connectionRepository.getReadSubjectsStudyConnectionById(defaultConnection.studyId)

        then: "the correct study connection is returned"
        with(connection) {
            studyId == defaultConnection.studyId
            connectionType == StudyConnectionType.READ_SUBJECTS
        }
    }

    def "get study connection for type WRITE_SUBJECT returns correct connection"() {
        given: "a default study connection to write a subject"
        def defaultConnection = StudyConnectionObjectMother.aDefaultWriteSubjectStudyConnection()
        connectionDocumentRepository.save(EDCConnectionDocument.from(defaultConnection))

        when: "getting the connection by studyId and connectionType"
        def connection = connectionRepository.getWriteSubjectStudyConnectionById(defaultConnection.studyId)

        then: "the correct study connection is returned"
        with(connection) {
            studyId == defaultConnection.studyId
            connectionType == StudyConnectionType.WRITE_SUBJECT
        }
    }

    def "get study connection for type SUBMIT_EVENT returns correct connection"() {
        given: "a default study connection to submit an event"
        def defaultConnection = StudyConnectionObjectMother.aDefaultSubmitEventStudyConnection()
        connectionDocumentRepository.save(EDCConnectionDocument.from(defaultConnection))

        when: "getting the connection by studyId and connectionType"
        def connection = connectionRepository.getSubmitEventStudyConnectionById(defaultConnection.studyId)

        then: "the correct study connection is returned"
        with(connection) {
            studyId == defaultConnection.studyId
            connectionType == StudyConnectionType.SUBMIT_EVENT
        }
    }

    def "get study connection for type READ_LABNAMES returns correct connection"() {
        given: "a default study connection to read labnames"
        def defaultConnection = StudyConnectionObjectMother.aDefaultReadLabnamesStudyConnection()
        connectionDocumentRepository.save(EDCConnectionDocument.from(defaultConnection))

        when: "getting the connection by studyId and connectionType"
        def connection = connectionRepository.getReadLabnamesStudyConnectionById(defaultConnection.studyId)

        then: "the correct study connection is returned"
        with(connection) {
            studyId == defaultConnection.studyId
            connectionType == StudyConnectionType.READ_LABNAMES
        }
    }

    def "find existing study connection returns a connection"() {
        given: "a default study connection"
        def connection = StudyConnectionObjectMother.aDefaultStudyConnection()
        connectionDocumentRepository.save(EDCConnectionDocument.from(connection))

        when: "finding the connection by studyId and connectionType"
        def result = connectionRepository.findStudyConnectionByIdAndType(connection.studyId, connection.connectionType)

        then: "the correct connection is returned"
        with(result.get()) {
            studyId == connection.studyId
            connectionType == connection.connectionType
        }
    }

    def "find non-existing study connection returns an empty optional"() {
        given: "a default study connection"
        def connection = StudyConnectionObjectMother.aDefaultStudyConnection()
        connectionDocumentRepository.save(EDCConnectionDocument.from(connection))

        when: "finding the connection by another studyId and connectionType"
        def result = connectionRepository.findStudyConnectionByIdAndType(StudyIdObjectMother.aRandomStudyId(), connection.connectionType)

        then: "an empty optional is returned"
        !result.isPresent()
    }

    def "get active study connection that is enabled returns the connection"() {
        given: "a study connection that is enabled"
        def connection = StudyConnectionObjectMother.aDefaultStudyConnectionBuilder()
                .withEnabled(true)
                .build()
        connectionDocumentRepository.save(EDCConnectionDocument.from(connection))

        when: "get active study connection"
        def result = connectionRepository.getActive(connection.studyId, connection.connectionType)

        then: "the connection is returned"
        with(result) {
            studyId == connection.studyId
            connectionType == connection.connectionType
        }
    }

    def "get active study connection that is disabled throws exception"() {
        given: "a study connection that is disabled"
        def connection = StudyConnectionObjectMother.aDefaultStudyConnectionBuilder()
                .withEnabled(false)
                .build()
        connectionDocumentRepository.save(EDCConnectionDocument.from(connection))

        when: "get active study connection"
        connectionRepository.getActive(connection.studyId, connection.connectionType)

        then: "an exception is thrown"
        def exception = thrown DomainException
        exception.key == "study.connection.disabled"
        exception.parameters == [connection.studyId, connection.connectionType].toArray()
    }

    def "get active study connection that doesn't exists throws exception"() {
        given: "a study without connection"
        def studyId = StudyId.of("unknown")

        when: "get active study connection"
        connectionRepository.getActive(studyId, StudyConnectionType.READ_SUBJECTS)

        then: "an exception is thrown"
        def exception = thrown DomainException
        exception.key == "study.connection.unknown"
        exception.parameters == [studyId, StudyConnectionType.READ_SUBJECTS].toArray()
    }

    @Unroll
    def "find active study connection with enabled '#enabled' returns the connection when '#enabled' is true"(boolean enabled) {
        given: "a study connection with enabled '#enabled'"
        def connection = StudyConnectionObjectMother.aDefaultStudyConnectionBuilder()
                .withEnabled(enabled)
                .build()
        connectionDocumentRepository.save(EDCConnectionDocument.from(connection))

        when: "find active study connection"
        def result = connectionRepository.findActive(connection.studyId, connection.connectionType)

        then: "'#enabled' is returned"
        result.isPresent() == enabled

        where:
        enabled | _
        true    | _
        false   | _
    }

    def "find active study connection that doesn't exist returns an empty optional"() {
        given: "a study without connection"
        def studyId = StudyId.of("unknown")

        when: "finding the active study connection"
        def result = connectionRepository.findActive(studyId, StudyConnectionType.READ_SUBJECTS)

        then: "an empty optional is returned"
        !result.isPresent()
    }

    @Unroll
    def "check if a study connection with enabled '#enabled' is enabled returns '#enabled'"(boolean enabled) {
        given: "a study connection with enabled '#enabled'"
        def connection = StudyConnectionObjectMother.aDefaultStudyConnectionBuilder()
                .withEnabled(enabled)
                .build()
        connectionDocumentRepository.save(EDCConnectionDocument.from(connection))

        when: "checking if the study connection is enabled"
        def result = connectionRepository.isEnabled(connection.studyId, connection.connectionType)

        then: "'#enabled' is returned"
        result == enabled

        where:
        enabled | _
        true    | _
        false   | _
    }

    def "check if a study without connection is enabled returns false"() {
        given: "a study without connection"
        def studyId = StudyId.of("unknown")

        when: "checking if the study connection is enabled"
        def result = connectionRepository.isEnabled(studyId, StudyConnectionType.READ_SUBJECTS)

        then: "false is returned"
        !result
    }

    def "domain exception is thrown if an unknown connection is retrieved"() {
        given: "an existing study connection"
        def connection = StudyConnectionObjectMother.aDefaultStudyConnection()
        connectionDocumentRepository.save(EDCConnectionDocument.from(connection))

        when: "getting the connection by studyId and connectionType"
        connectionRepository.getReadSubjectsStudyConnectionById(StudyId.of("unknown"))

        then:
        DomainException domainException = thrown(DomainException)
        domainException.getKey() == "study.connection.unknown"
    }

}