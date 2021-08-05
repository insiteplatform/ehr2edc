package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.removepatientdata;

import static com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.BatchExportPatientRunner.SUBJECT_ID_PARAM;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.demographic.DemographicDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue.LabValueDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.medication.MedicationDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.observationsummary.ObservationSummaryDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.vitalsign.VitalSignDocument;

public class RemovePatientDataTasklet implements Tasklet {

	private static final String SUBJECT_FIELD_SOURCE = "subjectId";
	private final MongoTemplate mongoTemplate;

	public RemovePatientDataTasklet(final MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public RepeatStatus execute(final StepContribution stepContribution, final ChunkContext chunkContext) {
		String subjectId = chunkContext.getStepContext().getStepExecution().getJobParameters().getString(SUBJECT_ID_PARAM);

		if(StringUtils.isNotBlank(subjectId)) {
			Query query = createQueryFor(subjectId);

			mongoTemplate.remove(query, DemographicDocument.class);
			mongoTemplate.remove(query, LabValueDocument.class);
			mongoTemplate.remove(query, MedicationDocument.class);
			mongoTemplate.remove(query, VitalSignDocument.class);
			mongoTemplate.remove(query, ObservationSummaryDocument.class);
		}
		return RepeatStatus.FINISHED;
	}

	private Query createQueryFor(final String subjectId) {
		Query query = new Query();
		query.addCriteria(Criteria.where(SUBJECT_FIELD_SOURCE).is(subjectId));
		return query;
	}
}
