package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.postprocessing;

import java.util.Optional;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSignQuery;

public class VitalSignPostProcessor {
	public Stream<VitalSign> process(final Stream<VitalSign> vitalSigns, final VitalSignQuery vitalSignQuery) {
		VitalSignProcessor criteriaProcessor = getCriteriaProcessor(vitalSignQuery.getCriteria());
		return criteriaProcessor.process(vitalSigns);
	}

	private VitalSignProcessor getCriteriaProcessor(final Criteria criteria) {
		return sequentialProcessor(getCriteriaProcessors(criteria));
	}

	private Stream<VitalSignProcessor> getCriteriaProcessors(final Criteria criteria) {
		final Optional<VitalSignProcessor> excludeConceptsProcessor = criteria.excludeConcepts()
				.map(ExcludeConceptsCriterionVitalSignProcessor::new);
		final Optional<VitalSignProcessor> conceptProcessor = criteria.concepts()
				.map(ConceptCriterionVitalSignProcessor::new);
		final Optional<VitalSignProcessor> ordinalProcessor = criteria.ordinal()
				.map(OrdinalCriterionVitalSignProcessor::new);
		return Stream.of(excludeConceptsProcessor, conceptProcessor, ordinalProcessor)
				.filter(Optional::isPresent)
				.map(Optional::get);
	}

	private VitalSignProcessor sequentialProcessor(Stream<VitalSignProcessor> processors) {
		return processors.reduce((op1, op2) -> vitalSigns -> op2.process(op1.process(vitalSigns)))
				.orElse(p -> p);
	}
}
