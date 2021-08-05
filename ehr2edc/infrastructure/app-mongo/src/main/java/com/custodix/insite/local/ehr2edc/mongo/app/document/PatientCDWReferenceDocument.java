package com.custodix.insite.local.ehr2edc.mongo.app.document;

import org.springframework.data.annotation.PersistenceConstructor;

public class PatientCDWReferenceDocument {

	private final String source;
	private final String id;

	@PersistenceConstructor
	public PatientCDWReferenceDocument(String source, String id) {
		this.source = source;
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public String getId() {
		return id;
	}
}
