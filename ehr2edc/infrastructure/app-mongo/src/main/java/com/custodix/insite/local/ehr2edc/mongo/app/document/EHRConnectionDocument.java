package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.net.URI;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.local.ehr2edc.vocabulary.EHRConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.EHRSystem;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

@Document(collection = EHRConnectionDocument.COLLECTION)
public final class EHRConnectionDocument {
	public static final String COLLECTION = "EHRConnection";

	@Id
	private final String studyId;
	private final String uri;
	private final EHRSystem system;

	@PersistenceConstructor
	public EHRConnectionDocument(String studyId, String uri, EHRSystem system) {
		this.studyId = studyId;
		this.uri = uri;
		this.system = system;
	}

	public static EHRConnectionDocument from(EHRConnection ehrConnection) {
		return new EHRConnectionDocument(ehrConnection.getStudyId()
				.getId(), ehrConnection.getUri()
				.toASCIIString(), ehrConnection.getSystem());
	}

	public EHRConnection toEHRConnection() {
		return EHRConnection.newBuilder()
				.withStudyId(StudyId.of(studyId))
				.withUri(URI.create(uri))
				.withSystem(system)
				.build();
	}
}
