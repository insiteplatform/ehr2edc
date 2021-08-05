package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.populator.PopulatedForm;
import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.FormId;
import com.custodix.insite.local.ehr2edc.vocabulary.LabName;

public final class FormDocument {
	private final String instanceId;
	private final String name;
	private final String formDefinitionId;
	private final Collection<ItemGroupDocument> itemGroups;
	private final String localLab;

	//CHECKSTYLE:OFF
	@PersistenceConstructor
	private FormDocument(final String instanceId, final String name, final String formDefinitionId,
			final Collection<ItemGroupDocument> itemGroups, final String localLab) {
		this.instanceId = instanceId;
		this.name = name;
		this.formDefinitionId = formDefinitionId;
		this.itemGroups = itemGroups;
		this.localLab = localLab;
	}
	//CHECKSTYLE:ON

	private FormDocument(final Builder builder) {
		instanceId = builder.instanceId;
		name = builder.name;
		formDefinitionId = builder.formDefinitionId;
		itemGroups = builder.itemGroups;
		localLab = builder.localLab;
	}

	public static FormDocument restoreFrom(final PopulatedForm populatedForm) {
		return FormDocument.newBuilder()
				.withInstanceId(populatedForm.getInstanceId()
						.getId())
				.withFormDefinitionId(populatedForm.getFormDefinitionId()
						.getId())
				.withName(populatedForm.getName())
				.withItemGroups(ItemGroupDocument.restoreFrom(populatedForm.getItemGroups()))
				.withLocalLab(getLocalLab(populatedForm))
				.build();
	}

	private static String getLocalLab(PopulatedForm populatedForm) {
		if (populatedForm.getLocalLab() == null) {
			return null;
		}
		return populatedForm.getLocalLab()
				.getName();
	}

	public PopulatedForm toForm() {
		return PopulatedForm.newBuilder()
				.withInstanceId(FormId.of(instanceId))
				.withName(name)
				.withFormDefinitionId(FormDefinitionId.of(formDefinitionId))
				.withItemGroups(itemGroups.stream()
						.map(ItemGroupDocument::toItemGroup)
						.collect(Collectors.toList()))
				.withLocalLab(localLab != null ? LabName.of(localLab) : null)
				.build();
	}

	public String getInstanceId() {
		return instanceId;
	}

	public String getName() {
		return name;
	}

	public String getFormDefinitionId() {
		return formDefinitionId;
	}

	public Collection<ItemGroupDocument> getItemGroups() {
		return itemGroups;
	}

	public String getLocalLab() {
		return localLab;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String instanceId;
		private String name;
		private String formDefinitionId;
		private Collection<ItemGroupDocument> itemGroups;
		private String localLab;

		private Builder() {
		}

		public Builder withInstanceId(final String val) {
			instanceId = val;
			return this;
		}

		public Builder withName(final String val) {
			name = val;
			return this;
		}

		public Builder withFormDefinitionId(final String val) {
			formDefinitionId = val;
			return this;
		}

		public Builder withItemGroups(final Collection<ItemGroupDocument> val) {
			itemGroups = val;
			return this;
		}

		public Builder withLocalLab(final String val) {
			localLab = val;
			return this;
		}

		public FormDocument build() {
			return new FormDocument(this);
		}
	}
}
