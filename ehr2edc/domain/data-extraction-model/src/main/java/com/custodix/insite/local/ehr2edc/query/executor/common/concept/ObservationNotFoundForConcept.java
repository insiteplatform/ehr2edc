package com.custodix.insite.local.ehr2edc.query.executor.common.concept;

public class ObservationNotFoundForConcept extends RuntimeException {
	private final ConceptCode concept;

	public ObservationNotFoundForConcept(final ConceptCode concept) {
		this.concept = concept;
	}

	public ConceptCode getConcept() {
		return concept;
	}

	@Override
	public String getMessage() {
		return "Could not find concept " + concept.getCode() + " in observations";
	}
}
