package eu.ehr4cr.workbench.local.model.clinicalStudy;

import java.util.Collections;
import java.util.List;

import javax.persistence.*;

import eu.ehr4cr.workbench.local.model.patient.Patient;
import eu.ehr4cr.workbench.local.model.security.User;

@Entity
@Table(name = "physicians")
public class TreatingPhysician {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String providerId;

	@Column(name = "isDefault")
	private Boolean isDefault;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "userId")
	private User user;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "physician")
	private List<Patient> patients;

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Patient> getPatients() {
		return Collections.unmodifiableList(patients);
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

}
