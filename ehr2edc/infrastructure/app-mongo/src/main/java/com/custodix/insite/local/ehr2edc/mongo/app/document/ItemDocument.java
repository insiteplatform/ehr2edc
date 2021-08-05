package com.custodix.insite.local.ehr2edc.mongo.app.document;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.metadata.model.ItemLabel;
import com.custodix.insite.local.ehr2edc.metadata.model.Question;
import com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.ProvenanceDataPointDocument;
import com.custodix.insite.local.ehr2edc.mongo.app.document.provenance.ProvenanceDataPointDocumentFactory;
import com.custodix.insite.local.ehr2edc.populator.PopulatedItem;
import com.custodix.insite.local.ehr2edc.populator.PopulatedMeasurementUnitReference;
import com.custodix.insite.local.ehr2edc.provenance.model.ProvenanceDataPoint;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionStep;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemId;

public final class ItemDocument {
	private final String instanceId;
	private final String id;
	private final String name;
	//Deprecated. Field 'value' is only here for backwards-compatibility
	private final String value;
	private final LabeledValueMongoSnapshot labeledValue;
	private final MeasurementUnitReferenceDocument measurementUnitReference;
	private final QuestionDocument question;
	private final boolean readOnly;
	private final boolean outputUnit;
	private final boolean key;
	private final ProvenanceDataPointDocument dataPoint;
	private final List<ProjectionStepDocument> projectionSteps;

	//CHECKSTYLE:OFF
	@PersistenceConstructor
	private ItemDocument(String instanceId, String id, final String name, String value,
			LabeledValueMongoSnapshot labeledValue, MeasurementUnitReferenceDocument measurementUnitReference,
			QuestionDocument question, Boolean readOnly, Boolean outputUnit, Boolean key,
			ProvenanceDataPointDocument dataPoint, List<ProjectionStepDocument> projectionSteps) {
		this.instanceId = instanceId;
		this.id = id;
		this.name = name;
		this.value = value;
		this.labeledValue = labeledValue;
		this.measurementUnitReference = measurementUnitReference;
		this.question = question;
		this.readOnly = Optional.ofNullable(readOnly)
				.orElse(false);
		this.outputUnit = Optional.ofNullable(outputUnit)
				.orElse(true);
		this.key = Optional.ofNullable(key)
				.orElse(false);
		this.dataPoint = dataPoint;
		this.projectionSteps = projectionSteps;
	}
	//CHECKSTYLE:ON

