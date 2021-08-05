package com.custodix.insite.local.ehr2edc.config;

import com.custodix.insite.local.ehr2edc.ehr.epic.config.EhrEpicConfiguration;
import com.custodix.insite.local.ehr2edc.ehr.fhir.config.EhrFhirConfiguration;
import com.custodix.insite.local.ehr2edc.ehr.mongo.config.EHRMongoConfiguration;
import com.custodix.insite.local.ehr2edc.infra.time.EHR2EDCTimeConfiguration;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirDstu2Configuration;
import com.custodix.insite.local.ehr2edc.query.mongo.MongoQueryDataConfiguration;
import com.custodix.insite.local.ehr2edc.shared.exceptions.conversion.EHR2EDCValidationConfiguration;
import com.custodix.insite.mongodb.export.patient.main.MongoMigratorConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ MongoMigratorConfiguration.class,
		  EHR2EDCAppConfig.class,
		  MongoQueryDataConfiguration.class,
		  FhirDstu2Configuration.class,
		  EHR2EDCValidationConfiguration.class,
		  EHR2EDCTimeConfiguration.class,
		  EHRMongoConfiguration.class,
		  EhrFhirConfiguration.class,
		  EhrEpicConfiguration.class
		})
public class EHR2EDCConfig {
}
