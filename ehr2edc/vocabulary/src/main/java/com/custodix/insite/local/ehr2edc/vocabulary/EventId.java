package com.custodix.insite.local.ehr2edc.vocabulary;

import static java.lang.Math.abs;

import java.util.Objects;
import java.util.UUID;

import javax.validation.constraints.NotBlank;

public final class EventId {
	@NotBlank
	private final String id;

	private EventId(String id) {
		this.id = id;
	}

	public static EventId of(String value) {
		return new EventId(value);
	}

	public String getId() {
		return id;
	}

	public static EventId generate() {
		final UUID uuid = UUID.randomUUID();
		return EventId.of(abs(uuid.getMostSignificantBits()) + "");
	}

	@Override
	public String toString() {
		return "EventId{" + "id='" + id + '\'' + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final EventId eventId = (EventId) o;
		return id.equals(eventId.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}