package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patientsearchcriteriainformation;

import java.time.LocalDate;

import org.springframework.batch.item.ItemProcessor;

import com.custodix.insite.mongodb.export.patient.domain.model.patientsearchcriteriainformation.PatientSearchCriteriaInformationRecord;
import com.custodix.insite.mongodb.export.patient.domain.model.patientsearchcriteriainformation.PatientSearchCriteriaInformation;
import com.custodix.insite.mongodb.vocabulary.Namespace;
import com.custodix.insite.mongodb.vocabulary.PatientId;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public class PatientSearchCriteriaInformationRecordProcessor implements ItemProcessor<PatientSearchCriteriaInformationRecord, PatientSearchCriteriaInformation> {
	@Override
	public PatientSearchCriteriaInformation process(PatientSearchCriteriaInformationRecord patientIdRecord) {
		return PatientSearchCriteriaInformation.newBuilder()
			    .withPatientIdentifier(createPatientIdentifier(patientIdRecord))
				.withBirthDate(getBirthDate(patientIdRecord))
				.build();
	}

	private PatientIdentifier createPatientIdentifier(PatientSearchCriteriaInformationRecord patientIdRecord) {
		return PatientIdentifier.newBuilder()
				.withNamespace(Namespace.of(patientIdRecord.getNamespace()))
				.withPatientId(PatientId.of(patientIdRecord.getId()))
				.build();
	}

	private LocalDate getBirthDate(PatientSearchCriteriaInformationRecord patientIdRecord) {
		return  patientIdRecord.getBirthDate() != null ?
				patientIdRecord.getBirthDate().toLocalDateTime().toLocalDate() :
				null;
	}
}
