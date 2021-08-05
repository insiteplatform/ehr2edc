package com.custodix.insite.local.ehr2edc.query.mongo;

import java.util.List;

import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.populator.EHRGatewayFactory;

@Component
public class MongoEHRGatewayFactory implements EHRGatewayFactory {
	private final List<EHRGateway> mongoDocumentGateways;

	public MongoEHRGatewayFactory(List<EHRGateway> mongoDocumentGateways) {
		this.mongoDocumentGateways = mongoDocumentGateways;
	}

	@Override
	public boolean canHandle(Query query, PopulationSpecification specification) {
		throw new UnsupportedOperationException("");
	}

	@Override
	public EHRGateway create(Query query, PopulationSpecification specification) {
		return mongoDocumentGateways.stream()
				.filter(executor -> executor.canHandle(query))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Cannot find gateway for query"));
	}

}
