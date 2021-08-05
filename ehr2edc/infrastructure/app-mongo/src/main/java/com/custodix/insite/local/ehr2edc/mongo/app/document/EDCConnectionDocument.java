package com.custodix.insite.local.ehr2edc.mongo.app.document;

import java.net.URI;
import java.util.Optional;

import javax.persistence.EmbeddedId;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalSiteId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

@Document(collection = EDCConnectionDocument.COLLECTION)
public class EDCConnectionDocument {
	public static final String COLLECTION = "EDCConnection";

	@EmbeddedId
	private final EDCConnectionDocumentId id;
	private final EDCSystem edcSystem;
	private final String externalSiteId;
	private final String clinicalDataURI;
	private final String username;
	private final String password;
	private final boolean enabled;
	private final String studyIdOverride;

	@PersistenceConstructor
	private EDCConnectionDocument(EDCConnectionDocumentId id, EDCSystem edcSystem, String externalSiteId,
			String clinicalDataURI, String username, String password, Boolean enabled, String studyIdOverride) {
		this.id = id;
		this.edcSystem = edcSystem;
		this.externalSiteId = externalSiteId;
		this.clinicalDataURI = clinicalDataURI;
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.studyIdOverride = studyIdOverride;
	}

	public static EDCConnectionDocument from(ExternalEDCConnection externalEDCConnection) {
		if (externalEDCConnection == null) {
			return null;
		}
		return new EDCConnectionDocument(EDCConnectionDocumentId.from(externalEDCConnection),
				externalEDCConnection.getEdcSystem(), externalEDCConnection.getExternalSiteId()
				.getId(), externalEDCConnection.getClinicalDataURI()
				.toASCIIString(), externalEDCConnection.getUsername(), externalEDCConnection.getPassword(),
				externalEDCConnection.isEnabled(), getStudyIdOverride(externalEDCConnection));
	}

	private static String getStudyIdOverride(ExternalEDCConnection externalEDCConnection) {
		final Optional<StudyId> studyIdOverride = externalEDCConnection.getStudyIdOverride();
		if (studyIdOverride.isPresent()) {
			final StudyId studyId = studyIdOverride.get();
			return studyId.getId();
		}
		return null;
	}

	public EDCConnectionDocumentId getId() {
		return this.id;
	}

	public EDCSystem getEdcSystem() {
		return edcSystem;
	}

	public String getExternalSiteId() {
		return externalSiteId;
	}

	public String getStudyIdOverride() {
		return studyIdOverride;
	}

	public String getClinicalDataURI() {
		return clinicalDataURI;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public ExternalEDCConnection to() {
		return ExternalEDCConnection.newBuilder()
				.withStudyId(StudyId.of(id.getStudyId()))
				.withEdcSystem(edcSystem)
				.withConnectionType(id.getConnectionType())
				.withUsername(username)
				.withPassword(password)
				.withExternalSiteId(ExternalSiteId.of(externalSiteId))
				.withClinicalDataURI(URI.create(clinicalDataURI))
				.withEnabled(enabled)
				.withStudyIdOVerride(studyIdOverride == null ? null : StudyId.of(studyIdOverride))
				.build();
	}

}
