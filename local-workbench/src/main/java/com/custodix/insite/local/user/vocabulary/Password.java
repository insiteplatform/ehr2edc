package com.custodix.insite.local.user.vocabulary;

import java.util.Objects;

import com.custodix.insite.local.user.vocabulary.validation.ValidPassword;

public final class Password {
	@ValidPassword
	private final String value;

	private Password(String value) {
		this.value = value;
	}

	public static Password of(String value) {
		return new Password(value);
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
		final Password password = (Password) o;
		return Objects.equals(value, password.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