	private ItemDocument(Builder builder) {
		instanceId = builder.instanceId;
		id = builder.id;
		name = builder.name;
		value = builder.value;
		labeledValue = builder.labeledValue;
		measurementUnitReference = builder.measurementUnitReference;
		question = builder.question;
		readOnly = builder.readOnly;
		outputUnit = builder.outputUnit;
		key = builder.key;
		dataPoint = builder.dataPoint;
		projectionSteps = builder.projectionSteps;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getInstanceId() {
		return instanceId;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Optional<MeasurementUnitReferenceDocument> getMeasurementUnitReference() {
		return Optional.ofNullable(measurementUnitReference);
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public boolean isOutputUnit() {
		return outputUnit;
	}

	static List<ItemDocument> restoreFrom(final List<PopulatedItem> items) {
		return Optional.ofNullable(items)
				.orElse(emptyList())
				.stream()
				.map(ItemDocument::toItemMongoSnapshot)
				.collect(toList());
	}

	private static ItemDocument toItemMongoSnapshot(PopulatedItem i) {
		LabeledValueMongoSnapshot labeledValue = LabeledValueMongoSnapshot.toSnapshot(i.getValue());
		ProvenanceDataPointDocument dataPoint = ProvenanceDataPointDocumentFactory.create(i.getDataPoint());
		Builder builder = ItemDocument.newBuilder()
				.withInstanceId(i.getInstanceId()
						.getId())
				.withId(i.getId()
						.getId())
				.withValue(i.getValueCode())
				.withLabeledValue(labeledValue)
				.withName(i.getName())
				.withQuestion(getQuestion(i))
				.withReadOnly(i.isReadOnly())
				.withKey(i.isKey())
				.withDataPoint(dataPoint)
				.withProjectionSteps(getProjectionSteps(i));
		i.getMeasurementUnitReference()
				.ifPresent(
						unit -> builder.withMeasurementUnitReference(MeasurementUnitReferenceDocument.restoreFrom(unit))
								.withOutputUnit(!unit.isReadOnly()));
		return builder.build();
	}

	private static QuestionDocument getQuestion(PopulatedItem i) {
		return Optional.ofNullable(i.getQuestion())
				.map(Question::toSnapshot)
				.map(QuestionDocument::fromSnapshot)
				.orElse(null);
	}

	private static List<ProjectionStepDocument> getProjectionSteps(PopulatedItem i) {
		return nonNull(i.getProjectionSteps()) ?
				i.getProjectionSteps()
						.stream()
						.map(ProjectionStepDocument::from)
						.collect(toList()) :
				emptyList();
	}

	public PopulatedItem toItem() {
		return PopulatedItem.newBuilder()
				.withInstanceId(ItemId.of(instanceId))
				.withId(ItemDefinitionId.of(id))
				.withValue(getLabeledValue())
				.withMeasurementUnitReference(getUnitReference())
				.withLabel(getLabel())
				.withReadOnly(readOnly)
				.withKey(key)
				.withDataPoint(restoreDataPoint())
				.withProjectionSteps(restoreProjectionSteps())
				.build();
	}

	private List<ProjectionStep> restoreProjectionSteps() {
		return nonNull(projectionSteps) ?
				projectionSteps.stream()
						.map(ProjectionStepDocument::restore)
						.collect(toList()) :
				emptyList();
	}

	private LabeledValue getLabeledValue() {
		return Optional.ofNullable(labeledValue)
				.map(LabeledValueMongoSnapshot::restoreFrom)
				.orElse(new LabeledValue(value));
	}

	private PopulatedMeasurementUnitReference getUnitReference() {
		return getMeasurementUnitReference().map(unit -> unit.toMeasurementUnitReference(outputUnit))
				.orElse(null);
	}

	private ItemLabel getLabel() {
		return ItemLabel.newBuilder()
				.withName(name)
				.withQuestion(getQuestion())
				.build();
	}

	private Question getQuestion() {
		return Optional.ofNullable(question)
				.map(QuestionDocument::toSnapshot)
				.map(Question::restoreFrom)
				.orElse(null);
	}

	private ProvenanceDataPoint restoreDataPoint() {
		return Optional.ofNullable(dataPoint)
				.map(ProvenanceDataPointDocument::restore)
				.orElse(null);
	}

	public static final class Builder {
		private String id;
		private String name;
		private String value;
		private LabeledValueMongoSnapshot labeledValue;
		private MeasurementUnitReferenceDocument measurementUnitReference;
		private QuestionDocument question;
		private boolean readOnly;
		private boolean outputUnit;
		private boolean key;
		private String instanceId;
		private ProvenanceDataPointDocument dataPoint;
		private List<ProjectionStepDocument> projectionSteps;

		private Builder() {
		}

		public Builder withId(final String val) {
			id = val;
			return this;
		}

		public Builder withName(final String val) {
			name = val;
			return this;
		}

		public Builder withValue(final String val) {
			value = val;
			return this;
		}

		public Builder withLabeledValue(final LabeledValueMongoSnapshot val) {
			labeledValue = val;
			return this;
		}

		public Builder withMeasurementUnitReference(final MeasurementUnitReferenceDocument val) {
			measurementUnitReference = val;
			return this;
		}

		public Builder withQuestion(final QuestionDocument val) {
			question = val;
			return this;
		}

		public Builder withReadOnly(final boolean val) {
			readOnly = val;
			return this;
		}

		public Builder withOutputUnit(final boolean val) {
			outputUnit = val;
			return this;
		}

		public Builder withKey(boolean key) {
			this.key = key;
			return this;
		}

		public ItemDocument build() {
			return new ItemDocument(this);
		}

		public Builder withInstanceId(String instanceId) {
			this.instanceId = instanceId;
			return this;
		}

		public Builder withDataPoint(ProvenanceDataPointDocument val) {
			this.dataPoint = val;
			return this;
		}

		public Builder withProjectionSteps(List<ProjectionStepDocument> projectionSteps) {
			this.projectionSteps = unmodifiableList(projectionSteps);
			return this;
		}
	}
}
