package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.observation;

import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.query.VitalSignQuery;
import com.custodix.insite.local.ehr2edc.query.fhir.observation.ObservationPostProcessor;
import com.custodix.insite.local.ehr2edc.query.fhir.observation.ObservationProcessor;

import ca.uhn.fhir.model.dstu2.resource.Observation;

class VitalSignObservationPostProcessor {
	Stream<Observation> process(final Stream<Observation> observations, final VitalSignQuery vitalSignQuery) {
		final Criteria criteria = vitalSignQuery.getCriteria();
		final ObservationPostProcessor processor = new ObservationPostProcessor();
		final ObservationProcessor criteriaProcessor = processor.getCriteriaProcessor(criteria);
		return criteriaProcessor.process(observations);
	}
}
