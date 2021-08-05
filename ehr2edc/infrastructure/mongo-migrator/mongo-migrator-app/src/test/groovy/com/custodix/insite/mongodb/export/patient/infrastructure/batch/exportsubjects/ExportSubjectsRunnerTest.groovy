package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportsubjects

import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import com.custodix.insite.mongodb.export.patient.application.api.GetActiveSubjects
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier
import com.custodix.insite.mongodb.vocabulary.SubjectId
import spock.lang.Specification

import static java.util.Arrays.asList

class ExportSubjectsRunnerTest extends Specification {

    private GetActiveSubjects getActiveSubjects;
    private ExportPatient exportPatient;
    private ExportSubjectsRunner exportSubjectsRunner;

    def setup() {
        getActiveSubjects = Mock(GetActiveSubjects)
        exportPatient = Mock(ExportPatient)
        exportSubjectsRunner = new ExportSubjectsRunner(getActiveSubjects, exportPatient)
    }

    def "When an export of a subject failed with runtime exception, the runner will continue with the export of other subjectz"(
            PatientIdentifier activeSubject1,
            PatientIdentifier activeSubject2) {
        given: "two active subjects"
        1 * getActiveSubjects.getAll() >> GetActiveSubjects.Response.newBuilder()
                .withPatientIdentifiers(asList(activeSubject1, activeSubject2))
                .build()

        when: "exporting active subjects"
        exportSubjectsRunner.execute()

        then: '''expect export first active subject with id #activeSubject1.subjectId.id which throws runtime exception'''
        1 * exportPatient.export(_ as ExportPatient.Request) >> { ExportPatient.Request request ->
            assert request.patientIdentifier.subjectId.id == activeSubject1.subjectId.id
            throw new RuntimeException()
        }
        and: '''then export active subject with id #activeSubject2.subjectId.id'''
        1 * exportPatient.export(_ as ExportPatient.Request) >> { ExportPatient.Request request ->
            assert request.patientIdentifier.subjectId.id == activeSubject2.subjectId.id
        }

        where:
        activeSubject1 | activeSubject2
        PatientIdentifier.of(PatientId.of("patientId1"), Namespace.of("nameSpace1"), SubjectId.of("subjectId1")) |
        PatientIdentifier.of(PatientId.of("patientId2"), Namespace.of("nameSpace2"), SubjectId.of("subjectId2"))
    }
}
