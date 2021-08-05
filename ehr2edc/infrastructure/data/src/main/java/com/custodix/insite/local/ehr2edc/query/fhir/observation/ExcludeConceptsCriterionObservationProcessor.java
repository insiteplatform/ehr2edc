package com.custodix.insite.local.ehr2edc.query.fhir.observation;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.ExcludeConceptsCriterion;

import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;

public class ExcludeConceptsCriterionObservationProcessor implements ObservationProcessor {
	private final Set<String> codes;

	public ExcludeConceptsCriterionObservationProcessor(final ExcludeConceptsCriterion criterion) {
		codes = criterion.getConcepts().stream()
				.map(ConceptCode::getCode)
				.collect(Collectors.toSet());
	}

	public Stream<Observation> process(final Stream<Observation> observations) {
		return observations.filter(this::isNotExcluded);
	}

	private boolean isNotExcluded(Observation observation) {
		return observation.getCode()
				.getCoding()
				.stream()
				.map(CodingDt::getCode)
				.noneMatch(codes::contains);
	}
}
