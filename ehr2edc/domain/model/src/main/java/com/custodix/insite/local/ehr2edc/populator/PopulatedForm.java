package com.custodix.insite.local.ehr2edc.populator;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.submitted.SubmittedForm;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItemGroup;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public final class PopulatedForm {
	private final FormId instanceId;
	private final String name;
	private final FormDefinitionId formDefinitionId;
	private final List<PopulatedItemGroup> itemGroups;
	private final LabName localLab;

	private PopulatedForm(Builder builder) {
		instanceId = builder.instanceId;
		name = builder.name;
		formDefinitionId = builder.formDefinitionId;
		itemGroups = builder.itemGroups;
		localLab = builder.localLab;
	}

	public FormId getInstanceId() {
		return instanceId;
	}

	public String getName() {
		return name;
	}

	public FormDefinitionId getFormDefinitionId() {
		return formDefinitionId;
	}

	public List<PopulatedItemGroup> getItemGroups() {
		return itemGroups;
	}

	public boolean hasItems() {
		return getItemCount() > 0;
	}

	long getItemCount() {
		return itemGroups.stream()
				.map(PopulatedItemGroup::getItems)
				.mapToLong(Collection::size)
				.sum();
	}

	public LabName getLocalLab() {
		return localLab;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	SubmittedForm toReviewedForm(PopulatedFormSelection groupSelections) {
		return SubmittedForm.newBuilder()
				.withName(name)
				.withFormDefinitionId(formDefinitionId)
				.withSubmittedItemGroups(mapToReviewedItemGroups(groupSelections))
				.withPopulatedFormId(instanceId)
				.withLocalLab(groupSelections.labNameOrDefault(localLab))
				.build();
	}

	Optional<PopulatedItem> findItemById(ItemId itemId) {
		return itemGroups.stream()
				.map(g -> g.findItemById(itemId))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
	}

	private List<SubmittedItemGroup> mapToReviewedItemGroups(PopulatedFormSelection groupSelections) {
		Map<ItemGroupId, List<PopulatedItemSelection>> groupToSelectedItemsMapping = groupSelections.stream()
				.collect(toMap(PopulatedItemGroupSelection::getGroupId, PopulatedItemGroupSelection::getItemSelections));
		return itemGroups.stream()
				.filter(group -> groupToSelectedItemsMapping.containsKey(group.getInstanceId()))
				.map(group -> group.toReviewedItemGroup(groupToSelectedItemsMapping.get(group.getInstanceId())))
				.collect(toList());
	}

	public static final class Builder {
		private FormId instanceId;
		private String name;
		private FormDefinitionId formDefinitionId;
		private List<PopulatedItemGroup> itemGroups;
		private LabName localLab;

		private Builder() {
		}

		public Builder withInstanceId(FormId instanceId) {
			this.instanceId = instanceId;
			return this;
		}

		public Builder withName(final String val) {
			name = val;
			return this;
		}

		public Builder withFormDefinitionId(FormDefinitionId formDefinitionId) {
			this.formDefinitionId = formDefinitionId;
			return this;
		}

		public Builder withItemGroups(List<PopulatedItemGroup> itemGroups) {
			this.itemGroups = itemGroups;
			return this;
		}

		public Builder withLocalLab(final LabName val) {
			localLab = val;
			return this;
		}

		public PopulatedForm build() {
			return new PopulatedForm(this);
		}
	}
}
