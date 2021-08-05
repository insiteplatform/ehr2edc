package eu.ehr4cr.workbench.local.model.clinicalStudy;

import javax.persistence.*;

import eu.ehr4cr.workbench.local.model.security.User;

@Entity
@Table(name = "clinical_study_patient_assignment")
public class PatientAssignment {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Long patientId;

	@ManyToOne
	private User user;

	@ManyToOne
	private ProtocolVersion protocolVersion;

	@Enumerated(EnumType.STRING)
	private PatientAssignmentType assignmentType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPatientId() {
		return patientId;
	}

	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ProtocolVersion getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(ProtocolVersion protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public PatientAssignmentType getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(PatientAssignmentType assignmentType) {
		this.assignmentType = assignmentType;
	}
}