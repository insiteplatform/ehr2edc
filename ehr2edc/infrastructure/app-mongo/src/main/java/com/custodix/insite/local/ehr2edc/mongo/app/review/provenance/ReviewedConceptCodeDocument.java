package com.custodix.insite.local.ehr2edc.mongo.app.review.provenance;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.provenance.model.ConceptCode;

final class ReviewedConceptCodeDocument {
	private final String code;

	@PersistenceConstructor
	private ReviewedConceptCodeDocument(String code) {
		this.code = code;
	}

	public static ReviewedConceptCodeDocument of(String code) {
		return new ReviewedConceptCodeDocument(code);
	}

	ConceptCode restore() {
		return ConceptCode.of(code);
	}
}
