package com.custodix.insite.local.ehr2edc.infra.users.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "app_user")
public class User extends BaseEntity {

	private static final long serialVersionUID = 1L;
	@Embedded
	private UserCredential credential = new UserCredential();

	@Column(name = "username",
			unique = true,
			nullable = false)
	private String username;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "email")
	private String email;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_authority",
			   joinColumns = { @JoinColumn(name = "user_id") },
			   inverseJoinColumns = { @JoinColumn(name = "authority_id") })
	private Set<Authority> authorities = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usergroup_user",
			   joinColumns = { @JoinColumn(name = "user_id") },
			   inverseJoinColumns = { @JoinColumn(name = "group_id") })
	private Set<Group> groups = new HashSet<>();

	public User() {
		//jpa
	}

	UserCredential getCredential() {
		return credential;
	}

	void setCredential(UserCredential credential) {
		this.credential = credential;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Set<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean addAuthority(Authority authority) {
		return authorities.add(authority);
	}

	public boolean removeAuthority(Authority authority) {
		return authorities.remove(authority);
	}

	public boolean isDrm() {
		return getGroups()
				.stream()
				.anyMatch(g -> "Data Relationship Managers"
						.equals(g.getName()));
	}
}