package eu.ehr4cr.workbench.local.usecases.feature;

import eu.ehr4cr.workbench.local.model.feature.Features;

public interface GetFeatures {
	Response getFeatures();

	final class Response {
		private final Features features;

		private Response(Builder builder) {
			features = builder.features;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public Features getFeatures() {
			return features;
		}

		public static final class Builder {
			private Features features;

			private Builder() {
			}

			public Builder withFeatures(Features features) {
				this.features = features;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}
}

