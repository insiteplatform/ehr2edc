package com.custodix.insite.local.ehr2edc.query.executor.common.domain;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.DataPoint;

public class FormItem {
	private final DataPoint dataPoint;
	private final LabeledValue value;
	private final String unit;
	private final String index;
	private final boolean readOnly;
	private final boolean outputUnit;
	private final boolean key;

	private FormItem(Builder builder) {
		dataPoint = builder.dataPoint;
		value = builder.value;
		unit = builder.unit;
		index = builder.index;
		readOnly = builder.readOnly;
		outputUnit = builder.outputUnit;
		key = builder.key;
	}

	public DataPoint getDataPoint() {
		return dataPoint;
	}

	public LabeledValue getValue() {
		return value;
	}

	public String getUnit() {
		return unit;
	}

	public String getIndex() {
		return index;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public boolean isOutputUnit() {
		return outputUnit;
	}

	public boolean isKey() {
		return key;
	}

	@Override
	public String toString() {
		return "FormItem{" + "dataPoint='" + dataPoint + '\'' + ", value='" + value + '\'' + ", unit='" + unit + '\'' + ", index='" + index + '\''
				+ ", readOnly=" + readOnly + ", outputUnit=" + outputUnit + ", key=" + key + '}';
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private DataPoint dataPoint;
		private LabeledValue value;
		private String unit;
		private String index;
		private boolean readOnly;
		private boolean outputUnit;
		private boolean key;

		private Builder() {
		}

		public Builder withDataPoint(DataPoint val) {
			dataPoint = val;
			return this;
		}

		public Builder withValue(LabeledValue val) {
			value = val;
			return this;
		}

		public Builder withUnit(String val) {
			unit = val;
			return this;
		}

		public Builder withIndex(String val) {
			index = val;
			return this;
		}

		public Builder withReadOnly(boolean val) {
			readOnly = val;
			return this;
		}

		public Builder withOutputUnit(boolean val) {
			outputUnit = val;
			return this;
		}

		public Builder withKey(boolean key) {
			this.key = key;
			return this;
		}

		public FormItem build() {
			return new FormItem(this);
		}
	}
}
