package com.custodix.insite.local.ehr2edc.mongo.app.document.provenance;

import org.springframework.data.annotation.PersistenceConstructor;

import com.custodix.insite.local.ehr2edc.provenance.model.ConceptCode;

final class ConceptCodeDocument {
	private final String code;

	@PersistenceConstructor
	private ConceptCodeDocument(String code) {
		this.code = code;
	}

	public static ConceptCodeDocument toDocument(ConceptCode concept) {
		return new ConceptCodeDocument(concept.getCode());
	}

	public ConceptCode restore() {
		return ConceptCode.of(code);
	}
}
