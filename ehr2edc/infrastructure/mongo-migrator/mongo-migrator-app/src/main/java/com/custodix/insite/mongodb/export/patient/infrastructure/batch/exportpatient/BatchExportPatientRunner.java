package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;

import com.custodix.insite.mongodb.export.patient.domain.exceptions.DomainException;
import com.custodix.insite.mongodb.export.patient.domain.exceptions.SystemException;
import com.custodix.insite.mongodb.export.patient.domain.repository.PatientRepository;
import com.custodix.insite.mongodb.export.patient.domain.service.ExportPatientRunner;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.observationsummary.ObservationSummaryWriter;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

public class BatchExportPatientRunner implements ExportPatientRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchExportPatientRunner.class);

	public static final String SUBJECT_ID_PARAM = "subjectId";

	private static final String EXPORT_FAILED_MESSAGE = "Unable to execute patient export job";
	private static final String PATIENT_NOT_FOUND_MESSAGE = "Unable to export a patient with identifier %s and namespace %s";
	private final JobBuilderFactory jobBuilderFactory;

	private final JobLauncher jobLauncher;
	private final List<ExportStepFactory> exportStepFactories;
	private final PatientRepository patientRepository;
	private final ObservationSummaryWriter observationSummaryWriter;

	public BatchExportPatientRunner(JobBuilderFactory jobBuilderFactory, JobLauncher jobLauncher,
			List<ExportStepFactory> exportStepFactories, PatientRepository patientRepository,
			ObservationSummaryWriter observationSummaryWriter) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.jobLauncher = jobLauncher;
		this.exportStepFactories = exportStepFactories;
		this.patientRepository = patientRepository;
		this.observationSummaryWriter = observationSummaryWriter;
	}

	@Override
	public void run(PatientIdentifier patientIdentifier) {
		try {
			JobExecution status = exportPatient(patientIdentifier);
			handleFailureExceptions(status);
		} catch (JobExecutionException e) {
			LOGGER.error(EXPORT_FAILED_MESSAGE, e);
			throw new SystemException(EXPORT_FAILED_MESSAGE, e);
		}
	}

	private void handleFailureExceptions(JobExecution status) throws JobExecutionException {
		List<Throwable> failureExceptions = status.getFailureExceptions();
		if (!failureExceptions.isEmpty()) {
			JobExecutionException jee = new JobExecutionException(EXPORT_FAILED_MESSAGE);
			failureExceptions.forEach(jee::addSuppressed);
			throw jee;
		}
	}

	private JobExecution exportPatient(PatientIdentifier patientIdentifier) throws JobExecutionException {
		verifyPatientExists(patientIdentifier);
		Job job = buildExportJob(patientIdentifier);
		return jobLauncher.run(job, buildJobParameters(patientIdentifier));
	}

	private void verifyPatientExists(PatientIdentifier patientIdentifier) {
		if (!patientRepository.patientExists(patientIdentifier)) {
			String message = String.format(PATIENT_NOT_FOUND_MESSAGE, patientIdentifier.getPatientId()
					.getId(), patientIdentifier.getNamespace()
					.getName());
			LOGGER.error(message);
			throw new DomainException(message);
		}
	}

	private Job buildExportJob(PatientIdentifier patientIdentifier) {
		List<Step> exportSteps = buildExportSteps(patientIdentifier);
		return buildExportJob(exportSteps);
	}

	private List<Step> buildExportSteps(PatientIdentifier patientIdentifier) {
		return exportStepFactories.stream()
				.map(factory -> factory.buildStep(patientIdentifier))
				.collect(toList());
	}

	private Job buildExportJob(List<Step> exportSteps) {
		exportSteps.sort(new StepOrderComparator());
		Step firstStep = exportSteps.get(0);
		SimpleJobBuilder jobBuilder = createJobBuilder(firstStep);
		exportSteps.stream()
				.skip(1)
				.forEachOrdered(jobBuilder::next);
		return jobBuilder.build();
	}

	private SimpleJobBuilder createJobBuilder(Step firstStep) {
		return jobBuilderFactory.get("exportPatient")
				.incrementer(new RunIdIncrementer())
				.listener(observationSummaryWriter)
				.start(firstStep);
	}

	private JobParameters buildJobParameters(PatientIdentifier patientIdentifier) {
		return new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.addString(SUBJECT_ID_PARAM, patientIdentifier.getSubjectId()
						.getId())
				.toJobParameters();
	}
}