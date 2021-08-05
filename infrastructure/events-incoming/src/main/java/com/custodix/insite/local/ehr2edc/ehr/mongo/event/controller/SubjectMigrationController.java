package com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller;

import com.custodix.insite.local.ehr2edc.ehr.mongo.command.SubjectMigration;
import com.custodix.insite.local.ehr2edc.events.SubjectRegisteredEvent;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public class SubjectMigrationController implements EHRAsyncEventController<SubjectRegisteredEvent>{

	private final SubjectMigration subjectMigration;

	public SubjectMigrationController(SubjectMigration subjectMigration) {
		this.subjectMigration = subjectMigration;
	}

	@Override
	public void handle(SubjectRegisteredEvent subjectRegisteredEvent) {
		subjectMigration.migrate(getRequest(subjectRegisteredEvent));
	}

	private SubjectMigration.Request getRequest(SubjectRegisteredEvent subjectRegisteredEvent) {
		PatientCDWReference patientCDWReference = PatientCDWReference.newBuilder()
				.withId(subjectRegisteredEvent.getPatientId())
				.withSource(subjectRegisteredEvent.getNamespace())
				.build();
		StudyId studyId = StudyId.of(subjectRegisteredEvent.getStudyId());
		SubjectId subjectId = SubjectId.of(subjectRegisteredEvent.getSubjectId());
		return SubjectMigration.Request.newBuilder()
				.withPatientCDWReference(patientCDWReference)
				.withStudyId(studyId)
				.withSubjectId(subjectId)
				.build();
	}
}
