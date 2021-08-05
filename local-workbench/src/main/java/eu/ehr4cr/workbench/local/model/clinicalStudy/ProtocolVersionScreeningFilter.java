package eu.ehr4cr.workbench.local.model.clinicalStudy;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Date;
import java.util.Iterator;

import javax.persistence.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.vocabulary.clinical.FilterIdentifier;

@Entity
@Table(name = "clinical_study_protocol_version_filter")
public class ProtocolVersionScreeningFilter {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String version;

	@ManyToOne
	private ProtocolVersion protocolVersion;

	@Lob
	private String jsonContent;

	private Boolean archived;
	private Boolean fromSponsor;

	private Date creationDate;

	@OneToOne(optional = true)
	private User creator;
	@OneToOne(optional = true)
	private ExternalUser sponsor;

	public ProtocolVersionScreeningFilter() {
		// JPA
	}

	public ProtocolVersionScreeningFilter(ClinicalStudy study, ProtocolVersion protocolVersion, String queryContent) {
		this.archived = false;
		this.creationDate = study.getLastUpdated();
		this.fromSponsor = true;
		this.jsonContent = queryContent;
		this.protocolVersion = protocolVersion;
		this.sponsor = study.getSponsor();
		this.version = "0";
	}

	public FilterIdentifier getIdentifier() {
		return FilterIdentifier.newBuilder()
				.withFilterId(id)
				.withVersionId(protocolVersion.getId())
				.build();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ProtocolVersion getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(ProtocolVersion protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public String getJsonContent() {
		return jsonContent;
	}

	public void setJsonContent(String jsonContent) {
		this.jsonContent = jsonContent;
	}

	public Boolean getArchived() {
		return archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}

	public Boolean getFromSponsor() {
		return fromSponsor;
	}

	public void setFromSponsor(Boolean fromSponsor) {
		this.fromSponsor = fromSponsor;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public ExternalUser getSponsor() {
		return sponsor;
	}

	public void setSponsor(ExternalUser sponsor) {
		this.sponsor = sponsor;
	}

	public JsonNode getFormattedJsonContent() {
		try {
			JsonNode node = new ObjectMapper().readTree(jsonContent);
			if (node.size() > 0) {
				removeDescriptions(node);
			}
			return node;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void removeDescriptions(JsonNode node) {
		Iterator<String> keys = node.fieldNames();
		while (keys.hasNext()) {
			String key = keys.next();
			JsonNode criterion = node.get(key);
			if (criterion != null && criterion.has("description")) {
				((ObjectNode) criterion).remove("description");
			}
		}
	}

	public String getStudyName(){
		return protocolVersion.getStudyName();
	}

	public String getStudyDescription(){
		return protocolVersion.getStudyDescription();
	}
}