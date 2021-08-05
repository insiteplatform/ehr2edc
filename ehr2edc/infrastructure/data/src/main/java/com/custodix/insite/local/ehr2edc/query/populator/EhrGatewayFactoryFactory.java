package com.custodix.insite.local.ehr2edc.query.populator;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.fhir.FhirEHRGatewayFactory;
import com.custodix.insite.local.ehr2edc.query.mongo.MongoEHRGatewayFactory;

class EhrGatewayFactoryFactory {
	private final FhirEHRGatewayFactory fhirEHRGatewayFactory;
	private final MongoEHRGatewayFactory mongoEHRGatewayFactory;

	EhrGatewayFactoryFactory(FhirEHRGatewayFactory fhirEHRGatewayFactory, MongoEHRGatewayFactory mongoEHRGatewayFactory) {
		this.fhirEHRGatewayFactory = fhirEHRGatewayFactory;
		this.mongoEHRGatewayFactory = mongoEHRGatewayFactory;
	}

	EHRGateway selectGateway(final Query query, PopulationSpecification specification) {
		if (fhirEHRGatewayFactory.canHandle(query, specification)) {
			return fhirEHRGatewayFactory.create(query, specification);
		}
		return mongoEHRGatewayFactory.create(query, specification);
	}
}
