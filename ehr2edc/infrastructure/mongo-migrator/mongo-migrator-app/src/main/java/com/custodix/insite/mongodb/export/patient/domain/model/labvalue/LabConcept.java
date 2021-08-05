package com.custodix.insite.mongodb.export.patient.domain.model.labvalue;

public final class LabConcept {

	private static final String LAB_CONCEPT_DOCUMENT = "{"
			+ "\"concept\" : {\"code\" : \"%s\"},"
			+ "\"component\" : \"%s\"," + "\"method\" : \"%s\"," + "\"fastingStatus\" : \"%s\","
			+ "\"specimen\" : \"%s\"" + "}";

	private LabConcept() {
	}

	public static String subdocument(String conceptCode, String component, String method, String fastingStatus,
			String specimen) {
		return String.format(LAB_CONCEPT_DOCUMENT, conceptCode, component, method, fastingStatus, specimen);
	}
}
