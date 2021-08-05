package com.custodix.insite.local.ehr2edc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.custodix.insite.local.ehr2edc.vocabulary.DeregisterReason;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public final class RegistrationRecord {
	private final PatientCDWReference patientId;
	private final StudyId studyId;
	private final SubjectId subjectId;
	private final String date;
	private final DeregisterReason reason;
	private final Type type;

	private RegistrationRecord(PatientCDWReference patientId, StudyId studyId, SubjectId subjectId, LocalDate date) {
		this.patientId = patientId;
		this.studyId = studyId;
		this.subjectId = subjectId;
		this.date = DateTimeFormatter.ISO_LOCAL_DATE.format(date);
		this.reason = null;
		this.type = Type.REGISTRATION;
	}

	private RegistrationRecord(PatientCDWReference patientId, StudyId studyId, SubjectId subjectId, LocalDate date,
			DeregisterReason reason) {
		this.patientId = patientId;
		this.studyId = studyId;
		this.subjectId = subjectId;
		this.date = DateTimeFormatter.ISO_LOCAL_DATE.format(date);
		this.reason = reason;
		this.type = Type.DEREGISTRATION;
	}

	public PatientCDWReference getPatientId() {
		return patientId;
	}

	public StudyId getStudyId() {
		return studyId;
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public String getDate() {
		return date;
	}

	public DeregisterReason getReason() {
		return reason;
	}

	public Type getType() {
		return type;
	}

	public static RegistrationRecord registration(PatientCDWReference patientId, StudyId studyId, SubjectId subjectId,
			LocalDate date) {
		return new RegistrationRecord(patientId, studyId, subjectId, date);
	}

	public static RegistrationRecord deregistration(PatientCDWReference patientId, StudyId studyId, SubjectId subjectId,
			LocalDate date, DeregisterReason reason) {
		return new RegistrationRecord(patientId, studyId, subjectId, date, reason);
	}

	public enum Type {
		REGISTRATION,
		DEREGISTRATION
	}
}
