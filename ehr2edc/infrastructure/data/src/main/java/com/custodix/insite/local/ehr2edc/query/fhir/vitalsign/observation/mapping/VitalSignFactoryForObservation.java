package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.observation.mapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept;
import com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.FhirDstu2VitalSignResources;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;

public class VitalSignFactoryForObservation {

	public Stream<VitalSign> create(FhirDstu2VitalSignResources<Observation> vitalSignResources, SubjectId subjectId) {
		Observation observation = vitalSignResources.getVitalSignResource();
		List<Observation.Component> components = observation.getComponent();
		if (components.isEmpty()) {
			return Stream.of(createFromObservation(subjectId, observation));
		}
		return components.stream()
				.map(component -> createFromComponent(subjectId, observation, component));
	}

	private VitalSign createFromComponent(SubjectId subjectId, Observation observation,
			Observation.Component component) {
		return baseBuilder(subjectId, observation).withCode(component.getCode())
				.withValue(component.getValue())
				.withReferenceRanges(component.getReferenceRange())
				.build();
	}

	private VitalSign createFromObservation(SubjectId subjectId, Observation observation) {
		return baseBuilder(subjectId, observation).withCode(observation.getCode())
				.withValue(observation.getValue())
				.withReferenceRanges(observation.getReferenceRange())
				.build();
	}

	private VitalSignFromFHIRBuilder baseBuilder(SubjectId subjectId, Observation observation) {
		return new VitalSignFromFHIRBuilder().withSubjectId(subjectId)
				.withBodySite(observation.getBodySite())
				.withEffective(observation.getEffective());
	}

	private static class VitalSignFromFHIRBuilder {
		private SubjectId subjectId;
		private CodeableConceptDt code;
		private CodeableConceptDt bodySite;
		private IDatatype effective;
		private IDatatype value;
		private List<Observation.ReferenceRange> referenceRanges;

		VitalSignFromFHIRBuilder withSubjectId(SubjectId subjectId) {
			this.subjectId = subjectId;
			return this;
		}

		VitalSignFromFHIRBuilder withCode(CodeableConceptDt code) {
			this.code = code;
			return this;
		}

		VitalSignFromFHIRBuilder withBodySite(CodeableConceptDt bodySite) {
			this.bodySite = bodySite;
			return this;
		}

		VitalSignFromFHIRBuilder withEffective(IDatatype effective) {
			this.effective = effective;
			return this;
		}

		VitalSignFromFHIRBuilder withValue(IDatatype value) {
			this.value = value;
			return this;
		}

		VitalSignFromFHIRBuilder withReferenceRanges(List<Observation.ReferenceRange> referenceRanges) {
			this.referenceRanges = referenceRanges;
			return this;
		}

		VitalSign build() {
			VitalSign.Builder builder = VitalSign.newBuilder()
					.withSubjectId(subjectId);
			processConcept(builder::withConcept);
			processEffectiveDateTime(builder::withEffectiveDateTime);
			processMeasurement(builder::withMeasurement);
			return builder.build();
		}

		private void processConcept(Consumer<VitalSignConcept> consumer) {
			VitalSignConcept.Builder builder = VitalSignConcept.newBuilder();
			VitalSignObservationCodeProcessor.process(code, builder::withConcept);
			VitalSignObservationBodySiteProcessor.processLocation(bodySite, builder::withLocation);
			VitalSignObservationBodySiteProcessor.processLaterality(bodySite, builder::withLaterality);
			consumer.accept(builder.build());
		}

		private void processEffectiveDateTime(Consumer<LocalDateTime> consumer) {
			VitalSignObservationEffectiveProcessor.process(effective, consumer);
		}

		private void processMeasurement(Consumer<Measurement> consumer) {
			Measurement.Builder builder = Measurement.newBuilder();
			VitalSignObservationValueProcessor.processValue(value, builder::withValue);
			VitalSignObservationValueProcessor.processUnit(value, builder::withUnit);
			VitalSignObservationReferenceRangeProcessor.processLowerLimit(referenceRanges, builder::withLowerLimit);
			VitalSignObservationReferenceRangeProcessor.processUpperLimit(referenceRanges, builder::withUpperLimit);
			consumer.accept(builder.build());
		}
	}
}
