package com.custodix.insite.local.ehr2edc.provenance.model;

public final class ConceptCode {
	private final String code;

	private ConceptCode(String code) {
		this.code = code;
	}

	public static ConceptCode of(String code) {
		return new ConceptCode(code);
	}

	public static ConceptCode from(
			com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode conceptCode) {
		return new ConceptCode(conceptCode.getCode());
	}

	public String getCode() {
		return code;
	}
}
