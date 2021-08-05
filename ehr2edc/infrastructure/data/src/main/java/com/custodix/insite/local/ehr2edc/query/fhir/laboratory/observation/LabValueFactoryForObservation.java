package com.custodix.insite.local.ehr2edc.query.fhir.laboratory.observation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;
import com.custodix.insite.local.ehr2edc.query.fhir.laboratory.FhirDstu2LaboratoryResources;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Specimen;
import ca.uhn.fhir.model.primitive.DateTimeDt;

class LabValueFactoryForObservation {

	LabValue create(FhirDstu2LaboratoryResources<Observation> laboratoryResources, SubjectId subjectId) {
		LabValue.Builder builder = LabValue.newBuilder()
				.forSubject(subjectId);
		Observation observation = laboratoryResources.getLaboratoryResource();
		addConcept(builder, observation);
		addStartAndEndDate(builder, observation);
		addMeasurement(builder, observation);
		return builder.build();
	}

	private void addConcept(LabValue.Builder builder, Observation observation) {
		builder.withLabConcept(LabConcept.newBuilder()
				.withMethod(getMethod(observation).orElse(null))
				.withSpecimen(getSpecimen(observation).orElse(null))
				.withConcept(getCode(observation))
				.build());
	}

	private void addStartAndEndDate(LabValue.Builder builder, Observation observation) {
		IDatatype effective = observation.getEffective();
		if (effective instanceof PeriodDt) {
			PeriodDt effectivePeriod = (PeriodDt) effective;
			builder.withStartDate(toLocalDateTime(effectivePeriod.getStart()));
			builder.withEndDate(toLocalDateTime(effectivePeriod.getEnd()));
		} else if (effective instanceof DateTimeDt) {
			DateTimeDt effectiveDateTime = (DateTimeDt) effective;
			builder.withStartDate(toLocalDateTime(effectiveDateTime.getValue()));
			builder.withEndDate(toLocalDateTime(effectiveDateTime.getValue()));
		}
	}

	private void addMeasurement(LabValue.Builder builder, Observation observation) {
		Measurement.Builder measurementBuilder = Measurement.newBuilder();
		addValueAndUnit(measurementBuilder, observation);
		addReferenceRange(measurementBuilder, observation);
		builder.withQuantitativeResult(measurementBuilder.build());
	}

	private void addValueAndUnit(Measurement.Builder measurementBuilder, Observation observation) {
		IDatatype value = observation.getValue();
		if (value instanceof QuantityDt) {
			QuantityDt quantity = (QuantityDt) value;
			measurementBuilder.withValue(quantity.getValue())
					.withUnit(getUnit(quantity));
		}
	}

	private String getUnit(QuantityDt quantity) {
		if (StringUtils.isNotBlank(quantity.getCode())) {
			return quantity.getCode();
		} else {
			return quantity.getUnit();
		}
	}

	private Optional<String> getMethod(Observation observation) {
		CodeableConceptDt methodCodeableConcept = observation.getMethod();
		return getFirstCoding(methodCodeableConcept);
	}

	private Optional<String> getSpecimen(Observation observation) {
		ResourceReferenceDt specimenReference = observation.getSpecimen();
		return Optional.ofNullable(specimenReference.getResource())
				.map(Specimen.class::cast)
				.map(Specimen::getType)
				.flatMap(this::getFirstCoding);
	}

	private Optional<String> getFirstCoding(CodeableConceptDt codeableConceptDt) {
		List<CodingDt> codings = codeableConceptDt.getCoding();
		if (!codings.isEmpty()) {
			CodingDt firstCoding = codings.get(0);
			return Optional.ofNullable(firstCoding.getCode());
		}
		return Optional.empty();
	}

	private void addReferenceRange(Measurement.Builder measurementBuilder, Observation observation) {
		List<Observation.ReferenceRange> referenceRanges = observation.getReferenceRange();
		if (!referenceRanges.isEmpty()) {
			Observation.ReferenceRange referenceRange = referenceRanges.get(0);
			measurementBuilder.withLowerLimit(referenceRange.getLow()
					.getValue());
			measurementBuilder.withUpperLimit(referenceRange.getHigh()
					.getValue());
		}
	}

	private ConceptCode getCode(Observation observation) {
		CodeableConceptDt code = observation.getCode();
		CodingDt firstCoding = code.getCodingFirstRep();
		return ConceptCode.conceptFor(firstCoding.getCode());
	}

	private LocalDateTime toLocalDateTime(Date date) {
		return date.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}
}
