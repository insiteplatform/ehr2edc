package eu.ehr4cr.workbench.local.model.clinicalStudy;

import eu.ehr4cr.workbench.local.usecases.clinical.study.create.CreateClinicalStudyVersionInfo;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "clinical_study_protocol_version")
public class ProtocolVersion {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String version;

	@ManyToOne
	private ClinicalStudy study;

	@OneToMany(mappedBy = "protocolVersion", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<ProtocolVersionDocument> documents;

	@OneToMany(mappedBy = "protocolVersion", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Fetch(FetchMode.SELECT)
	@OrderBy("id DESC")
	private List<ProtocolVersionScreeningFilter> filters;

	@OneToMany(mappedBy = "protocolVersion", fetch = FetchType.LAZY)
	private List<PatientAssignment> assignments;

	public ProtocolVersion() {
		// JPA
	}

	public ProtocolVersion(ClinicalStudy clinicalStudy, CreateClinicalStudyVersionInfo versionInfo) {
		this.study = clinicalStudy;
		this.version = versionInfo.getName();
		this.filters = new ArrayList<>();
		this.documents = new ArrayList<>();
		addScreeningFilters(clinicalStudy, versionInfo);
		addDocuments(versionInfo);
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

	public ClinicalStudy getStudy() {
		return study;
	}

	public void setStudy(ClinicalStudy study) {
		this.study = study;
	}

	public List<ProtocolVersionDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<ProtocolVersionDocument> documents) {
		this.documents = documents;
	}

	public List<ProtocolVersionScreeningFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<ProtocolVersionScreeningFilter> filters) {
		this.filters = filters;
	}

	public ProtocolVersionScreeningFilter getActiveScreeningFilter() {
		return this.filters.size() == 0 ? null : this.filters.get(0);
	}

	private void addScreeningFilters(ClinicalStudy clinicalStudy, CreateClinicalStudyVersionInfo versionInfo) {
		ProtocolVersionScreeningFilter filter = new ProtocolVersionScreeningFilter(clinicalStudy, this,
				versionInfo.getQueryContent());
		filters.add(filter);
	}

	private void addDocuments(CreateClinicalStudyVersionInfo versionInfo) {
		Map<String, byte[]> docs = versionInfo.getDocuments();
		docs.forEach(this::addDocument);
	}

	private void addDocument(String name, byte[] content) {
		ProtocolVersionDocument doc = new ProtocolVersionDocument(this, name, content);
		documents.add(doc);
	}

	public String getStudyName() {
		return study.getName();
	}

	public String getStudyDescription() {
		return study.getDescription();
	}
}
