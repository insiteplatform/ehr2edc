package com.custodix.insite.local.ehr2edc.usecase.impl.getformoverview;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Locale;

import com.custodix.insite.local.ehr2edc.populator.*;
import com.custodix.insite.local.ehr2edc.query.GetEvent;

final class FormOverviewFactory {
	private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

	GetEvent.Form createForm(PopulatedForm populatedForm, PopulatedEvent populatedEvent) {
		return GetEvent.Form.newBuilder()
				.withId(populatedForm.getInstanceId())
				.withName(populatedForm.getName())
				.withItemGroups(mapItemGroups(populatedForm.getItemGroups()))
				.withReferenceDate(populatedEvent.getReferenceDate())
				.withPopulationTime(populatedEvent.getPopulationTime())
				.build();
	}

	private List<GetEvent.ItemGroup> mapItemGroups(List<PopulatedItemGroup> itemGroups) {
		return itemGroups.stream()
				.map(this::mapItemGroup)
				.collect(toList());
	}

	private GetEvent.ItemGroup mapItemGroup(PopulatedItemGroup itemGroup) {
		return GetEvent.ItemGroup.newBuilder()
				.withId(itemGroup.getInstanceId())
				.withName(itemGroup.getName())
				.withItems(mapItems(itemGroup.getItems()))
				.build();
	}

	private List<GetEvent.Item> mapItems(List<PopulatedItem> items) {
		return items.stream()
				.map(this::mapItem)
				.collect(toList());
	}

	private GetEvent.Item mapItem(PopulatedItem item) {
		return GetEvent.Item.newBuilder()
				.withId(item.getInstanceId())
				.withName(item.getDisplayLabel(DEFAULT_LOCALE))
				.withValue(item.getValueCode())
				.withValueLabel(item.findValueLabel(DEFAULT_LOCALE)
						.orElse(null))
				.withUnit(mapUnit(item))
				.withExportable(!item.isReadOnly())
				.build();
	}

	private String mapUnit(PopulatedItem item) {
		return item.getMeasurementUnitReference()
				.map(PopulatedMeasurementUnitReference::getId)
				.orElse(null);
	}

}