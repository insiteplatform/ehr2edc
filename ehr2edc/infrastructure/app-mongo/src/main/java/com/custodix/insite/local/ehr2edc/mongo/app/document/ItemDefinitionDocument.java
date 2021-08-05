package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.snapshots.*;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;

public final class ItemDefinitionDocument {
	private final String id;
	private final String name;
	private final String dataType;
	private final Integer length;
	private final CodeListDocument codeList;
	private final List<MeasurementUnitDocument> measurementUnits;
	private final QuestionDocument question;

	@PersistenceConstructor
	private ItemDefinitionDocument(
			String id, String name, String dataType, Integer length,
			CodeListDocument codeList,
			List<MeasurementUnitDocument> measurementUnits,
			QuestionDocument question) {
		this.id = id;
		this.name = name;
		this.dataType = dataType;
		this.length = length;
		this.codeList = codeList;
		this.measurementUnits = measurementUnits;
		this.question = question;
	}

	private ItemDefinitionDocument(final Builder builder) {
		id = builder.id;
		name = builder.name;
		dataType = builder.dataType;
		length = builder.length;
		codeList = builder.codeList;
		measurementUnits = builder.measurementUnits;
		question = builder.question;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	static List<ItemDefinitionDocument> fromSnapshots(List<ItemDefinitionSnapshot> items) {
		if (items == null) {
			return Collections.emptyList();
		}
		return items.stream()
				.map(s -> new ItemDefinitionDocument(s.getId().getId(), s.getName(), s.getDataType(), s.getLength(),
						CodeListDocument.fromSnapshot(s.getCodeList()),
						MeasurementUnitDocument.fromSnapshot(s.getMeasurementUnits()),
						QuestionDocument.fromSnapshot(s.getQuestion())))
				.collect(Collectors.toList());
	}

	public String getId() {
		return id;
	}

	public String getDataType() {
		return dataType;
	}

	public Integer getLength() {
		return length;
	}

	public QuestionDocument getQuestion() {
		return question;
	}

	public ItemDefinitionSnapshot toSnapshot() {
		return ItemDefinitionSnapshot.newBuilder()
				.withId(ItemDefinitionId.of(id))
				.withDataType(dataType)
				.withLength(length)
				.withCodeList(toCodeListSnapshot())
				.withMeasurementUnits(toMeasurementUnitSnapshots())
				.withLabel(new ItemLabelSnapshot(name, toQuestionSnapshot()))
				.build();
	}

	private CodeListSnapshot toCodeListSnapshot() {
		return Optional.ofNullable(codeList)
				.map(CodeListDocument::toSnapshot)
				.orElse(null);
	}

	private List<MeasurementUnitSnapshot> toMeasurementUnitSnapshots() {
		return measurementUnits == null ?
				Collections.emptyList() :
				measurementUnits.stream()
						.map(MeasurementUnitDocument::toSnapshot)
						.collect(Collectors.toList());
	}

	private QuestionSnapshot toQuestionSnapshot() {
		return Optional.ofNullable(question)
				.map(QuestionDocument::toSnapshot)
				.orElse(null);
	}

	public static final class Builder {
		private String id;
		private String name;
		private String dataType;
		private Integer length;
		private CodeListDocument codeList;
		private List<MeasurementUnitDocument> measurementUnits;
		private QuestionDocument question;

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

		public Builder withDataType(final String val) {
			dataType = val;
			return this;
		}

		public Builder withLength(final Integer val) {
			length = val;
			return this;
		}

		public Builder withCodeList(final CodeListDocument val) {
			codeList = val;
			return this;
		}

		public Builder withMeasurementUnits(final List<MeasurementUnitDocument> val) {
			measurementUnits = val;
			return this;
		}

		public Builder withQuestion(final QuestionDocument val) {
			question = val;
			return this;
		}

		public ItemDefinitionDocument build() {
			return new ItemDefinitionDocument(this);
		}
	}
}
