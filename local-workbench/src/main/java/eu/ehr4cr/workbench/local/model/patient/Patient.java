package eu.ehr4cr.workbench.local.model.patient;

import javax.persistence.*;

import eu.ehr4cr.workbench.local.model.clinicalStudy.TreatingPhysician;

@Entity
@Table(name = "patient")
public class Patient {
	@Id
	private Long patientId;

	private Short age;
	private String gender;

	@ManyToOne(optional = true)
	@JoinColumn(name = "tpId")
	private TreatingPhysician physician;

	public Long getPatientId() {
		return patientId;
	}

	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}

	public Short getAge() {
		return age;
	}

	public void setAge(Short age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((patientId == null) ? 0 : patientId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Patient other = (Patient) obj;
		if (patientId == null) {
			if (other.patientId != null)
				return false;
		} else if (!patientId.equals(other.patientId))
			return false;
		return true;
	}

}
