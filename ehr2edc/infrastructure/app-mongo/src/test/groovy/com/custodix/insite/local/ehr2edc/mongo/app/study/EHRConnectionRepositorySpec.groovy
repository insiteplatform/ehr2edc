package com.custodix.insite.local.ehr2edc.mongo.app.study

import com.custodix.insite.local.ehr2edc.RepositorySpec
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException
import com.custodix.insite.local.ehr2edc.vocabulary.EHRConnectionObjectMother
import com.custodix.insite.local.ehr2edc.vocabulary.EHRSystem
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import org.springframework.beans.factory.annotation.Autowired

class EHRConnectionRepositorySpec extends RepositorySpec {

    @Autowired
    private EHRConnectionMongoRepository ehrConnectionRepository

    def "save an EHR connection"() {
        given: "an EHR connection"
        def connection = EHRConnectionObjectMother.aFhirConnection()

        when: "I save the EHR connection"
        ehrConnectionRepository.save(connection)

        then: "an EHR connection document is created"
        with(ehrConnectionRepository.findByStudyId(connection.studyId).get()) {
            studyId == connection.studyId
            uri == connection.uri
            system == connection.system
        }
    }

    def "get an EHR connection by study id"() {
        given: "an EHR connection"
        def connection = EHRConnectionObjectMother.aFhirConnection()
        ehrConnectionRepository.save(connection)

        when: "I get the EHR connection"
        def result = ehrConnectionRepository.getByStudyId(connection.studyId)

        then: "I receive the EHR connection"
        with(result) {
            studyId == connection.studyId
            uri == connection.uri
            system == connection.system
        }
    }

    def "get an EHR connection by study id that does not exist throws exception"() {
        when: "I get an EHR connection that does not exist"
        def studyId = StudyId.of("999")
        ehrConnectionRepository.getByStudyId(studyId)

        then: "an exception is thrown"
        def exception = thrown DomainException
        exception.key == "study.ehr.connection.not.found"
        exception.parameters == [studyId.id].toArray()
    }

    def "find an EHR connection by study id"() {
        given: "an EHR connection"
        def connection = EHRConnectionObjectMother.aFhirConnection()
        ehrConnectionRepository.save(connection)

        when: "I find the EHR connection"
        def result = ehrConnectionRepository.findByStudyId(connection.studyId)

        then: "I receive the EHR connection"
        with(result.get()) {
            studyId == connection.studyId
            uri == connection.uri
            system == connection.system
        }
    }

    def "find an EHR connection by study id that does not exist returns an empty result"() {
        when: "find an EHR connection that does not exist"
        def result = ehrConnectionRepository.findByStudyId(StudyId.of("999"))

        then: "I receive an empty result"
        result == Optional.empty()
    }

    def "find an EHR connection by study id and system"() {
        given: "an EHR connection"
        def connection = EHRConnectionObjectMother.aFhirConnection()
        ehrConnectionRepository.save(connection)

        when: "I find the EHR connection"
        def result = ehrConnectionRepository.findByStudyIdAndSystem(connection.studyId, EHRSystem.FHIR)

        then: "I receive the EHR connection"
        with(result.get()) {
            studyId == connection.studyId
            uri == connection.uri
            system == connection.system
        }
    }

    def "find an EHR connection by study id and system for which the study id does not match returns an empty result"() {
        given: "an EHR connection"
        def connection = EHRConnectionObjectMother.aFhirConnection()
        ehrConnectionRepository.save(connection)

        when: "I find an EHR connection by study id and system for which the study id does not match"
        def result = ehrConnectionRepository.findByStudyIdAndSystem(StudyId.of("999"), EHRSystem.FHIR)

        then: "I receive an empty result"
        result == Optional.empty()
    }

    def "find an EHR connection by study id and system for which the system does not match returns an empty result"() {
        given: "an EHR connection"
        def connection = EHRConnectionObjectMother.aFhirConnection()
        ehrConnectionRepository.save(connection)

        when: "I find an EHR connection by study id and system for which the system does not match"
        def result = ehrConnectionRepository.findByStudyIdAndSystem(connection.studyId, EHRSystem.MONGO)

        then: "I receive an empty result"
        result == Optional.empty()
    }
}