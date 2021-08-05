package com.custodix.insite.local.ehr2edc.submitted;

import java.util.Locale;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedItemId;

public final class SubmittedItem {
	private final SubmittedItemId instanceId;
	private final ItemDefinitionId id;
	private final ProvenanceDataPoint dataPoint;
	private final SubmittedLabeledValue value;
	private final SubmittedMeasurementUnitReference submittedMeasurementUnitReference;
	private final SubmittedItemLabel label;
	private final ItemId populatedItemId;
	private final boolean submittedToEDC;
	private final boolean key;

	private SubmittedItem(Builder builder) {
		instanceId = builder.instanceId;
		id = builder.id;
		dataPoint = builder.dataPoint;
		value = builder.value;
		submittedMeasurementUnitReference = builder.submittedMeasurementUnitReference;
		key = builder.key;
		label = builder.label;
		populatedItemId = builder.populatedItemId;
		submittedToEDC = builder.submittedToEDC;
	}

	public ProvenanceDataPoint getDataPoint() {
		return dataPoint;
	}

	public SubmittedItemId getInstanceId() {
		return instanceId;
	}

	public ItemDefinitionId getId() {
		return id;
	}

	public SubmittedLabeledValue getLabeledValue() {
		return value;
	}

	public Optional<SubmittedMeasurementUnitReference> getReviewedMeasurementUnitReference() {
		return Optional.ofNullable(submittedMeasurementUnitReference);
	}

	public boolean isKey() {
		return key;
	}

	public boolean isSubmittedToEDC() {
		return submittedToEDC;
	}

	public Optional<SubmittedItemLabel> getLabel() {
		return Optional.ofNullable(label);
	}

	public ItemId getPopulatedItemId() {
		return populatedItemId;
	}

	public String getDisplayLabel(Locale locale) {
		return Optional.ofNullable(label).map(l -> l.getRepresentationFor(locale)).orElse(null);
	}

	public String getDisplayValue(Locale locale) {
		return Optional.ofNullable(value)
				.map(l -> l.findLabelFor(locale)
						).orElse(null);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private SubmittedItemId instanceId;
		private ItemDefinitionId id;
		private ProvenanceDataPoint dataPoint;
		private SubmittedLabeledValue value;
		private SubmittedMeasurementUnitReference submittedMeasurementUnitReference;
		private boolean key;
		private SubmittedItemLabel label;
		private ItemId populatedItemId;
		private boolean submittedToEDC;

		private Builder() {
		}

		public Builder withInstanceId(SubmittedItemId val) {
			instanceId = val;
			return this;
		}

		public Builder withId(ItemDefinitionId val) {
			id = val;
			return this;
		}

		public Builder withDataPoint(ProvenanceDataPoint val) {
			dataPoint = val;
			return this;
		}

		public Builder withValue(SubmittedLabeledValue val) {
			value = val;
			return this;
		}

		public Builder withSubmittedMeasurementUnitReference(SubmittedMeasurementUnitReference val) {
			submittedMeasurementUnitReference = val;
			return this;
		}

		public Builder withKey(boolean val) {
			key = val;
			return this;
		}

		public Builder withLabel(SubmittedItemLabel val) {
			label = val;
			return this;
		}

		public Builder withPopulatedItemId(ItemId val) {
			populatedItemId = val;
			return this;
		}

		public Builder withSubmittedToEDC(boolean val) {
			submittedToEDC = val;
			return this;
		}

		public SubmittedItem build() {
			return new SubmittedItem(this);
		}
	}
}
