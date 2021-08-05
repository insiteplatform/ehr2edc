package com.custodix.insite.local.ehr2edc.usecase;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

@Validated
public class SomeExampleRequest {

	@NotNull
	@Size(min = 5,
		  max = 10)
	private String something;

	@NotNull
	@DecimalMax("5")
	private Integer somethingElse;

	private SomeExampleRequest(Builder builder) {
		something = builder.something;
		somethingElse = builder.somethingElse;
	}

	public String getSomething() {
		return something;
	}

	public Integer getSomethingElse() {
		return somethingElse;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static final class Builder {
		private String something;
		private Integer somethingElse;

		private Builder() {
		}

		public Builder withSomething(String something) {
			this.something = something;
			return this;
		}

		public Builder withSomethingElse(Integer somethingElse) {
			this.somethingElse = somethingElse;
			return this;
		}

		public SomeExampleRequest build() {
			return new SomeExampleRequest(this);
		}
	}
}
