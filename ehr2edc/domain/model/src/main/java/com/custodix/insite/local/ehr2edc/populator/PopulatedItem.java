package com.custodix.insite.local.ehr2edc.populator;

import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.metadata.model.ItemLabel;
import com.custodix.insite.local.ehr2edc.metadata.model.Question;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.Label;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionStep;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedItem;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedLabel;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedLabeledValue;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedItemId;

public final class PopulatedItem {
	private final ItemId instanceId;
	private final ItemDefinitionId id;
	private final ProvenanceDataPoint dataPoint;
	private final List<ProjectionStep> projectionSteps;
	private final LabeledValue value;
	private final PopulatedMeasurementUnitReference measurementUnitReference;
	private final String index;
	private final ItemLabel label;
	private final boolean readOnly;
	private final boolean key;

	private PopulatedItem(Builder builder) {
		instanceId = builder.instanceId;
		id = builder.id;
		dataPoint = builder.dataPoint;
		projectionSteps = builder.projectionSteps;
		value = builder.value;
		measurementUnitReference = builder.measurementUnitReference;
		index = builder.index;
		label = builder.label;
		readOnly = builder.readOnly;
		key = builder.key;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public ItemId getInstanceId() {
		return instanceId;
	}

	public ItemDefinitionId getId() {
		return id;
	}

	@Deprecated
	public String getName() {
		return label.getName();
	}

	public ProvenanceDataPoint getDataPoint() {
		return dataPoint;
	}

	public List<ProjectionStep> getProjectionSteps() {
		return projectionSteps;
	}

	public LabeledValue getValue() {
		return value;
	}

	public String getValueCode() {
		return value.getValue();
	}

	public Optional<String> findValueLabel(Locale locale) {
		return value.findLabelFor(locale);
	}

	public Optional<PopulatedMeasurementUnitReference> getMeasurementUnitReference() {
		return Optional.ofNullable(measurementUnitReference);
	}

	public String getIndex() {
		return index;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public boolean isKey() {
		return key;
	}

	SubmittedItem toReviewedItem() {
		return SubmittedItem.newBuilder()
				.withInstanceId(SubmittedItemId.newId())
				.withId(id)
				.withValue(toReviewedLabeledValue(value))
				.withKey(key)
				.withSubmittedToEDC(!readOnly)
				.withPopulatedItemId(instanceId)
				.withLabel(Optional.ofNullable(label)
						.map(ItemLabel::toReviewedLabel)
						.orElse(null))
				.withDataPoint(dataPoint)
				.withSubmittedMeasurementUnitReference(Optional.ofNullable(measurementUnitReference)
						.map(PopulatedMeasurementUnitReference::toReviewed)
						.orElse(null))
				.build();
	}

	private SubmittedLabeledValue toReviewedLabeledValue(LabeledValue value) {
		return SubmittedLabeledValue.newBuilder()
				.withValue(value.getValue())
				.withLabels(value.getLabels()
						.stream()
						.map(this::toReviewLabel)
						.collect(Collectors.toList()))
				.build();
	}

	private SubmittedLabel toReviewLabel(Label label) {
		return SubmittedLabel.newBuilder()
				.withLocale(label.getLocale())
				.withText(label.getText())
				.build();
	}

	public String getDisplayLabel(Locale locale) {
		return label.getRepresentationFor(locale);
	}

	@Deprecated
	public Question getQuestion() {
		return label.getQuestion();
	}

	public static final class Builder {
		private ItemId instanceId;
		private ItemDefinitionId id;
		private ProvenanceDataPoint dataPoint;
		private List<ProjectionStep> projectionSteps;
		private LabeledValue value;
		private PopulatedMeasurementUnitReference measurementUnitReference;
		private String index;
		private ItemLabel label;
		private boolean readOnly;
		private boolean key;

		private Builder() {
		}

		public Builder withInstanceId(final ItemId val) {
			instanceId = val;
			return this;
		}

		public Builder withId(ItemDefinitionId id) {
			this.id = id;
			return this;
		}

		public Builder withDataPoint(ProvenanceDataPoint val) {
			this.dataPoint = val;
			return this;
		}

		public Builder withProjectionSteps(List<ProjectionStep> projectionSteps) {
			this.projectionSteps = unmodifiableList(projectionSteps);
			return this;
		}

		public Builder withValue(final LabeledValue val) {
			value = val;
			return this;
		}

		public Builder withMeasurementUnitReference(final PopulatedMeasurementUnitReference measurementUnitReference) {
			this.measurementUnitReference = measurementUnitReference;
			return this;
		}

		public Builder withIndex(String val) {
			index = val;
			return this;
		}

		public Builder withLabel(ItemLabel val) {
			label = val;
			return this;
		}

		public Builder withReadOnly(boolean val) {
			readOnly = val;
			return this;
		}

		public Builder withKey(boolean key) {
			this.key = key;
			return this;
		}

		public PopulatedItem build() {
			return new PopulatedItem(this);
		}
	}
}
