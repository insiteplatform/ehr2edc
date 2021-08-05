package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.Label;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue;

public final class LabeledValueMongoSnapshot {
	private final String value;
	private final List<LabelDocument> labels;

	@PersistenceConstructor
	private LabeledValueMongoSnapshot(String value, List<LabelDocument> labels) {
		this.value = value;
		this.labels = labels;
	}

	private LabeledValueMongoSnapshot(Builder builder) {
		value = builder.value;
		labels = builder.labels != null ? builder.labels : Collections.emptyList();
	}

	static LabeledValueMongoSnapshot toSnapshot(LabeledValue labeledValue) {
		List<LabelDocument> labels = labeledValue.getLabels()
				.stream()
				.map(LabelDocument::toSnapshot)
				.collect(Collectors.toList());
		return LabeledValueMongoSnapshot.newBuilder()
				.withValue(labeledValue.getValue())
				.withLabels(labels)
				.build();
	}

	static LabeledValue restoreFrom(LabeledValueMongoSnapshot labeledValueMongoSnapshot) {
		List<Label> labels = labeledValueMongoSnapshot.labels.stream()
				.map(LabelDocument::restoreFrom)
				.collect(Collectors.toList());
		return new LabeledValue(labeledValueMongoSnapshot.value, labels);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String value;
		private List<LabelDocument> labels;

		private Builder() {
		}

		public Builder withValue(String value) {
			this.value = value;
			return this;
		}

		public Builder withLabels(List<LabelDocument> labels) {
			this.labels = labels;
			return this;
		}

		public LabeledValueMongoSnapshot build() {
			return new LabeledValueMongoSnapshot(this);
		}
	}
}
