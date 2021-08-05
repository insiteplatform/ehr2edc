package com.custodix.insite.local.ehr2edc.query.fhir.observation;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.ConceptCriterion;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;

public class ConceptCriterionObservationProcessor implements ObservationProcessor {
	private final Set<String> codes;
	private final boolean includeMissing;

	public ConceptCriterionObservationProcessor(ConceptCriterion criterion) {
		this.codes = criterion.getConcepts()
				.stream()
				.map(ConceptCode::getCode)
				.collect(Collectors.toSet());
		this.includeMissing = criterion.getIncludeMissing();
	}

	@Override
	public Stream<Observation> process(Stream<Observation> observations) {
		List<Observation> observationList = observations.filter(this::hasIncludedCode)
				.collect(Collectors.toList());
		return Stream.concat(observationList.stream(), createMissingObservations(observationList));
	}

	private boolean hasIncludedCode(Observation observation) {
		return getCodes(observation).anyMatch(codes::contains);
	}

	private Stream<Observation> createMissingObservations(List<Observation> observations) {
		return missingCodesToInclude().filter(notPresentIn(observations))
				.map(this::toEmptyObservation);
	}

	private Stream<String> missingCodesToInclude() {
		return includeMissing ? codes.stream() : Stream.empty();
	}

	private Predicate<String> notPresentIn(List<Observation> observations) {
		final Set<String> codes = observations.stream()
				.flatMap(this::getCodes)
				.collect(Collectors.toSet());
		return code -> !codes.contains(code);
	}

	private Stream<String> getCodes(Observation observation) {
		return Stream.concat(getObservationCodes(observation), getComponentCodes(observation))
				.distinct();
	}

	private Stream<String> getObservationCodes(Observation observation) {
		return getConceptCodes(observation.getCode());
	}

	private Stream<String> getComponentCodes(Observation observation) {
		return observation.getComponent()
				.stream()
				.map(Observation.Component::getCode)
				.flatMap(this::getConceptCodes);
	}

	private Stream<String> getConceptCodes(CodeableConceptDt concept) {
		return concept.getCoding()
				.stream()
				.map(CodingDt::getCode);
	}

	private Observation toEmptyObservation(String code) {
		Observation observation = new Observation();
		observation.setCode(new CodeableConceptDt(null, code));
		return observation;
	}
}
