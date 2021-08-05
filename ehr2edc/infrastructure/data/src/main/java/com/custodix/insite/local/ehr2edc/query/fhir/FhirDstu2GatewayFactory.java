package com.custodix.insite.local.ehr2edc.query.fhir;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.QueryResult;

public interface FhirDstu2GatewayFactory<T extends QueryResult, Q extends Query<T>> {

	boolean canHandle(Query<?> query);

	EHRGateway<T, Q> create(Q query, PopulationSpecification specification);
}
