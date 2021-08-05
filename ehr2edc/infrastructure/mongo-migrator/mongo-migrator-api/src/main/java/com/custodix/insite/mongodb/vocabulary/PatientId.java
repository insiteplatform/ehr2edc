package com.custodix.insite.mongodb.vocabulary;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = PatientId.Builder.class)
public final class PatientId {

	@NotBlank
	private final String id;

	private PatientId(Builder builder) {
		id = builder.id;
	}

	public String getId() {
		return id;
	}

	public static PatientId of(String id) {
		return PatientId.newBuilder()
				.withId(id)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String id;

		private Builder() {
		}

		Builder withId(String val) {
			id = val;
			return this;
		}

		public PatientId build() {
			return new PatientId(this);
		}
	}
}