package eu.ehr4cr.workbench.local.model.clinicalStudy;

import eu.ehr4cr.workbench.local.model.security.User;

import javax.persistence.*;

@Entity
@Table(name = "principal_investigator")
public class PrincipalInvestigator {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Boolean occupied;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "userId")
	private User user;

	private PrincipalInvestigator() {
		// JPA
	}

	public PrincipalInvestigator(User user) {
		this.user = user;
		this.occupied = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getOccupied() {
		return occupied;
	}

	public void setOccupied(Boolean occupied) {
		this.occupied = occupied;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PrincipalInvestigator) {
			PrincipalInvestigator other = (PrincipalInvestigator) obj;
			return this.id.equals(other.id);
		} else {
			return false;
		}
	}

}
