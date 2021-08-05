package com.custodix.insite.local.ehr2edc.query.populator;

import static com.custodix.insite.local.ehr2edc.query.populator.PopulatedItemGroupFactory.createItemGroup;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.metadata.model.ItemDefinition;
import com.custodix.insite.local.ehr2edc.metadata.model.ItemGroupDefinition;
import com.custodix.insite.local.ehr2edc.populator.PopulatedItem;
import com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroup;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.QueryResult;

final class ItemGroupPopulator {

	private final QueryFactory queryFactory;
	private final ItemPopulator itemPopulator;
	private final FormPopulatorLogger formPopulatorLogger;

	ItemGroupPopulator(QueryFactory queryFactory, PopulationSpecification specification,
			FormPopulatorLogger formPopulatorLogger) {
		this.queryFactory = queryFactory;
		this.itemPopulator = new ItemPopulator(queryFactory, specification, formPopulatorLogger);
		this.formPopulatorLogger = formPopulatorLogger;
	}

	List<PopulatedItemGroup> populateGroup(ItemGroupDefinition itemGroupDefinition, Map<Query, QueryResult> results) {
		return itemGroupDefinition.isRepeating() ?
				populateRepeatingGroup(itemGroupDefinition, results) :
				populateUniqueGroup(itemGroupDefinition, results);
	}

	private List<PopulatedItemGroup> populateUniqueGroup(ItemGroupDefinition itemGroupDefinition,
			Map<Query, QueryResult> results) {
		formPopulatorLogger.logPopulateSingularGroup(itemGroupDefinition);
		List<PopulatedItem> items = itemGroupDefinition.getItemDefinitions()
				.stream()
				.filter(queryFactory::canGenerateQuery)
				.map(item -> populateItem(item, results))
				.flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
				.collect(toList());
		return singletonList(createItemGroup(itemGroupDefinition, items));
	}

	private Optional<PopulatedItem> populateItem(ItemDefinition itemDefinition, Map<Query, QueryResult> results) {
		Query query = queryFactory.getQueryForItem(itemDefinition);
		Optional<DataPoint> result = results.get(query)
				.findResult();
		return itemPopulator.populateItem(itemDefinition, result.orElse(null));
	}

	/**
	 * Note, there are 2 pre-conditions to use repeating item groups:
	 * - all mapped item definitions in a repeating item group use the same query
	 * - the query returns 1 result per item group
	 */
	private List<PopulatedItemGroup> populateRepeatingGroup(ItemGroupDefinition itemGroupDefinition,
			Map<Query, QueryResult> results) {
		formPopulatorLogger.logPopulateRepeatingGroup(itemGroupDefinition);
		return Optional.of(results)
				.filter(resultMap -> !resultMap.isEmpty())
				.map(resultMap -> populateRepeatingGroupWithResults(itemGroupDefinition, resultMap))
				.orElse(emptyList());
	}

	private List<PopulatedItemGroup> populateRepeatingGroupWithResults(ItemGroupDefinition itemGroupDefinition,
			Map<Query, QueryResult> results) {
		Optional<Query> query = queryFactory.getQueryForRepeatingItemGroup(itemGroupDefinition, formPopulatorLogger);
		return query.map(results::get)
				.map(result -> populateRepeatingGroup(itemGroupDefinition, result))
				.orElse(emptyList());
	}

	private List<PopulatedItemGroup> populateRepeatingGroup(ItemGroupDefinition itemGroupDefinition, QueryResult queryResult) {
		formPopulatorLogger.logAmountOfQueryResults(queryResult);
		return queryResult.getResults()
				.stream()
				.peek(formPopulatorLogger::logPopulateItemGroupForResult)
				.map(result -> populateItems(itemGroupDefinition, result))
				.map(items -> createItemGroup(itemGroupDefinition, items))
				.collect(toList());
	}

	private List<PopulatedItem> populateItems(ItemGroupDefinition itemGroupDefinition, DataPoint dataPoint) {
		return itemGroupDefinition.getItemDefinitions()
				.stream()
				.filter(queryFactory::canGenerateQuery)
				.map(item -> itemPopulator.populateItem(item, dataPoint))
				.flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
				.collect(toList());
	}

}
