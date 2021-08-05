package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportsubjects;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient;
import com.custodix.insite.mongodb.export.patient.application.api.GetActiveSubjects;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public class ExportSubjectsRunner {
	private static final Logger LOGGER = getLogger(ExportSubjectsRunner.class);

	private final GetActiveSubjects getActiveSubjects;
	private final ExportPatient exportPatient;

	public ExportSubjectsRunner(final GetActiveSubjects getActiveSubjects, final ExportPatient exportPatient) {
		this.getActiveSubjects = getActiveSubjects;
		this.exportPatient = exportPatient;
	}

	public void execute() {
		getActiveSubjects.getAll()
				.getPatientIdentifiers()
				.forEach(this::exportActiveSubject);
	}

	private void exportActiveSubject(final PatientIdentifier patientIdentifier) {
		try {
			exportPatient.export(toRequest(patientIdentifier));
		} catch (RuntimeException ex) {
			LOGGER.error(String.format("Failed to export subject with id '%s'", patientIdentifier.getSubjectId().getId()), ex);
		}
	}

	private ExportPatient.Request toRequest(final PatientIdentifier patientIdentifier) {
		return ExportPatient.Request.newBuilder().withPatientIdentifier(patientIdentifier).build();
	}
}
