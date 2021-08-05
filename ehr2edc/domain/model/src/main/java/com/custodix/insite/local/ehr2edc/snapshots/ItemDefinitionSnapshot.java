package com.custodix.insite.local.ehr2edc.snapshots;

import java.util.List;

import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;

public final class ItemDefinitionSnapshot {
	private final ItemDefinitionId id;
	private final String dataType;
	private final Integer length;
	private final CodeListSnapshot codeList;
	private final List<MeasurementUnitSnapshot> measurementUnits;
	private final ItemLabelSnapshot label;

	private ItemDefinitionSnapshot(final Builder builder) {
		id = builder.id;
		dataType = builder.dataType;
		length = builder.length;
		codeList = builder.codeList;
		measurementUnits = builder.measurementUnits;
		label = builder.label;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public ItemDefinitionId getId() {
		return id;
	}

	public String getDataType() {
		return dataType;
	}

	public Integer getLength() {
		return length;
	}

	public CodeListSnapshot getCodeList() {
		return codeList;
	}

	public List<MeasurementUnitSnapshot> getMeasurementUnits() {
		return measurementUnits;
	}

	public ItemLabelSnapshot getLabel() {
		return label;
	}

	@Deprecated //maintained for MongoItemDefinitionSnapshot
	public String getName() {
		return label.getName();
	}

	@Deprecated //maintained for MongoItemDefinitionSnapshot
	public QuestionSnapshot getQuestion() {
		return label.getQuestion();
	}

	public static final class Builder {
		private ItemDefinitionId id;
		private String dataType;
		private Integer length;
		private CodeListSnapshot codeList;
		private List<MeasurementUnitSnapshot> measurementUnits;
		private ItemLabelSnapshot label;

		private Builder() {
		}

		public Builder withId(final ItemDefinitionId val) {
			id = val;
			return this;
		}

		public Builder withDataType(final String val) {
			dataType = val;
			return this;
		}

		public Builder withLength(final Integer val) {
			length = val;
			return this;
		}

		public Builder withCodeList(final CodeListSnapshot val) {
			codeList = val;
			return this;
		}

		public Builder withMeasurementUnits(final List<MeasurementUnitSnapshot> val) {
			measurementUnits = val;
			return this;
		}

		public Builder withLabel(ItemLabelSnapshot val) {
			label = val;
			return this;
		}

		public ItemDefinitionSnapshot build() {
			return new ItemDefinitionSnapshot(this);
		}
	}
}
