package com.custodix.insite.local.ehr2edc.query.populator;

import static java.util.UUID.randomUUID;

import java.util.List;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.metadata.model.ItemDefinition;
import com.custodix.insite.local.ehr2edc.populator.PopulatedItem;
import com.custodix.insite.local.ehr2edc.populator.PopulatedMeasurementUnitReference;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPointFactory;
import com.custodix.insite.local.ehr2edc.query.executor.common.domain.FormItem;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionStep;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemId;

class PopulatedItemFactory {

	private PopulatedItemFactory() {
	}

	static Optional<PopulatedItem> createItem(ItemDefinition itemDefinition, FormItem formItem,
			List<ProjectionStep> projectionSteps) {
		return Optional.ofNullable(formItem.getValue())
				.map(value -> doCreateItem(itemDefinition, formItem, value, projectionSteps));
	}

	private static PopulatedItem doCreateItem(ItemDefinition itemDefinition, FormItem formItem, LabeledValue value,
			List<ProjectionStep> projectionSteps) {
		ProvenanceDataPoint provenanceDataPoint = ProvenanceDataPointFactory.create(formItem.getDataPoint());
		return createItemBuilder(itemDefinition).withDataPoint(provenanceDataPoint)
				.withProjectionSteps(projectionSteps)
				.withValue(value)
				.withIndex(formItem.getIndex())
				.withMeasurementUnitReference(createPopulatedMeasurementUnitReference(formItem))
				.withReadOnly(formItem.isReadOnly())
				.withKey(formItem.isKey())
				.build();
	}

	private static PopulatedItem.Builder createItemBuilder(ItemDefinition itemDefinition) {
		return PopulatedItem.newBuilder()
				.withInstanceId(ItemId.of(randomUUID().toString()))
				.withId(itemDefinition.getId())
				.withLabel(itemDefinition.getLabel());
	}

	private static PopulatedMeasurementUnitReference createPopulatedMeasurementUnitReference(FormItem formItem) {
		if (formItem.getUnit() != null) {
			return PopulatedMeasurementUnitReference.newBuilder()
					.withId(formItem.getUnit())
					.withReadOnly(!formItem.isOutputUnit())
					.build();
		} else {
			return null;
		}
	}
}
