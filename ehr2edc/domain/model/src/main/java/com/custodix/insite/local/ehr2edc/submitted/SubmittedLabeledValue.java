package com.custodix.insite.local.ehr2edc.submitted;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class SubmittedLabeledValue {
	private final String value;
	private final List<SubmittedLabel> labels;

	private SubmittedLabeledValue(Builder builder) {
		value = builder.value;
		labels = builder.labels == null ? Collections.emptyList() : builder.labels;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public String getValue() {
		return value;
	}

	public List<SubmittedLabel> getLabels() {
		return labels;
	}

	public String findLabelFor(Locale locale) {
		return labels.stream()
				.filter(l -> l.matchesLocale(locale))
				.map(SubmittedLabel::getText)
				.findFirst().orElse(null);
	}

	@Override
	public String toString() {
		return "SubmittedLabeledValue{" + "value='" + value + '\'' + ", labels=" + labels + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final SubmittedLabeledValue that = (SubmittedLabeledValue) o;
		return Objects.equals(value, that.value) && Objects.equals(labels, that.labels);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, labels);
	}

	public static final class Builder {
		private String value;
		private List<SubmittedLabel> labels;

		private Builder() {
		}

		public Builder withValue(String val) {
			value = val;
			return this;
		}

		public Builder withLabels(List<SubmittedLabel> val) {
			labels = val;
			return this;
		}

		public SubmittedLabeledValue build() {
			return new SubmittedLabeledValue(this);
		}
	}
}
