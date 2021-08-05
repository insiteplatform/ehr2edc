package com.custodix.insite.local.ehr2edc.populator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.vocabulary.LabName;

public final class PopulatedFormSelection {
	private final LabName labName;
	private final List<PopulatedItemGroupSelection> itemGroupSelections;

	private PopulatedFormSelection(Builder builder) {
		labName = builder.labName;
		itemGroupSelections = builder.itemGroupSelections;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public Stream<PopulatedItemGroupSelection> stream() {
		return itemGroupSelections.stream();
	}

	LabName labNameOrDefault(final LabName defaultLabName) {
		return Optional.ofNullable(labName).orElse(defaultLabName);
	}

	public static final class Builder {
		private LabName labName;
		private List<PopulatedItemGroupSelection> itemGroupSelections;

		private Builder() {
		}

		public Builder withLabName(LabName labName) {
			this.labName = labName;
			return this;
		}

		public Builder withItemGroupSelections(List<PopulatedItemGroupSelection> itemGroupSelections) {
			this.itemGroupSelections = itemGroupSelections;
			return this;
		}

		public PopulatedFormSelection build() {
			return new PopulatedFormSelection(this);
		}
	}
}
