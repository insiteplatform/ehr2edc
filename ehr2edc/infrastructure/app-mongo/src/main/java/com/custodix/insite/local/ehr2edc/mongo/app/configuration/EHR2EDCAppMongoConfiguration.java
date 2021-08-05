package com.custodix.insite.local.ehr2edc.mongo.app.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custodix.insite.local.ehr2edc.mongo.app.review.provenance.*;

@Configuration
public class EHR2EDCAppMongoConfiguration {

	@Bean
	ReviewedProvenanceDemographicDocumentMapper reviewedProvenanceDemographicDocumentMapper() {
		return new ReviewedProvenanceDemographicDocumentMapper();
	}

	@Bean
	ReviewedProvenanceNullDataPointMapper reviewedProvenanceNullDataPointMapper() {
		return new ReviewedProvenanceNullDataPointMapper();
	}

	@Bean
	ReviewedProvenanceLabValueDocumentMapper reviewedProvenanceLabValueDocumentMapper() {
		return new ReviewedProvenanceLabValueDocumentMapper();
	}

	@Bean
	ReviewedProvenanceMedicationDocumentMapper reviewedProvenanceMedicationDocumentMapper() {
		return new ReviewedProvenanceMedicationDocumentMapper();
	}

	@Bean
	ReviewedProvenanceVitalSignDocumentMapper reviewedProvenanceVitalSignDocumentMapper() {
		return new ReviewedProvenanceVitalSignDocumentMapper();
	}

	@Bean
	ReviewedProvenanceDataPointDocumentFactory reviewedProvenanceDataPointDocumentFactory(List<ReviewedProvenanceDataPointMapper> mappers) {
		return new ReviewedProvenanceDataPointDocumentFactory(mappers);
	}

}
