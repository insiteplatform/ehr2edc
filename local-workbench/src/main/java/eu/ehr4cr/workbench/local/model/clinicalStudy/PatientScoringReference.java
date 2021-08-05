package eu.ehr4cr.workbench.local.model.clinicalStudy;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "clinical_study_patient_scoring_reference")
public class PatientScoringReference {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne
	private ProtocolVersionScreeningFilter patientsFilter;

	@ManyToOne
	private ProtocolVersionScreeningFilter scoringFilter;

	private Date creationDate;

	private String repoKey;

	public PatientScoringReference() {
	}

	public PatientScoringReference(ProtocolVersionScreeningFilter patientsFilter,
			ProtocolVersionScreeningFilter scoringFilter, Date creationDate, String repoKey) {
		this.patientsFilter = patientsFilter;
		this.scoringFilter = scoringFilter;
		this.creationDate = creationDate;
		this.repoKey = repoKey;
	}

	public Long getId() {
		return id;
	}

	public ProtocolVersionScreeningFilter getPatientsFilter() {
		return patientsFilter;
	}

	public ProtocolVersionScreeningFilter getScoringFilter() {
		return scoringFilter;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getRepoKey() {
		return repoKey;
	}
}