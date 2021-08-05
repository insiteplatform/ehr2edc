package com.custodix.insite.local.ehr2edc.mongo.app.review;

import com.custodix.insite.local.ehr2edc.submitted.SubmittedMeasurementUnitReference;
import org.springframework.data.annotation.PersistenceConstructor;

public final class ReviewedMeasurementUnitReferenceMongoSnapShot {
	private final String id;
	private final boolean submittedToEDC;

	@PersistenceConstructor
	ReviewedMeasurementUnitReferenceMongoSnapShot(String id, Boolean submittedToEDC) {
		this.id = id;
		this.submittedToEDC = submittedToEDC == null || submittedToEDC;
	}

	private ReviewedMeasurementUnitReferenceMongoSnapShot(final Builder builder) {
		id = builder.id;
		submittedToEDC = builder.submittedToEDC;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public boolean isSubmittedToEDC() {
		return submittedToEDC;
	}

	public String getId() {
		return id;
	}

	SubmittedMeasurementUnitReference toReviewedMeasurementUnitReference() {
		return SubmittedMeasurementUnitReference.newBuilder()
				.withId(id)
				.withSubmittedToEDC(submittedToEDC)
				.build();
	}

	public static final class Builder {
		private String id;
		private boolean submittedToEDC;

		private Builder() {
		}

		public Builder withId(final String val) {
			id = val;
			return this;
		}

		public Builder withSubmittedToEDC(final boolean val) {
			submittedToEDC = val;
			return this;
		}

		public ReviewedMeasurementUnitReferenceMongoSnapShot build() {
			return new ReviewedMeasurementUnitReferenceMongoSnapShot(this);
		}
	}

}

