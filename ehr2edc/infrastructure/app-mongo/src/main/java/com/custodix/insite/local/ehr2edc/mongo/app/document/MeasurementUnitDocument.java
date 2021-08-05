package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.snapshots.MeasurementUnitSnapshot;

public final class MeasurementUnitDocument {
	private final String id;
	private final String name;

	@PersistenceConstructor
	private MeasurementUnitDocument(String id, String name) {
		this.id = id;
		this.name = name;
	}

	private MeasurementUnitDocument(final Builder builder) {
		id = builder.id;
		name = builder.name;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(final MeasurementUnitDocument copy) {
		Builder builder = new Builder();
		builder.id = copy.getId();
		builder.name = copy.getName();
		return builder;
	}

	static List<MeasurementUnitDocument> fromSnapshot(List<MeasurementUnitSnapshot> measurementUnits) {
		if (measurementUnits == null) {
			return Collections.emptyList();
		}
		return measurementUnits.stream()
				.map(s -> new MeasurementUnitDocument(s.getId(), s.getName()))
				.collect(Collectors.toList());
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public MeasurementUnitSnapshot toSnapshot() {
		return MeasurementUnitSnapshot.newBuilder()
				.withId(id)
				.withName(name)
				.build();
	}

	public static final class Builder {
		private String id;
		private String name;

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

		public MeasurementUnitDocument build() {
			return new MeasurementUnitDocument(this);
		}
	}
}
