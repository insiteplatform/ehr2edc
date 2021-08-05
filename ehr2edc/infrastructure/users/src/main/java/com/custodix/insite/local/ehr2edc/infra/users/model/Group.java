package com.custodix.insite.local.ehr2edc.infra.users.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

/**
 * The group to which a user can belong. A group can have a collection of
 * authorities assigned to it.
 */
@Entity
@Table(name = "usergroup")
public class Group extends BaseEntity {

	private static final long serialVersionUID = 1L;
	@Column(name = "name",
			unique = true,
			nullable = false)
	private String name;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usergroup_authority",
			   joinColumns = { @JoinColumn(name = "group_id") },
			   inverseJoinColumns = { @JoinColumn(name = "authority_id") })
	private Set<Authority> authorities = new HashSet<>();

	public Group() {
	}

	public Group(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}

	public boolean addAuthority(Authority authority) {
		return authorities.add(authority);
	}

	public boolean removeAuthority(Authority authority) {
		return authorities.remove(authority);
	}

	public boolean hasAuthority(Authority authority) {
		return authorities.contains(authority);
	}

}