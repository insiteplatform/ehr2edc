package eu.ehr4cr.workbench.local.vocabulary;

import java.io.Serializable;
import java.util.Objects;

public final class UserIdentifier implements Serializable {
	private final long id;

	private UserIdentifier(long id) {
		this.id = id;
	}

	public static UserIdentifier of(long id) {
		return new UserIdentifier(id);
	}

	public long getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof UserIdentifier)) {
			return false;
		}
		final UserIdentifier that = (UserIdentifier) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return String.valueOf(id);
	}
}