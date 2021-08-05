package com.custodix.insite.local.ehr2edc.infra.users.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The rights of a User or Group.
 */
@Entity
@Table(name = "authority")
public class Authority extends BaseEntity {

	private static final long serialVersionUID = 1L;
	@Column(name = "name",
			unique = true,
			nullable = false)
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

}
