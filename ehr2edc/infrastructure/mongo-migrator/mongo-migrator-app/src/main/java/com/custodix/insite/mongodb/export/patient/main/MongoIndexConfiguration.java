package com.custodix.insite.mongodb.export.patient.main;

import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.MONGO_TEMPLATE_MONGO_MIGRATOR;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.patientsearchcriteriainformation.PatientSearchCriteriaInformationMongoWriter;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.demographic.DemographicDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue.LabValueDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.medication.MedicationDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.vitalsign.VitalSignDocument;

@Component
@DependsOn(MONGO_TEMPLATE_MONGO_MIGRATOR)
public class MongoIndexConfiguration {

	@Autowired
	@Qualifier(MONGO_TEMPLATE_MONGO_MIGRATOR)
	private MongoTemplate mongoTemplate;

	@PostConstruct
	public void initIndices() {
		ensurePatientIdIndex();
		ensurePatientSearchCriteriaInformationIndex();
		ensureDemographicIndex();
		ensureMedicationIndex();
		ensureVitalSignIndex();
		ensureLabValueIndex();
	}

	private void ensurePatientIdIndex() {
		org.bson.Document indexOptions = new org.bson.Document();
		indexOptions.append(PatientSearchCriteriaInformationMongoWriter.FIELD_SOURCE, 1);
		indexOptions.append(PatientSearchCriteriaInformationMongoWriter.FIELD_IDENTIFIER, 1);
		CompoundIndexDefinition compoundIndexDefinition = new CompoundIndexDefinition(indexOptions);
		mongoTemplate.indexOps(PatientSearchCriteriaInformationMongoWriter.COLLECTION_NAME)
				.ensureIndex(compoundIndexDefinition.unique());
	}

	private void ensurePatientSearchCriteriaInformationIndex() {
		org.bson.Document indexOptions = new org.bson.Document();
		indexOptions.append(PatientSearchCriteriaInformationMongoWriter.FIELD_SOURCE, 1);
		indexOptions.append(PatientSearchCriteriaInformationMongoWriter.FIELD_IDENTIFIER, 1);
		indexOptions.append(PatientSearchCriteriaInformationMongoWriter.FIELD_BIRTH_DATE, 1);
		CompoundIndexDefinition compoundIndexDefinition = new CompoundIndexDefinition(indexOptions);
		mongoTemplate.indexOps(PatientSearchCriteriaInformationMongoWriter.COLLECTION_NAME)
				.ensureIndex(compoundIndexDefinition.unique());
	}

	private void ensureDemographicIndex() {
		mongoTemplate.indexOps(DemographicDocument.COLLECTION)
				.ensureIndex(new Index(DemographicDocument.SUBJECTID_FIELD, Sort.Direction.ASC));
	}

	private void ensureMedicationIndex() {
		mongoTemplate.indexOps(MedicationDocument.COLLECTION)
				.ensureIndex(new Index(MedicationDocument.SUBJECTID_FIELD, Sort.Direction.ASC));
	}

	private void ensureVitalSignIndex() {
		mongoTemplate.indexOps(VitalSignDocument.COLLECTION)
				.ensureIndex(new Index(VitalSignDocument.SUBJECTID_FIELD, Sort.Direction.ASC));
	}

	private void ensureLabValueIndex() {
		org.bson.Document indexOptions = new org.bson.Document();
		indexOptions.append(LabValueDocument.SUBJECTID_FIELD, 1);
		indexOptions.append(LabValueDocument.CONCEPT_FIELD, 1);
		indexOptions.append(LabValueDocument.STARTDATE_FIELD, -1);
		indexOptions.append(LabValueDocument.ENDDATE_FIELD, -1);
		mongoTemplate.indexOps(LabValueDocument.COLLECTION)
				.ensureIndex(new CompoundIndexDefinition(indexOptions));
	}
}
