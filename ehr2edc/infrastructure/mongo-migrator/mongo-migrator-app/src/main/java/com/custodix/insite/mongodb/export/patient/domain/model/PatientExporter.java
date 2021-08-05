package com.custodix.insite.mongodb.export.patient.domain.model;

import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientEnded;
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientFailed;
import com.custodix.insite.mongodb.export.patient.domain.event.ExportPatientStarting;
import com.custodix.insite.mongodb.export.patient.domain.exceptions.DomainException;
import com.custodix.insite.mongodb.export.patient.domain.service.ExportPatientRunner;
import com.custodix.insite.mongodb.export.patient.vocabulary.PatientExporterId;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public final class PatientExporter {

	private final ExportPatientRunner exportPatientRunner;
	private final PatientExporterId id;

	private PatientExporter(ExportPatientRunner exportPatientRunner) {
		this.id = PatientExporterId.generate();
		this.exportPatientRunner = exportPatientRunner;
	}

	public static PatientExporter getInstance(ExportPatientRunner exportPatientRunner) {
		return new PatientExporter(exportPatientRunner);
	}

	public void export(PatientIdentifier patientIdentifier) {
		try {
			DomainEventPublisher.publishEvent(toExportPatientStarting(patientIdentifier));
			exportPatientRunner.run(patientIdentifier);
			DomainEventPublisher.publishEvent(toExportPatientEnded(patientIdentifier));
		} catch (RuntimeException ex) {
			handleFailure(patientIdentifier, ex);
		}
	}

	private void handleFailure(final PatientIdentifier patientIdentifier, final RuntimeException cause) {
		DomainEventPublisher.publishEvent(toExportPatientFailed(patientIdentifier));
		throw new DomainException(String.format("Patient export id %s has failed", id.getId()), cause);
	}

	private ExportPatientStarting toExportPatientStarting(final PatientIdentifier patientIdentifier) {
		return ExportPatientStarting.newBuilder()
				.withSubjectId(patientIdentifier.getSubjectId()
						.getId())
				.withNamespace(patientIdentifier.getNamespace()
						.getName())
				.withPatientId(patientIdentifier.getPatientId()
						.getId())
				.withDate(DomainTime.now())
				.withPatientExporterId(id.getId())
				.build();
	}

	private ExportPatientEnded toExportPatientEnded(final PatientIdentifier patientIdentifier) {
		return ExportPatientEnded.newBuilder()
				.withSubjectId(patientIdentifier.getSubjectId()
						.getId())
				.withNamespace(patientIdentifier.getNamespace()
						.getName())
				.withPatientId(patientIdentifier.getPatientId()
						.getId())
				.withDate(DomainTime.now())
				.build();
	}

	private ExportPatientFailed toExportPatientFailed(final PatientIdentifier patientIdentifier) {
		return ExportPatientFailed.newBuilder()
				.withSubjectId(patientIdentifier.getSubjectId()
						.getId())
				.withNamespace(patientIdentifier.getNamespace()
						.getName())
				.withPatientId(patientIdentifier.getPatientId()
						.getId())
				.withDate(DomainTime.now())
				.build();
	}
}
