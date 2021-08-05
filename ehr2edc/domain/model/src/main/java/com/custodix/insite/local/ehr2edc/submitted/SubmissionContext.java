package com.custodix.insite.local.ehr2edc.submitted;

import com.custodix.insite.local.ehr2edc.vocabulary.SubmissionContextId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubmittedEventId;

public final class SubmissionContext {

	private final SubmissionContextId id;
	private final SubmittedEventId submittedEventId;
	private final String submittedXml;

	private SubmissionContext(Builder builder) {
		id = SubmissionContextId.of(builder.submittedEventId.getId());
		submittedEventId = builder.submittedEventId;
		submittedXml = builder.submittedXml;
	}

	public SubmissionContextId getId() {
		return id;
	}

	public SubmittedEventId getSubmittedEventId() {
		return submittedEventId;
	}

	public String getSubmittedXml() {
		return submittedXml;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private SubmittedEventId submittedEventId;
		private String submittedXml;

		private Builder() {
		}

		public Builder withSubmittedEventId(SubmittedEventId val) {
			submittedEventId = val;
			return this;
		}

		public Builder withSubmittedXml(String val) {
			submittedXml = val;
			return this;
		}

		public SubmissionContext build() {
			return new SubmissionContext(this);
		}
	}
}
