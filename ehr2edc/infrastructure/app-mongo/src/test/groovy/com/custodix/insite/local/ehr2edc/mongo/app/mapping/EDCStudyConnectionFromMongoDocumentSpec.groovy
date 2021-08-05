package com.custodix.insite.local.ehr2edc.mongo.app.mapping

import com.custodix.insite.local.ehr2edc.mongo.app.document.EDCConnectionDocument
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalSiteId
import com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import spock.lang.Specification
import spock.lang.Unroll

class EDCStudyConnectionFromMongoDocumentSpec extends Specification {

    @Unroll
    def "Mapping an StudyConnection document to a EDCStudyConnection"() {
        given: "a study connection document with studyId '#studyId', connectionType '#connectionType', edcSystem '#edcSystem', externalSiteId '#externalSiteId', studyIdOverride '#studyIdOverride', clinicalDataURI '#clinicalDataURI', username '#username', password '#password' and enabled '#enabled'"
        def document = EDCConnectionDocument.from(ExternalEDCConnection.newBuilder()
                .withStudyId(StudyId.of(studyId))
                .withConnectionType(connectionType)
                .withEdcSystem(edcSystem)
                .withExternalSiteId(ExternalSiteId.of(externalSiteId))
                .withStudyIdOVerride(StudyId.of(studyIdOverride))
                .withClinicalDataURI(new URI(clinicalDataURI))
                .withUsername(username)
                .withPassword(password)
                .withEnabled(enabled)
                .build())

        when: "converting to mongo document"
        def connection = document.to()

        then: "study id is converted to '#studyId'"
        connection.studyId == StudyId.of(studyId)
        and: "connection type is converted to '#connectionType'"
        connection.connectionType == connectionType
        and: "edcSystem is converted to '#edcSystem'"
        connection.edcSystem == edcSystem
        and: "externalSiteId is converted to '#externalSiteId'"
        connection.externalSiteId == ExternalSiteId.of(externalSiteId)
        and: "studyIdOverride is converted to '#studyIdOverride'"
        connection.studyIdOverride.get() == StudyId.of(studyIdOverride)
        and: "clinicalDataURI is converted to '#clinicalDataURI'"
        connection.clinicalDataURI == new URI(clinicalDataURI)
        and: "username is converted to '#username'"
        connection.username == username
        and: "password is converted to '#password'"
        connection.password == password
        and: "enabled is converted to '#enabled'"
        connection.enabled == enabled

        where:
        studyId | connectionType                    | edcSystem      | externalSiteId | studyIdOverride | clinicalDataURI | username | password   | enabled
        "123"   | StudyConnectionType.READ_SUBJECTS | EDCSystem.RAVE | "456"          | "789"           | "https://rave"  | "user"   | "password" | true

    }

}
