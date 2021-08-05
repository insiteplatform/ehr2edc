package eu.ehr4cr.workbench.local.service;

import java.util.List;
import java.util.Optional;

import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.Authority;
import eu.ehr4cr.workbench.local.model.security.Group;
import eu.ehr4cr.workbench.local.model.security.User;

public interface IUserMgrService {
	User createUser(String email, String username, String password);

	Authority createAuthority(String innerName);

	Group createGroup(String groupname);

	void deleteUser(Long userId);

	void acceptUser(Long userId);

	void acceptUser(Long userId, String password);

	void assignUserToGroups(Long userId, List<GroupType> groups);

	boolean userHasAuthority(Long userId, AuthorityType authorityType);

	boolean userAlreadyExists(String email, String username);

	User findUserByUsername(String username);

	User findUserById(Long userId);

	Optional<User> findUserByEmail(String email);

	Group findGroupByGroupname(String groupname);

	Authority findAuthorityByName(String innerName);

	List<User> findAllUsers();

	List<Group> findAllGroups();

	List<User> findUsersByGroupname(String innerName);

	List<User> findInvalidUsers();

	List<User> findValidUsers();
}