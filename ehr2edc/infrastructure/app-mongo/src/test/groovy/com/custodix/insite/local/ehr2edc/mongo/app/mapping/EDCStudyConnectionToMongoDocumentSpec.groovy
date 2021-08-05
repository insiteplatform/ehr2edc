package com.custodix.insite.local.ehr2edc.mongo.app.mapping

import com.custodix.insite.local.ehr2edc.mongo.app.document.EDCConnectionDocument
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalSiteId
import com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import spock.lang.Specification
import spock.lang.Unroll

class EDCStudyConnectionToMongoDocumentSpec extends Specification {

    @Unroll
    def "Mapping an EDCStudyConnection to a mongo document correctly"() {
        given: "a study connection with studyId '#studyId', connectionType '#connectionType', edcSystem '#edcSystem', externalSiteId '#externalSiteId', studyIdOverride '#studyIdOverride', clinicalDataURI '#clinicalDataURI', username '#username', password '#password' and enabled '#enabled'"
        def connection = ExternalEDCConnection.newBuilder()
                .withStudyId(StudyId.of(studyId))
                .withConnectionType(connectionType)
                .withEdcSystem(edcSystem)
                .withExternalSiteId(ExternalSiteId.of(externalSiteId))
                .withStudyIdOVerride(StudyId.of(studyIdOverride))
                .withClinicalDataURI(new URI(clinicalDataURI))
                .withUsername(username)
                .withPassword(password)
                .withEnabled(enabled)
                .build()

        when: "converting to mongo document"
        def connectionSnapshot = EDCConnectionDocument.from(connection)

        then: "study id is converted to '#studyId'"
        connectionSnapshot.id.studyId == studyId
        and: "connection type is converted to '#connectionType'"
        connectionSnapshot.id.connectionType == connectionType
        and: "edcSystem is converted to '#edcSystem'"
        connectionSnapshot.edcSystem == edcSystem
        and: "externalSiteId is converted to '#externalSiteId'"
        connectionSnapshot.externalSiteId == externalSiteId
        and: "studyIdOverride is converted to '#studyIdOverride'"
        connectionSnapshot.studyIdOverride == studyIdOverride
        and: "clinicalDataURI is converted to '#clinicalDataURI'"
        connectionSnapshot.clinicalDataURI == clinicalDataURI
        and: "username is converted to '#username'"
        connectionSnapshot.username == username
        and: "password is converted to '#password'"
        connectionSnapshot.password == password
        and: "enabled is converted to '#enabled'"
        connectionSnapshot.enabled == enabled

        where:
        studyId | connectionType                    | edcSystem      | externalSiteId | studyIdOverride | clinicalDataURI | username | password   | enabled
        "123"   | StudyConnectionType.READ_SUBJECTS | EDCSystem.RAVE | "456"          | "789"           | "https://rave"  | "user"   | "password" | true

    }

}
