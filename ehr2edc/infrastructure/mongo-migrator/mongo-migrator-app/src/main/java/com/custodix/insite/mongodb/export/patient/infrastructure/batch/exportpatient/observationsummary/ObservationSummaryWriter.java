package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.observationsummary;

import static com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.BatchExportPatientRunner.SUBJECT_ID_PARAM;

import java.util.Objects;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.observationsummary.ObservationSummaryDocument;

public class ObservationSummaryWriter implements JobExecutionListener {
	private final ObservationSummaryCounter observationSummaryCounter;
	private final MongoTemplate mongoTemplate;

	public ObservationSummaryWriter(ObservationSummaryCounter observationSummaryCounter, MongoTemplate mongoTemplate) {
		this.observationSummaryCounter = observationSummaryCounter;
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		try {
			String subjectId = jobExecution.getJobParameters()
					.getString(SUBJECT_ID_PARAM);
			observationSummaryCounter.getSummaryItemsDocumentsFor(subjectId)
					.stream()
					.filter(Objects::nonNull)
					.forEach(document -> mongoTemplate.insert(document, ObservationSummaryDocument.COLLECTION));
			observationSummaryCounter.resetCounter(subjectId);
		} catch (Exception e) {
			jobExecution.addFailureException(e);
		}
	}
}
