package com.custodix.insite.local.ehr2edc.query.populator;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.RecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.custodix.insite.local.ehr2edc.metadata.model.FormDefinition;
import com.custodix.insite.local.ehr2edc.metadata.model.ItemDefinition;
import com.custodix.insite.local.ehr2edc.metadata.model.ItemGroupDefinition;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.QueryResult;

final class FormPopulatorLogger {
	private static final String MDC_KEY_SUBJECT_ID = "subjectId";
	private static final String MDC_KEY_TRACKINGID = "trackingId";

	private static final Logger LOGGER = LoggerFactory.getLogger(FormPopulatorLogger.class);

	void logQueriesInItemGroup(Set<Query> queriesOccurringInItemGroup) {
		logTrace(() -> LOGGER.trace("There are {} queries in item group which are: {}",
				queriesOccurringInItemGroup.size(), queriesOccurringInItemGroup.stream()
						.map(this::reflectionToString)
						.collect(Collectors.joining())));

	}

	void logAmountOfQueryResults(QueryResult queryResult) {
		logTrace(() -> LOGGER.trace("Query has {} results which are: {}", queryResult.getResults()
				.size(), reflectionToString(queryResult)));
	}

	void logFoundMapping(String itemDefinitionId) {
		logTrace(() -> LOGGER.trace("Found mapping for {}", itemDefinitionId));
	}

	void logQueryRun(Query query, QueryResult result) {
		logTrace(() -> LOGGER.trace("Ran query {} with result {} ", reflectionToString(query),
				reflectionToString(result)));
	}

	void startLogging(FormDefinition formDefinition, PopulationSpecification specification) {
		MDC.put(MDC_KEY_SUBJECT_ID, specification.getSubjectId()
				.getId());
		MDC.put(MDC_KEY_TRACKINGID, UUID.randomUUID()
				.toString());
		logTrace(() -> LOGGER.trace("========================================================================="));
		logTrace(() -> {
			LOGGER.trace("Populating form for: {} ({})", formDefinition.getName(), formDefinition.getId()
					.getId());
		});
		logTrace(() -> LOGGER.trace("========================================================================="));
	}

	void logPopulateSingularGroup(ItemGroupDefinition itemGroupDefinition) {
		logTrace(() -> LOGGER.trace("Populating singular group: {}", itemGroupDefinition.getId()));
	}

	void logPopulateRepeatingGroup(ItemGroupDefinition itemGroupDefinition) {
		logTrace(() -> LOGGER.trace("Populating repeating group: {}", itemGroupDefinition.getId()));
	}

	void logPopulateItemGroupForResult(DataPoint result) {
		logTrace(() -> LOGGER.trace("Populating repeating group with result: {}", result));
	}

	void logPopulateItem(ItemDefinition itemDefinition) {
		logTrace(() -> LOGGER.trace("Populating item: {}", itemDefinition.getId()));
	}

	private void logTrace(Runnable runnable) {
		if (LOGGER.isTraceEnabled()) {
			runnable.run();
		}
	}

	void stopLogging() {
		MDC.remove(MDC_KEY_SUBJECT_ID);
		MDC.remove(MDC_KEY_TRACKINGID);
	}

	private String reflectionToString(Object object) {
		return ReflectionToStringBuilder.toString(object, new OwnRecursiveToString());
	}

	private final class OwnRecursiveToString extends RecursiveToStringStyle {
		OwnRecursiveToString() {
			super();
			setUseShortClassName(true);
			setUseIdentityHashCode(false);
		}
	}
}
