package com.custodix.insite.local.ehr2edc.query.executor.demographic.domain;

import com.custodix.insite.local.ehr2edc.query.executor.common.domain.HasSubjectId;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public final class Demographic implements DataPoint, HasSubjectId {

	private SubjectId subjectId;
	private DemographicType demographicType;
	private String value;

	private Demographic(final Builder builder) {
		subjectId = builder.subjectId;
		demographicType = builder.demographicType;
		value = builder.value;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(final Demographic copy) {
		Builder builder = new Builder();
		builder.subjectId = copy.getSubjectId();
		builder.demographicType = copy.getDemographicType();
		builder.value = copy.getValue();
		return builder;
	}

	public SubjectId getSubjectId() {
		return subjectId;
	}

	public DemographicType getDemographicType() {
		return demographicType;
	}

	public String getValue() {
		return value;
	}

	public static final class Builder {
		private SubjectId subjectId;
		private DemographicType demographicType;
		private String value;

		private Builder() {
		}

		public Builder withSubjectId(final SubjectId val) {
			subjectId = val;
			return this;
		}

		public Builder withDemographicType(final DemographicType val) {
			demographicType = val;
			return this;
		}

		public Builder withValue(final String val) {
			value = val;
			return this;
		}

		public Demographic build() {
			return new Demographic(this);
		}
	}
}
