package com.custodix.insite.local.ehr2edc.vocabulary;

import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public final class EventDefinitionId {

	@NotNull
	@NotBlank
	private final String id;

	private EventDefinitionId(Builder builder) {
		id = builder.id;
	}

	public static EventDefinitionId of(String id) {
		return EventDefinitionId.newBuilder()
				.withId(id)
				.build();
	}

	public String getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final EventDefinitionId eventDefinitionId = (EventDefinitionId) o;
		return Objects.equals(id, eventDefinitionId.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "EventDefinitionId{" + "id='" + id + '\'' + '}';
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(EventDefinitionId copy) {
		Builder builder = new Builder();
		builder.id = copy.getId();
		return builder;
	}

	public static final class Builder {
		private String id;

		private Builder() {
		}

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public EventDefinitionId build() {
			return new EventDefinitionId(this);
		}
	}
}
