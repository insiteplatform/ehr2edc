package com.custodix.insite.local.ehr2edc.query.mongo.patient.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = PatientIdDocument.COLLECTION)
public class PatientIdDocument {

	public static final String COLLECTION = "PatientId";
	@Id
	@SuppressWarnings("unused")
	private String id;
	private String source;
	private String identifier;
	private LocalDate birthDate;

	@PersistenceConstructor
	public PatientIdDocument(String source, String identifier, LocalDate birthDate) {
		this.source = source;
		this.identifier = identifier;
		this.birthDate = birthDate;
	}

	public String getSource() {
		return source;
	}

	public String getIdentifier() {
		return identifier;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}
}
