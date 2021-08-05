package com.custodix.insite.mongodb.export.patient.infrastructure.embedded;

import static java.util.Collections.emptyList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.support.ListItemWriter;

class LaboratoryConceptInfoFetcher {
	private static final String JOB_NAME = "getLaboratoryConceptInfo";
	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedLaboratoryConceptInfoRepository.class);

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final JobLauncher jobLauncher;
	private final LaboratoryConceptInfoMappingReaderFactory readerFactory;

	LaboratoryConceptInfoFetcher(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
			JobLauncher jobLauncher) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
		this.jobLauncher = jobLauncher;
		this.readerFactory = new LaboratoryConceptInfoMappingReaderFactory();
	}

	List<LaboratoryConceptInfoMapping> fetch() {
		try {
			ListItemWriter<LaboratoryConceptInfoMapping> writer = new ListItemWriter<>();
			Step step = buildStep(writer);
			Job job = jobBuilderFactory.get(JOB_NAME)
					.start(step)
					.build();
			jobLauncher.run(job, new JobParametersBuilder().toJobParameters());
			return (List<LaboratoryConceptInfoMapping>) writer.getWrittenItems();
		} catch (JobExecutionException e) {
			LOGGER.error("Unable to retrieve the laboratory concept info", e);
			return emptyList();
		}
	}

	private TaskletStep buildStep(ListItemWriter<LaboratoryConceptInfoMapping> writer) {
		return stepBuilderFactory.get(JOB_NAME).<LaboratoryConceptInfoMapping, LaboratoryConceptInfoMapping>chunk(
				100).reader(readerFactory.createReader())
				.writer(writer)
				.build();
	}
}