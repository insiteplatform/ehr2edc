package eu.ehr4cr.workbench.local.model.security;

import static eu.ehr4cr.workbench.local.Constants.STRING_SHORT;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.model.BaseEntity;

/**
 * The rights of a User or Group.
 */
@Entity
@Table(name = "authority")
public class Authority extends BaseEntity {

	private static final long serialVersionUID = 1L;
	@Size(max = STRING_SHORT)
	@Column(name = "name", unique = true, nullable = false, length = STRING_SHORT)
	private String name;

	public Authority() {
	}

	public Authority(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isType(AuthorityType type) {
		return name.equalsIgnoreCase(type.name());
	}

	public AuthorityType getType() {
		return AuthorityType.valueOf(name);
	}
}
