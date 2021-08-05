package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.observation.mapping;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;

class VitalSignObservationBodySiteProcessor {
	private static final Set<String> LATERALITY_CODES = new HashSet<>(
			asList("51440002", "24028007", "49370004", "7771000", "255208005", "66459002", "255209002"));
	private static final Predicate<String> LATERALITY_FILTER = LATERALITY_CODES::contains;
	private static final Predicate<String> LOCATION_FILTER = LATERALITY_FILTER.negate();

	static void processLocation(CodeableConceptDt bodySite, Consumer<String> locationConsumer) {
		processBodySite(bodySite, locationConsumer, LOCATION_FILTER);
	}

	static void processLaterality(CodeableConceptDt bodySite, Consumer<String> lateralityConsumer) {
		processBodySite(bodySite, lateralityConsumer, LATERALITY_FILTER);
	}

	private static void processBodySite(CodeableConceptDt bodySite, Consumer<String> consumer,
			Predicate<String> codeFilter) {
		bodySite.getCoding()
				.stream()
				.map(CodingDt::getCode)
				.filter(codeFilter)
				.findFirst()
				.ifPresent(consumer);
	}
}
