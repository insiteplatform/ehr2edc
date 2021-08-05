package com.custodix.insite.local.ehr2edc.query.executor.common.projector.model;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class LabeledValue {
	private final String value;
	private final List<Label> labels;

	public LabeledValue(String value, List<Label> labels) {
		this.value = value;
		this.labels = labels;
	}

	public LabeledValue(String value) {
		this(value, emptyList());
	}

	public String getValue() {
		return value;
	}

	public List<Label> getLabels() {
		return labels;
	}

	public Optional<String> findLabelFor(Locale locale) {
		return labels.stream()
				.filter(l -> l.matchesLocale(locale))
				.map(Label::getText)
				.findFirst();
	}

	@Override
	public String toString() {
		return "LabeledValue{" + "value='" + value + '\'' + ", labels=" + labels + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final LabeledValue that = (LabeledValue) o;
		return Objects.equals(value, that.value) && Objects.equals(labels, that.labels);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, labels);
	}
}
