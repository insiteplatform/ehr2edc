package com.custodix.insite.local.ehr2edc.query.populator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.custodix.insite.local.ehr2edc.metadata.model.FormDefinition;
import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroup;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.QueryResult;

class QueryResultGenerator {
	private final PopulationSpecification specification;
	private final EhrGatewayFactoryFactory ehrGatewayFactory;
	private final FormPopulatorLogger formPopulatorLogger;

	QueryResultGenerator(PopulationSpecification specification, FormPopulatorLogger formPopulatorLogger,
			EhrGatewayFactoryFactory ehrGatewayFactory) {
		this.specification = specification;
		this.ehrGatewayFactory = ehrGatewayFactory;
		this.formPopulatorLogger = formPopulatorLogger;
	}

	List<PopulatedItemGroup> calculateResults(FormDefinition formDefinition, QueryFactory queryFactory) {
		final Map<Query, QueryResult> queryResultMap = calculateResults(
				queryFactory.getQueriesOccurringInForm(formDefinition, formPopulatorLogger));
		ItemGroupPopulator itemGroupPopulator = new ItemGroupPopulator(queryFactory, specification, formPopulatorLogger);
		QueryResults queryResults = new QueryResults(queryResultMap, itemGroupPopulator);
		return queryResults.getItemGroups(formDefinition, formPopulatorLogger);
	}

	private Map<Query, QueryResult> calculateResults(Set<Query> queries) {
		Map<Query, QueryResult> results = new HashMap<>();
		for (Query query : queries) {
			QueryResult result = getQueryResult(query);
			formPopulatorLogger.logQueryRun(query, result);
			results.put(query, result);
		}
		return results;
	}

	private QueryResult getQueryResult(final Query query) {
		EHRGateway ehrGateway = ehrGatewayFactory.selectGateway(query, specification);
		return ehrGateway.execute(query, specification.getReferenceDate());
	}

}
