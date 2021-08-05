package com.custodix.insite.local.ehr2edc.mongo.app.document;

import javax.persistence.Embeddable;

import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

@Embeddable
public class EDCConnectionDocumentId {

    private final String studyId;
    private final StudyConnectionType connectionType;

    private EDCConnectionDocumentId(String studyId, StudyConnectionType connectionType) {
        this.studyId = studyId;
        this.connectionType = connectionType;
    }

    public static EDCConnectionDocumentId from(ExternalEDCConnection externalEDCConnection) {
        return new EDCConnectionDocumentId(
                externalEDCConnection.getStudyId().getId(),
                externalEDCConnection.getConnectionType());
    }

    public static EDCConnectionDocumentId from(StudyId studyId, StudyConnectionType connectionType) {
        return new EDCConnectionDocumentId(
                studyId.getId(),
                connectionType);
    }

    public String getStudyId() {
        return studyId;
    }

    public StudyConnectionType getConnectionType() {
        return connectionType;
    }

}
