package eu.ehr4cr.workbench.local.global.web;

import java.util.concurrent.TimeUnit;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

public interface WebResourcesCacheSettings {
	MaxAge getMaxage();

	@Validated
	final class MaxAge {
		@Min(1)
		private int value;
		@NotNull
		private TimeUnit unit;

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public TimeUnit getUnit() {
			return unit;
		}

		public void setUnit(TimeUnit unit) {
			this.unit = unit;
		}
	}

}
