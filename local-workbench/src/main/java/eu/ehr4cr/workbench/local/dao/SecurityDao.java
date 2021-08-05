package eu.ehr4cr.workbench.local.dao;

import java.util.List;
import java.util.Optional;

import com.custodix.insite.local.user.vocabulary.Email;

import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.Authority;
import eu.ehr4cr.workbench.local.model.security.Group;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier;

/**
 * Interface to be used to access the local workbench's security-related
 * objects:
 * <ul>
 * <li>User</li>
 * <li>Group</li>
 * </ul>
 */
public interface SecurityDao extends GenericDao {

	/**
	 * Finds a user with the given username.
	 *
	 * @param username
	 *            the username to find the user for.
	 * @return the user or null, if it doesn't exist.
	 */
	User findUserByUsername(String username);

	/**
	 * Finds a user with the given email.
	 *
	 * @param email
	 *            the email to find the user for.
	 * @return the user or null, if it doesn't exist.
	 */
	Optional<User> findUserByEmail(Email email);

	Group findGroup(GroupType groupType);

	/**
	 * @deprecated  Use {@link #findGroup(GroupType)} ()} instead
	 */
	@Deprecated
	Group findGroupByGroupname(String groupname);

	/**
	 * Finds an authority with the given authority name.
	 *
	 * @param name
	 *            the name of the authority to search for.
	 * @return the authority, or null if it doesn't exist.
	 */
	Authority findAuthorityByName(String name);

	/**
	 * Finds all the groups of which the User with the given username is a
	 * member.
	 *
	 * @param username
	 *            the username for which to find groups.
	 * @return the groups, or an empty collection if the given username does not
	 *         exist or is not a member of any groups.
	 */
	List<Group> findGroupsByUsername(String username);

	List<Group> findGroupsByUserId(Long userId);

	/**
	 * Finds all the users belonging to the given group name.
	 *
	 * @param groupname
	 *            the group name for which to find all its users.
	 * @return the users of the group or an empty collection if the group has no
	 *         members or does not exist.
	 */
	List<User> findUsersByGroupname(String groupname);

	List<Group> findAllGroups();

	List<User> findAllUsers();

	User createUser(String email, String username, String plainPassword);

	User createUser(String email, String username);

	void updateUsername(Long userId, String username);

	User findUserById(Long userId);

	User findUserById(UserIdentifier userId);

	Optional<User> findUserByIdentifier(UserIdentifier userIdentifier);

	void deleteUser(Long userId);

	List<User> findInvalidUsers();

	void acceptUser(Long userId);

	Boolean userAlreadyExists(String email, String userName);

	List<User> findAllValidUsers();

	Optional<User> findActiveUserByEmail(Email email);

	User getActiveUserByEmail(Email email);

	boolean userHasAuthority(Long userId, AuthorityType authorityType);
}