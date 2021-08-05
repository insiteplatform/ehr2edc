package eu.ehr4cr.workbench.local.dao.jpa;

import static java.lang.String.format;

import java.util.*;

import org.springframework.stereotype.Repository;

import com.custodix.insite.local.shared.exceptions.NotFoundException;
import com.custodix.insite.local.user.vocabulary.Email;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.Authority;
import eu.ehr4cr.workbench.local.model.security.Group;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier;

@Repository
class JpaSecurityRepository extends AbstractJpaGenericRepository implements SecurityDao {
	private static final String QUERY_USER_BY_EMAIL = "select u from User u where lower(email) = :email";

	@Override
	public User createUser(String email, String username, String plainPassword) {
		return this.persist(new User(username,plainPassword,email));
	}

	@Override
	public User createUser(String email, String username) {
		return this.createUser(email, username, "");
	}

	@Override
	public User findUserByUsername(String username) {
		final String query = "select u from User u where username = :username";
		final Map<String, Object> params = new HashMap<>();
		params.put("username", username);
		return findSingleOrNull(query, params);
	}

	@Override
	public Optional<User> findUserByEmail(Email email) {
		final Map<String, Object> params = new HashMap<>();
		params.put("email", email.getValue()
				.toLowerCase());
		return findSingleOptional(QUERY_USER_BY_EMAIL, params);
	}

	@Override
	public Optional<User> findActiveUserByEmail(Email email) {
		final String query = "select u from User u where lower(email) = :email AND u.credential.deleted = false AND u.credential.enabled = true"
				+ " AND u.credential.password.password != ''";
		final Map<String, Object> params = new HashMap<>();
		params.put("email", email.getValue()
				.toLowerCase());
		return findSingleOptional(query, params);
	}

	@Override
	public User getActiveUserByEmail(Email email) {
		return findActiveUserByEmail(email).orElseThrow(
				() -> new NotFoundException(User.class, format("No active user found with email '%s'", email)));
	}

	@Override
	public Boolean userAlreadyExists(String email, String userName) {
		final String query = "select u from User u where lower(u.email) = :email OR u.username = :userName";
		final Map<String, Object> params = new HashMap<>();
		params.put("email", email.toLowerCase());
		params.put("userName", userName);
		User foundUser = findSingleOrNull(query, params);

		return foundUser != null;
	}

	@Override
	public List<User> findAllUsers() {
		final String query = "SELECT u FROM User u WHERE u.credential.deleted IS NULL OR u.credential.deleted = false ORDER BY u.username";
		return this.find(query, Collections.emptyMap());
	}

	@Override
	public List<User> findAllValidUsers() {
		final String query = "SELECT u FROM User u WHERE (u.credential.deleted IS NULL OR u.credential.deleted = false) "
				+ "AND u.credential.enabled = true ORDER BY u.username";
		return this.find(query, Collections.emptyMap());
	}

	/**
	 * @deprecated Please use {@link #findUserByIdentifier(UserIdentifier)}
	 * @param userId
	 * @return
	 */
	@Override
	@Deprecated
	public User findUserById(Long userId) {
		return findUserById(UserIdentifier.of(userId));
	}

	@Override
	public User findUserById(UserIdentifier userIdentifier) {
		final String query = "SELECT u FROM User u WHERE u.id = :userId";

		Map<String, Object> params = new HashMap<>();
		params.put("userId", userIdentifier.getId());

		return this.findSingleOrNull(query, params);
	}

	@Override
	public Optional<User> findUserByIdentifier(UserIdentifier userIdentifier) {
		return Optional.ofNullable(findUserById(userIdentifier));
	}

	@Override
	public void updateUsername(Long userId, String username) {
		User user = this.findUserById(userId);
		user.setUsername(username);
		this.merge(user);
	}

	@Override
	public void acceptUser(Long userId) {
		User user = findUserById(userId);
		user.setEnabled(true);
		this.persist(user);
	}

	@Override
	public void deleteUser(Long userId) {
		User user = this.findUserById(userId);
		this.remove(user);
	}

	@Override
	public List<Group> findGroupsByUsername(String username) {
		final String query = "select g from User u join u.groups as g where u.username = :username";
		final Map<String, Object> params = new HashMap<>();
		params.put("username", username);
		return find(query, params);
	}

	@Override
	public List<Group> findGroupsByUserId(Long userId) {
		final String query = "select g from User u join u.groups as g where u.id = :userId";
		final Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		return find(query, params);
	}

	@Override
	public List<User> findUsersByGroupname(String groupname) {
		String query = "select u from User u join u.groups as g where g.name = :groupname and u.credential.deleted = false AND u.credential.enabled = true";
		final Map<String, Object> params = new HashMap<>();
		params.put("groupname", groupname);
		return find(query, params);
	}

	@Override
	public List<User> findInvalidUsers() {
		String query = "SELECT u FROM User u  WHERE u.groups IS EMPTY AND u.credential.enabled = true AND u.credential.deleted = false";
		return this.find(query, Collections.emptyMap());
	}

	@Override
	public Group findGroup(GroupType groupType) {
		final String query = "select g from Group g where name = :groupname";
		final Map<String, Object> params = new HashMap<>();
		params.put("groupname", groupType.getInnerName());
		return findSingleOrNull(query, params);
	}

	@Override
	public Group findGroupByGroupname(String groupname) {
		final String query = "select g from Group g where name = :groupname";
		final Map<String, Object> params = new HashMap<>();
		params.put("groupname", groupname);
		return findSingleOrNull(query, params);
	}

	@Override
	public List<Group> findAllGroups() {
		final String query = "SELECT g FROM Group g ORDER BY g.name";
		return this.find(query, Collections.emptyMap());
	}

	@Override
	public Authority findAuthorityByName(String name) {
		final String query = "select g from Authority g where name = :name";
		final Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		return findSingleOrNull(query, params);
	}

	@Override
	public boolean userHasAuthority(Long userId, AuthorityType authorityType) {
		User user = this.findUserById(userId);
		return user.hasAuthority(authorityType);
	}
}
