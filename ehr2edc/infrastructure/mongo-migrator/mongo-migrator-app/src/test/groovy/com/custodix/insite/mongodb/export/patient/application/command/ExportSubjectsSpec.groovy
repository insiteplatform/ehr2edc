package com.custodix.insite.mongodb.export.patient.application.command

import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.activesubject.ActiveSubjectDocument
import com.custodix.insite.mongodb.vocabulary.SubjectId

class ExportSubjectsSpec extends AbstractExportSubjectsSpec {

    private static final String PATIENT_ID = "7"
    private static final String NAMESPACE = "MASTER_PATIENT_INDEX"
    private static final String SUBJECT_ID = "999-888"

    def "correctly executes the export patient use case"(String patientId, String patientIdSource, String subjectId) {
        given: "an active subject"
        assertActiveSubjectWith(subjectId, patientId, patientIdSource);

        when: "performing the export of subjects"
        exportSubjects.export()

        then: "the export patient use case is called with"
        1 * exportPatientMock.export(_ as ExportPatient.Request) >> {
            ExportPatient.Request request ->
                request.patientIdentifier.subjectId.id == subjectId &&
                        request.patientIdentifier.patientId.id == patientId &&
                        request.patientIdentifier.namespace.name == patientIdSource
        }

        where:
        patientId  | patientIdSource | subjectId
        PATIENT_ID | NAMESPACE       | SUBJECT_ID

    }

    private void assertActiveSubjectWith(final String subjectId, final String patientId, final String patientIdSource) {
        activeSubjectDocumentRepository.save(ActiveSubjectDocument.newBuilder()
                .withPatientIdSource(patientIdSource)
                .withPatientId(patientId)
                .withSubjectId(SubjectId.of(subjectId))
                .build()
        )
    }
}
