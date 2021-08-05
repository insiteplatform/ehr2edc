package com.custodix.insite.local.ehr2edc.query.populator;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;

public interface EHRGatewayFactory {
	boolean canHandle(Query query, PopulationSpecification specification);

	EHRGateway create(Query query, PopulationSpecification specification);
}
