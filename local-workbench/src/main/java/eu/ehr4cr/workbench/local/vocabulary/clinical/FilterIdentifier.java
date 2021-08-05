package eu.ehr4cr.workbench.local.vocabulary.clinical;

import java.io.Serializable;
import java.util.Objects;

public class FilterIdentifier implements Serializable {
	private final long filterId;
	private final long versionId;

	private FilterIdentifier(Builder builder) {
		filterId = builder.filterId;
		versionId = builder.versionId;
	}

	public long getFilterId() {
		return filterId;
	}

	public long getVersionId() {
		return versionId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof FilterIdentifier)) {
			return false;
		}
		final FilterIdentifier that = (FilterIdentifier) o;
		return filterId == that.filterId && versionId == that.versionId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(filterId, versionId);
	}

	public static FilterIdentifier of(long filterId, long versionId) {
		return newBuilder().withFilterId(filterId)
				.withVersionId(versionId)

				.build();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private long filterId;
		private long versionId;

		private Builder() {
		}

		public Builder withFilterId(long filterId) {
			this.filterId = filterId;
			return this;
		}

		public Builder withVersionId(long versionId) {
			this.versionId = versionId;
			return this;
		}

		public FilterIdentifier build() {
			return new FilterIdentifier(this);
		}
	}
}