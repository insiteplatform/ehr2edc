package com.custodix.insite.local.ehr2edc.query.populator;

import java.util.List;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.metadata.model.ItemDefinition;
import com.custodix.insite.local.ehr2edc.populator.PopulatedItem;
import com.custodix.insite.local.ehr2edc.populator.PopulationSpecification;
import com.custodix.insite.local.ehr2edc.query.executor.common.domain.FormItem;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.formitem.ProjectedValueToFormItem;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.ProjectedValueField;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.*;
import com.custodix.insite.local.ehr2edc.querymapping.model.ItemQueryMapping;

class ItemPopulator {

	private QueryFactory queryFactory;
	private PopulationSpecification specification;
	private final FormPopulatorLogger formPopulatorLogger;

	ItemPopulator(QueryFactory queryFactory, PopulationSpecification specification,
			FormPopulatorLogger formPopulatorLogger) {
		this.queryFactory = queryFactory;
		this.specification = specification;
		this.formPopulatorLogger = formPopulatorLogger;
	}

	Optional<PopulatedItem> populateItem(ItemDefinition itemDefinition, DataPoint queryResult) {
		formPopulatorLogger.logPopulateItem(itemDefinition);
		ItemQueryMapping mapping = queryFactory.itemQueryMappingFor(itemDefinition);
		ProjectedDataPoint projectedResult = project(queryResult, mapping);
		return Optional.ofNullable(projectedResult)
				.flatMap(result -> toPopulatedItem(itemDefinition, queryResult, result));
	}

	private ProjectedDataPoint project(DataPoint dataPoint, ItemQueryMapping itemQueryMapping) {
		return ProjectionChain.of(itemQueryMapping.getProjectors())
				.project(dataPoint, createProjectionContext(dataPoint));
	}

	private ProjectionContext createProjectionContext(DataPoint dataPoint) {
		return ProjectionContext.newBuilder()
				.withDataPoint(dataPoint)
				.withSubjectId(specification.getSubjectId())
				.withEdcSubjectReference(specification.getEdcSubjectReference())
				.withReferenceDate(specification.getReferenceDate())
				.withConsentDate(specification.getConsentDate())
				.build();
	}

	private Optional<PopulatedItem> toPopulatedItem(ItemDefinition itemDefinition, DataPoint queryResult,
			ProjectedDataPoint projectedDataPoint) {
		FormItem formItem = getFormItem(projectedDataPoint.getResult(), queryResult);
		List<ProjectionStep> steps = projectedDataPoint.getProjectionSteps();
		return PopulatedItemFactory.createItem(itemDefinition, formItem, steps);
	}

	private FormItem getFormItem(Object result, DataPoint dataPoint) {
		if (result instanceof FormItem) {
			return (FormItem) result;
		} else {
			return mapToFormItem(result, dataPoint);
		}
	}

	private FormItem mapToFormItem(Object result, DataPoint dataPoint) {
		ProjectedValueToFormItem projectedValueToFormItem = new ProjectedValueToFormItem(null,
				ProjectedValueField.VALUE, ProjectedValueToFormItem.UnitMapping.IGNORE, false, false);
		return projectedValueToFormItem.project(toProjectedValue(result), createProjectionContext(dataPoint))
				.orElse(FormItem.newBuilder()
						.build());
	}

	private Optional<ProjectedValue> toProjectedValue(Object p) {
		return Optional.of(new ProjectedValue(p, null, null));
	}
}
