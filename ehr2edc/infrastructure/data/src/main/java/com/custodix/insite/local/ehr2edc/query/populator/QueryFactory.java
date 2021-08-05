package com.custodix.insite.local.ehr2edc.query.populator;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.custodix.insite.local.ehr2edc.metadata.model.FormDefinition;
import com.custodix.insite.local.ehr2edc.metadata.model.ItemDefinition;
import com.custodix.insite.local.ehr2edc.metadata.model.ItemGroupDefinition;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion;
import com.custodix.insite.local.ehr2edc.querymapping.model.ItemQueryMapping;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

class QueryFactory {
	private final Map<ItemDefinitionId, ItemQueryMapping> studyItemQueryMappings;
	private final SubjectId subjectId;
	private final PatientCDWReference patientCDWReference;

	QueryFactory(Map<ItemDefinitionId, ItemQueryMapping> studyItemQueryMappings, SubjectId subjectId,
			PatientCDWReference patientCDWReference) {
		this.studyItemQueryMappings = studyItemQueryMappings;
		this.subjectId = subjectId;
		this.patientCDWReference = patientCDWReference;
	}

	Set<Query> getQueriesOccurringInForm(FormDefinition formDefinition, FormPopulatorLogger formPopulatorLogger) {
		Set<Query> queries = new HashSet<>();
		for (ItemGroupDefinition itemGroupDefinition : formDefinition.getItemGroupDefinitions()) {
			queries.addAll(getQueriesOccurringInItemGroup(itemGroupDefinition, formPopulatorLogger));
		}
		return queries;
	}

	Optional<Query> getQueryForRepeatingItemGroup(ItemGroupDefinition itemGroupDefinition,
			FormPopulatorLogger formPopulatorLogger) {
		Set<Query> queries = getQueriesOccurringInItemGroup(itemGroupDefinition, formPopulatorLogger);
		formPopulatorLogger.logQueriesInItemGroup(queries);
		return queries.stream().findFirst();
	}

	private Set<Query> getQueriesOccurringInItemGroup(ItemGroupDefinition itemGroupDefinition,
			FormPopulatorLogger formPopulatorLogger) {
		Set<Query> queries = new HashSet<>();
		for (ItemDefinition itemDefinition : itemGroupDefinition.getItemDefinitions()) {
			getQueryForItem(formPopulatorLogger, itemDefinition).ifPresent(queries::add);
		}
		return queries;
	}

	private Optional<Query> getQueryForItem(FormPopulatorLogger formPopulatorLogger, ItemDefinition itemDefinition) {
		if (hasNoQueryMappingFor(itemDefinition)) {
			return Optional.empty();
		}
		ItemQueryMapping mapping = studyItemQueryMappings.get(itemDefinition.getId());
		formPopulatorLogger.logFoundMapping(itemDefinition.getId()
				.getId());
		return Optional.of(configureQueryForSubject(mapping.getQuery()));
	}

	private boolean hasNoQueryMappingFor(ItemDefinition itemDefinition) {
		return !studyItemQueryMappings.containsKey(itemDefinition.getId());
	}

	Query getQueryForItem(ItemDefinition itemDefinition) {
		return configureQueryForSubject(itemQueryMappingFor(itemDefinition).getQuery());
	}

	private Query configureQueryForSubject(Query query) {
		return query.forSubject(SubjectCriterion.subjectIs(subjectId, patientCDWReference));
	}

	boolean canGenerateQuery(ItemDefinition itemDefinition) {
		return studyItemQueryMappings.containsKey(itemDefinition.getId());
	}

	ItemQueryMapping itemQueryMappingFor(ItemDefinition itemDefinition) {
		return studyItemQueryMappings.get(itemDefinition.getId());
	}
}
