package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import java.util.Objects;

public class AuthenticationToken {
	private final String value;

	AuthenticationToken(String value) {
		this.value = value;
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
		AuthenticationToken that = (AuthenticationToken) o;
		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
