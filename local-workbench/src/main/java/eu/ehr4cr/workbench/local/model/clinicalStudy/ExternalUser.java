package eu.ehr4cr.workbench.local.model.clinicalStudy;

import javax.persistence.*;

@Entity
@Table(name = "external_user")
public class ExternalUser {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;
	private String email;
	private String companyName;

	private ExternalUser() {
		// JPA
	}

	public ExternalUser(String name, String email, String companyName) {
		this(name, companyName);
		this.email = email;
	}

	public ExternalUser(String name, String companyName) {
		this.name = name;
		this.companyName = companyName;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getCompanyName() {
		return companyName;
	}
}
