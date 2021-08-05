package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign;

import ca.uhn.fhir.model.dstu2.resource.Observation;

public final class FhirDstu2VitalSignResources<V> {
	private final V vitalSignResource;

	private FhirDstu2VitalSignResources(Builder<V> builder) {
		vitalSignResource = builder.vitalSignResource;
	}

	public V getVitalSignResource() {
		return vitalSignResource;
	}

	private static <V> Builder<V> newBuilder() {
		return new Builder<>();
	}

	public static Builder<Observation> observationBuilder() {
		return newBuilder();
	}

	public static final class Builder<V> {
		private V vitalSignResource;

		private Builder() {
		}

		public Builder<V> withVitalSignResource(V val) {
			vitalSignResource = val;
			return this;
		}

		public FhirDstu2VitalSignResources<V> build() {
			return new FhirDstu2VitalSignResources(this);
		}

	}
}
