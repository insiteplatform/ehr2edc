package com.custodix.insite.mongodb.vocabulary;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = Namespace.Builder.class)
public final class Namespace {

	@NotBlank
	private final String name;

	private Namespace(Builder builder) {
		name = builder.name;
	}

	public String getName() {
		return name;
	}

	public static Namespace of(String name) {
		return Namespace.newBuilder()
				.withName(name)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String name;

		private Builder() {
		}

		Builder withName(String val) {
			name = val;
			return this;
		}

		public Namespace build() {
			return new Namespace(this);
		}
	}
}