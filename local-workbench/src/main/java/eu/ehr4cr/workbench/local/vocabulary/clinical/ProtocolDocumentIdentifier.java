package eu.ehr4cr.workbench.local.vocabulary.clinical;

import java.io.Serializable;
import java.util.Objects;

public class ProtocolDocumentIdentifier implements Serializable {
	private final long id;

	private ProtocolDocumentIdentifier(Builder builder) {
		id = builder.id;
	}

	public long getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ProtocolDocumentIdentifier)) {
			return false;
		}
		final ProtocolDocumentIdentifier that = (ProtocolDocumentIdentifier) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public static ProtocolDocumentIdentifier of(long id) {
		return newBuilder().withId(id)
				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private long id;

		private Builder() {
		}

		public Builder withId(long id) {
			this.id = id;
			return this;
		}

		public ProtocolDocumentIdentifier build() {
			return new ProtocolDocumentIdentifier(this);
		}
	}
}