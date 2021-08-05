package com.custodix.insite.local.user.vocabulary;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Email {
	@NotNull
	@javax.validation.constraints.Email
	private final String value;

	@JsonCreator
	private Email(@JsonProperty("value") String value) {
		this.value = value;
	}

	public static Email of(String value) {
		return new Email(value);
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final Email email = (Email) o;
		return Objects.equals(value, email.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return value;
	}
}
