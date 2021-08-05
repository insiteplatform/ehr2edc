package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patient;

import static com.custodix.insite.mongodb.export.patient.domain.model.demographic.PatientGender.Gender.FEMALE;
import static com.custodix.insite.mongodb.export.patient.domain.model.demographic.PatientGender.Gender.MALE;
import static com.custodix.insite.mongodb.export.patient.domain.model.demographic.PatientGender.Gender.UNKNOWN;
import static com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.BatchExportPatientRunner.SUBJECT_ID_PARAM;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;

import com.custodix.insite.mongodb.export.patient.domain.model.PatientFact;
import com.custodix.insite.mongodb.export.patient.domain.model.PatientRecord;
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.BirthInformation;
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.DeathInformation;
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.PatientGender;
import com.custodix.insite.mongodb.export.patient.domain.model.demographic.VitalStatus;
import com.custodix.insite.mongodb.vocabulary.Namespace;
import com.custodix.insite.mongodb.vocabulary.PatientId;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

@StepScope
final class PatientRecordProcessor implements ItemProcessor<PatientRecord, PatientFact> {

	private final PatientExportGenderCodesSettings patientExportGenderCodesSettings;

	private SubjectId subjectId;

	PatientRecordProcessor(PatientExportGenderCodesSettings patientExportGenderCodesSettings) {
		this.patientExportGenderCodesSettings = patientExportGenderCodesSettings;
	}

	@BeforeStep
	public void beforeStep(final StepExecution stepExecution) {
		this.subjectId = SubjectId.of(stepExecution.getJobParameters()
				.getString(SUBJECT_ID_PARAM));
	}

	@Override
	public PatientFact process(PatientRecord patientRecord) {
		return PatientFact.newBuilder()
				.withIdentifier(mapIdentifier(patientRecord))
				.withGender(mapGender(patientRecord))
				.withBirthInformation(mapBirthInformation(patientRecord))
				.withVitalStatus(mapVitalStatus(patientRecord))
				.withDeathInformation(mapDeathInformation(patientRecord))
				.build();
	}

	private PatientIdentifier mapIdentifier(PatientRecord patientRecord) {
		return PatientIdentifier.newBuilder()
				.withPatientId(PatientId.of(patientRecord.getId()))
				.withNamespace(Namespace.of(patientRecord.getNamespace()))
				.withSubjectId(subjectId)
				.build();
	}

	private DeathInformation mapDeathInformation(PatientRecord patientRecord) {
		DeathInformation.Builder builder = DeathInformation.newBuilder()
				.withAccuracy(mapDeathDateAccuracy(patientRecord));
		mapOptionalTimestamp(patientRecord.getDeathDate()).ifPresent(builder::withDeathDate);
		return builder.build();
	}

	private DeathInformation.Accuracy mapDeathDateAccuracy(PatientRecord patientRecord) {
		String vitalStatus = patientRecord.getVitalStatus()
				.toUpperCase();
		if (vitalStatus.isEmpty()) {
			return DeathInformation.Accuracy.UNSPECIFIED;
		}
		Character accuracyChar = vitalStatus.charAt(0);
		return Arrays.stream(DeathInformation.Accuracy.values())
				.filter(a -> a.getCode()
						.equals(accuracyChar))
				.findFirst()
				.orElse(DeathInformation.Accuracy.UNSPECIFIED);
	}

	private VitalStatus mapVitalStatus(PatientRecord patientRecord) {
		DeathInformation.Accuracy deathInformationAccuracy = mapDeathDateAccuracy(patientRecord);
		return VitalStatus.newBuilder()
				.withStatus(deathInformationAccuracy.toVitalStatus())
				.build();
	}

	private BirthInformation mapBirthInformation(PatientRecord patientRecord) {
		return BirthInformation.newBuilder()
				.withAccuracy(mapBirthDateAccuracy(patientRecord))
				.withBirthDate(mapTimestamp(patientRecord.getBirthDate()))
				.build();
	}

	private BirthInformation.Accuracy mapBirthDateAccuracy(PatientRecord patientRecord) {
		String vitalStatus = patientRecord.getVitalStatus()
				.toUpperCase();

		if (vitalStatus.length() < 2) {
			return BirthInformation.Accuracy.UNSPECIFIED;
		}
		Character accuracyChar = vitalStatus.charAt(1);
		return Arrays.stream(BirthInformation.Accuracy.values())
				.filter(a -> a.getCode()
						.equals(accuracyChar))
				.findFirst()
				.orElse(BirthInformation.Accuracy.UNSPECIFIED);
	}

	private Instant mapTimestamp(Timestamp timestamp) {
		return timestamp.toInstant();
	}

	private Optional<Instant> mapOptionalTimestamp(Timestamp timestamp) {
		return Optional.ofNullable(timestamp)
				.map(this::mapTimestamp);
	}

	private PatientGender mapGender(PatientRecord patientRecord) {
		String genderCode = patientRecord.getGender();
		PatientGender.Builder builder = PatientGender.newBuilder()
				.withSourceValue(genderCode);

		if (patientExportGenderCodesSettings.getFemaleCodes()
				.contains(genderCode)) {
			return builder.withInterpretedValue(FEMALE)
					.build();
		} else if (patientExportGenderCodesSettings.getMaleCodes()
				.contains(genderCode)) {
			return builder.withInterpretedValue(MALE)
					.build();
		} else {
			return builder.withInterpretedValue(UNKNOWN)
					.build();
		}
	}
}