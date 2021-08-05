package com.custodix.insite.local.ehr2edc.query.fhir.laboratory;

import ca.uhn.fhir.model.dstu2.resource.Observation;

public final class FhirDstu2LaboratoryResources<M> {
	private final M laboratoryResource;

	private FhirDstu2LaboratoryResources(Builder<M> builder) {
		laboratoryResource = builder.laboratoryResource;
	}

	public M getLaboratoryResource() {
		return laboratoryResource;
	}

	private static <M> Builder<M> newBuilder() {
		return new Builder<>();
	}

	public static Builder<Observation> observationBuilder() {
		return newBuilder();
	}

	public static final class Builder<M> {
		private M laboratoryResource;

		private Builder() {
		}

		public Builder<M> withLaboratoryResource(M val) {
			laboratoryResource = val;
			return this;
		}

		public FhirDstu2LaboratoryResources<M> build() {
			return new FhirDstu2LaboratoryResources(this);
		}

	}
}
