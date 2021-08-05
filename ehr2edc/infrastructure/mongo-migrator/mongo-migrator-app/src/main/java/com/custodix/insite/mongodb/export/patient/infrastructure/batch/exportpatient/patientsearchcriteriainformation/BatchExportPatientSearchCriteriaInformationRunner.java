package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patientsearchcriteriainformation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;

import com.custodix.insite.mongodb.export.patient.domain.exceptions.SystemException;
import com.custodix.insite.mongodb.export.patient.domain.service.ExportPatientSearchCriteriaInformationRunner;

public class BatchExportPatientSearchCriteriaInformationRunner implements ExportPatientSearchCriteriaInformationRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchExportPatientSearchCriteriaInformationRunner.class);
	private static final String EXPORT_FAILED_MESSAGE = "Unable to execute patients ids export job";

	private final JobBuilderFactory jobBuilderFactory;
	private final JobLauncher jobLauncher;
	private final PatientSearchCriteriaInformationStepFactory patientSearchCriteriaInformationStepFactory;

	public BatchExportPatientSearchCriteriaInformationRunner(JobBuilderFactory jobBuilderFactory, JobLauncher jobLauncher,
			PatientSearchCriteriaInformationStepFactory patientSearchCriteriaInformationStepFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.jobLauncher = jobLauncher;
		this.patientSearchCriteriaInformationStepFactory = patientSearchCriteriaInformationStepFactory;
	}

	@Override
	public void run() {
		try {
			exportPatientsIdentifications();
		} catch (JobExecutionException e) {
			LOGGER.error(EXPORT_FAILED_MESSAGE, e);
			throw new SystemException(EXPORT_FAILED_MESSAGE, e);
		}
	}

	private void exportPatientsIdentifications() throws JobExecutionException  {
		Job job = buildExportJob();
		jobLauncher.run(job, buildJobParameters());
	}

	private Job buildExportJob() {
		Step step = patientSearchCriteriaInformationStepFactory.buildStep();
		SimpleJobBuilder jobBuilder = createJobBuilder(step);
		return jobBuilder.build();
	}

	private SimpleJobBuilder createJobBuilder(Step firstStep) {
		return jobBuilderFactory.get("exportPatientSearchCriteriaInformation")
				.incrementer(new RunIdIncrementer())
				.start(firstStep);
	}

	private JobParameters buildJobParameters() {
		return new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
	}
}
