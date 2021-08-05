package com.custodix.insite.mongodb.export.patient.main;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.ExportPatientRunnerConfiguration;
import com.custodix.insite.mongodb.export.patient.infrastructure.scheduling.MongoMigratorTimeConfiguration;

@Configuration
@Import({ ActiveSubjectConfiguration.class,
		  ExportSubjectsConfiguration.class,
		  ExportPatientRunnerConfiguration.class,
		  ExportPatientsIdentificationsConfiguration.class,
		  MigratorMongoDBConfiguration.class,
		  SubjectMigrationConfiguration.class,
		  MongoMigratorTimeConfiguration.class
		})
public class MongoMigratorConfiguration {

}
