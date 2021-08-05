package com.custodix.insite.local.ehr2edc.query.populator;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import com.custodix.insite.local.ehr2edc.metadata.model.FormDefinition;
import com.custodix.insite.local.ehr2edc.populator.PopulatedItem;
import com.custodix.insite.local.ehr2edc.populator.PopulatedItemGroup;
import com.custodix.insite.local.ehr2edc.populator.PopulatedMeasurementUnitReference;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.QueryResult;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId;

final class QueryResults {
	private final Map<Query, QueryResult> queryResultMap;
	private final ItemGroupPopulator itemGroupPopulator;

	QueryResults(Map<Query, QueryResult> queryResultMap, ItemGroupPopulator itemGroupPopulator) {
		this.queryResultMap = queryResultMap;
		this.itemGroupPopulator = itemGroupPopulator;
	}

	List<PopulatedItemGroup> getItemGroups(FormDefinition formDefinition, FormPopulatorLogger formPopulatorLogger) {
		List<PopulatedItemGroup> itemGroups = emptyList();
		if (!queryResultMap.isEmpty()) {
			itemGroups = formDefinition.getItemGroupDefinitions()
					.stream()
					.map(group -> itemGroupPopulator.populateGroup(group, queryResultMap))
					.flatMap(Collection::stream)
					.collect(toList());
		}
		return itemGroups.stream()
				.filter(distinctByKey(ItemGroupKey::new))
				.filter(PopulatedItemGroup::isNotReadOnly)
				.filter(PopulatedItemGroup::isNotEmpty)
				.collect(toList());
	}

	private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) 	{
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	private static final class ItemGroupKey {
		private final ItemGroupDefinitionId itemGroupDefinitionId;
		private final String index;
		private final List<ItemKey> itemKeys;

		private ItemGroupKey(PopulatedItemGroup itemGroup) {
			itemGroupDefinitionId =  itemGroup.getDefinition().getId();
			index =  itemGroup.getIndex();
			itemKeys = itemGroup.getItems().stream().map(ItemKey::new).collect(toList());
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			final ItemGroupKey that = (ItemGroupKey) o;
			return itemGroupDefinitionId.equals(that.itemGroupDefinitionId) && Objects.equals(index, that.index) && Objects.equals(itemKeys, that.itemKeys);
		}

		@Override
		public int hashCode() {
			return Objects.hash(itemGroupDefinitionId, index, itemKeys);
		}

		private static final class ItemKey {
			private final String id;
			private final LabeledValue value;
			private final String measurementUnitReferenceId;

			private ItemKey(PopulatedItem item) {
				this.id = item.getId().getId();
				this.value = item.getValue();
				this.measurementUnitReferenceId = item.getMeasurementUnitReference().map(
						PopulatedMeasurementUnitReference::getId).orElse(null);
			}

			@Override
			public boolean equals(final Object o) {
				if (this == o) {
					return true;
				}
				if (o == null || getClass() != o.getClass()) {
					return false;
				}
				final ItemKey itemKey = (ItemKey) o;
				return id.equals(itemKey.id) && value.equals(itemKey.value) && Objects.equals(measurementUnitReferenceId, itemKey.measurementUnitReferenceId);
			}

			@Override
			public int hashCode() {
				return Objects.hash(id, value, measurementUnitReferenceId);
			}
		}
	}
}
