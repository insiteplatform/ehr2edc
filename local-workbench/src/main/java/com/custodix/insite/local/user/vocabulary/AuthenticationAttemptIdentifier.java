package com.custodix.insite.local.user.vocabulary;

import java.util.Objects;

public final class AuthenticationAttemptIdentifier {
	private final long id;

	private AuthenticationAttemptIdentifier(long id) {
		this.id = id;
	}

	public static AuthenticationAttemptIdentifier of(long id) {
		return new AuthenticationAttemptIdentifier(id);
	}

	public long getId() {
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
		final AuthenticationAttemptIdentifier that = (AuthenticationAttemptIdentifier) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
