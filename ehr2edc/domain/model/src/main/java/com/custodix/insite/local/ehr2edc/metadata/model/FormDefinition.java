package com.custodix.insite.local.ehr2edc.metadata.model;

import java.util.List;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.custodix.insite.local.ehr2edc.snapshots.FormDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.snapshots.ItemGroupDefinitionSnapshot;
import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.LabName;

public final class FormDefinition {
	private FormDefinitionId id;
	private String name;
	private List<ItemGroupDefinition> itemGroupDefinitions;
	private LabName localLab;

	public FormDefinition(FormDefinitionId id, String name, List<ItemGroupDefinition> itemGroupDefinitions,
			LabName localLab) {
		this.id = id;
		this.name = name;
		this.itemGroupDefinitions = itemGroupDefinitions;
		this.localLab = localLab;
	}

	private FormDefinition(final Builder builder) {
		id = builder.id;
		name = builder.name;
		itemGroupDefinitions = builder.itemGroupDefinitions;
		localLab = builder.localLab;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static List<FormDefinition> restoreFrom(List<FormDefinitionSnapshot> formDefinitionSnapshots) {
		return formDefinitionSnapshots == null ?
				null :
				formDefinitionSnapshots.stream()
						.map(FormDefinition::restoreFrom)
						.collect(Collectors.toList());
	}

	public static FormDefinition restoreFrom(FormDefinitionSnapshot formDefinitionSnapshot) {
		return new FormDefinition(formDefinitionSnapshot.getId(), formDefinitionSnapshot.getName(),
				ItemGroupDefinition.restoreFrom(formDefinitionSnapshot.getItemGroupDefinitions()),
				formDefinitionSnapshot.getLocalLab());
	}

	public FormDefinitionSnapshot toSnapshot() {
		return FormDefinitionSnapshot.newBuilder()
				.withId(id)
				.withName(name)
				.withItemGroupDefinitions(itemGroupsToSnapshot())
				.withLocalLab(localLab)
				.build();
	}

	public ItemGroupDefinition getItemGroupBy(final String itemGroupId) {
		return this.itemGroupDefinitions.stream()
				.filter(itemGroup -> itemGroup.getId().getId().equals(itemGroupId))
				.findFirst()
				.orElseThrow(() -> new SystemException(
						String.format(
								"Cannot find item group definition with id '%s' in a form definition with id '%s' ",
								itemGroupId,
								this.id)));
	}

	private List<ItemGroupDefinitionSnapshot> itemGroupsToSnapshot() {
		return itemGroupDefinitions == null ?
				null :
				itemGroupDefinitions.stream()
						.map(ItemGroupDefinition::toSnapshot)
						.collect(Collectors.toList());
	}

	public FormDefinitionId getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<ItemGroupDefinition> getItemGroupDefinitions() {
		return itemGroupDefinitions;
	}

	public LabName getLocalLab() {
		return localLab;
	}

	public static final class Builder {
		private FormDefinitionId id;
		private String name;
		private List<ItemGroupDefinition> itemGroupDefinitions;
		private LabName localLab;

		private Builder() {
		}

		public Builder withId(final FormDefinitionId val) {
			id = val;
			return this;
		}

		public Builder withName(final String val) {
			name = val;
			return this;
		}

		public Builder withItemGroupDefinitions(final List<ItemGroupDefinition> val) {
			itemGroupDefinitions = val;
			return this;
		}

		public Builder withLocalLab(final LabName val) {
			localLab = val;
			return this;
		}

		public FormDefinition build() {
			return new FormDefinition(this);
		}

	}
}
