package com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew;

import java.util.Optional;

public enum ProjectedValueField {
	VALUE {
		@Override
		public Optional<Object> getFieldValue(ProjectedValue projectedValue) {
			return Optional.ofNullable(projectedValue.getValue());
		}

		@Override
		public ProjectedValue updateFieldValue(ProjectedValue projectedValue, String fieldValue) {
			return new ProjectedValue<>(fieldValue, projectedValue.getUnit(), projectedValue.getCode());
		}
	},
	CODE {
		@Override
		public Optional<Object> getFieldValue(ProjectedValue projectedValue) {
			return Optional.ofNullable(projectedValue.getCode());
		}

		@Override
		public ProjectedValue updateFieldValue(ProjectedValue projectedValue, String fieldValue) {
			return new ProjectedValue<>(projectedValue.getValue(), projectedValue.getUnit(), fieldValue);
		}
	},
	UNIT {
		@Override
		public Optional<Object> getFieldValue(ProjectedValue projectedValue) {
			return Optional.ofNullable(projectedValue.getUnit());
		}

		@Override
		public ProjectedValue updateFieldValue(ProjectedValue projectedValue, String fieldValue) {
			return new ProjectedValue<>(projectedValue.getValue(), fieldValue, projectedValue.getCode());
		}
	};

	public abstract Optional<Object> getFieldValue(ProjectedValue projectedValue);

	public abstract ProjectedValue updateFieldValue(ProjectedValue projectedValue, String fieldValue);
}
