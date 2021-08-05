package eu.ehr4cr.workbench.local.model.security;

import static eu.ehr4cr.workbench.local.Constants.STRING_SHORT;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Size;

import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.BaseEntity;

/**
 * The group to which a user can belong. A group can have a collection of
 * authorities assigned to it.
 */
@Entity
@Table(name = "usergroup")
public class Group extends BaseEntity {

	private static final long serialVersionUID = 1L;
	@Size(max = STRING_SHORT)
	@Column(name = "name",
			unique = true,
			nullable = false,
			length = STRING_SHORT)
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

	public boolean hasAuthority(AuthorityType authorityType) {
		return authorities.stream()
				.anyMatch(a -> a.isType(authorityType));
	}

	public GroupType getType() {
		return GroupType.fromInnerName(name);
	}
}
