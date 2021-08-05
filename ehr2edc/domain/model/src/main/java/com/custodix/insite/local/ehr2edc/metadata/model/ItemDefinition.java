package com.custodix.insite.local.ehr2edc.metadata.model;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.snapshots.*;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;

public final class ItemDefinition {
	private final ItemDefinitionId id;
	private final String dataType;
	private final Integer length;
	private final CodeList codeList;
	private final List<MeasurementUnit> measurementUnits;
	private final ItemLabel label;

	public ItemDefinition(CreateItem createItem) {
		this.id = createItem.id;
		this.dataType = createItem.dataType;
		this.length = createItem.length;
		this.codeList = createItem.codeList;
		this.measurementUnits = createItem.measurementUnits;
		this.label = createItem.label;
	}

	private ItemDefinition(Builder builder) {
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

	public static List<ItemDefinition> restoreFrom(List<ItemDefinitionSnapshot> items) {
		return items == null ?
				null :
				items.stream()
						.map(ItemDefinition::restoreFrom)
						.collect(Collectors.toList());
	}

	public static ItemDefinition restoreFrom(ItemDefinitionSnapshot item) {
		return new ItemDefinition(CreateItem.newBuilder()
				.withId(item.getId())
				.withDataType(item.getDataType())
				.withLength(item.getLength())
				.withCodeList(CodeList.restoreFrom(item.getCodeList()))
				.withMeasurementUnits(MeasurementUnit.restoreFrom(item.getMeasurementUnits()))
				.withLabel(ItemLabel.restoreFrom(item.getLabel()))
				.build());
	}

	public ItemDefinitionSnapshot toSnapshot() {
		return ItemDefinitionSnapshot.newBuilder()
				.withId(id)
				.withCodeList(codeListToSnapshot())
				.withDataType(dataType)
				.withLength(length)
				.withMeasurementUnits(measurementUnitsToSnapshot())
				.withLabel(label.toSnapshot())
				.build();
	}

	private CodeListSnapshot codeListToSnapshot() {
		return codeList == null ? null : codeList.toSnapshot();
	}

	private List<MeasurementUnitSnapshot> measurementUnitsToSnapshot() {
		return measurementUnits == null ?
				null :
				measurementUnits.stream()
						.map(MeasurementUnit::toSnapshot)
						.collect(Collectors.toList());
	}

	public ItemLabel getLabel() {
		return label;
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

	public CodeList getCodeList() {
		return codeList;
	}

	public List<MeasurementUnit> getMeasurementUnits() {
		return measurementUnits;
	}

	public String getDisplayLabel(Locale locale) {
		return label.getRepresentationFor(locale);
	}

	public static final class Builder {
		private ItemDefinitionId id;
		private String dataType;
		private Integer length;
		private CodeList codeList;
		private List<MeasurementUnit> measurementUnits;
		private ItemLabel label;

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

		public Builder withCodeList(final CodeList val) {
			codeList = val;
			return this;
		}

		public Builder withMeasurementUnits(final List<MeasurementUnit> val) {
			measurementUnits = val;
			return this;
		}

		public Builder withLabel(ItemLabel val) {
			label = val;
			return this;
		}

		public ItemDefinition build() {
			return new ItemDefinition(this);
		}
	}

	public static final class CreateItem {
		private ItemDefinitionId id;
		private String dataType;
		private Integer length;
		private CodeList codeList;
		private List<MeasurementUnit> measurementUnits;
		private ItemLabel label;

		private CreateItem(Builder builder) {
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

		public static final class Builder {
			private ItemDefinitionId id;
			private String dataType;
			private Integer length;
			private CodeList codeList;
			private List<MeasurementUnit> measurementUnits;
			private ItemLabel label;

			private Builder() {
			}

			public Builder withId(ItemDefinitionId id) {
				this.id = id;
				return this;
			}

			Builder withDataType(String dataType) {
				this.dataType = dataType;
				return this;
			}

			Builder withLength(Integer length) {
				this.length = length;
				return this;
			}

			Builder withCodeList(CodeList codeList) {
				this.codeList = codeList;
				return this;
			}

			Builder withMeasurementUnits(List<MeasurementUnit> measurementUnits) {
				this.measurementUnits = measurementUnits;
				return this;
			}

			public Builder withLabel(ItemLabel label) {
				this.label = label;
				return this;
			}

			public CreateItem build() {
				return new CreateItem(this);
			}
		}
	}
}
