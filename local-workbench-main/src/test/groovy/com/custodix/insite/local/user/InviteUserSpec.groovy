package com.custodix.insite.local.user

import com.custodix.insite.local.user.vocabulary.Email
import eu.ehr4cr.workbench.local.dao.SecurityDao
import eu.ehr4cr.workbench.local.eventhandlers.user.UserInvitedEvent
import eu.ehr4cr.workbench.local.exception.security.InvalidPermissionsException
import eu.ehr4cr.workbench.local.exception.security.InvitedUserExistsException
import eu.ehr4cr.workbench.local.global.AuthorityType
import eu.ehr4cr.workbench.local.global.GroupType
import eu.ehr4cr.workbench.local.model.security.User
import eu.ehr4cr.workbench.local.model.security.UserRole
import eu.ehr4cr.workbench.local.model.security.UserStatus
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title
import spock.lang.Unroll

import java.util.stream.Collectors

import static com.custodix.insite.local.user.InviteUser.Request
import static org.assertj.core.api.Java6Assertions.assertThat

@Title("Invite user")
class InviteUserSpec extends AbstractSpecification {
    private static final String EMAIL = "invite-user@custodix.com"
    private static final String NAME = "Invite User"
    private static final List<UserRole> ROLES = Arrays.asList(UserRole.PWR)
    private static final EXISTING_EMAIL = "activeUser@custodix.com"
    private static final EXISTING_NAME = "Active User"

    @Autowired
    private InviteUser inviteUser
    @Autowired
    private SecurityDao securityDao

    def "A user with 'ManageUsers_Edit' authority can invite a new user"() {
        given: "A user with 'MANAGE_ACCOUNTS' authority"
        User user = users.aUserWithAuthority(AuthorityType.MANAGE_ACCOUNTS)
        users.login(user)

        when: "The user invites a new user"
        Request request = createRequest(EMAIL, NAME)
        inviteUser.invite(request)

        then: "The user is created and invited"
        User invitedUser = securityDao.findUserByEmail(Email.of(EMAIL)).get()
        validateUser(invitedUser)

        and: "The user has appropriate roles"
        validateUserRoles(invitedUser)

        and: "A user invited event is published"
        validateEventWasPublishedForInvite(invitedUser)
    }

    def "A user without 'ManageUsers_Edit' authority cannot invite a new user"() {
        given: "A user without 'MANAGE_ACCOUNTS' authority"
        User user = users.aUserWithoutAuthority(AuthorityType.MANAGE_ACCOUNTS)
        users.login(user)

        when: "The user invites a new user"
        Request request = createRequest(EMAIL, NAME)
        inviteUser.invite(request)

        then: "The user is not invited"
        thrown InvalidPermissionsException
    }

    @Unroll
    def "A user with appropriate permissions cannot invite a new user with a duplicate #attribute"() {
        given: "A user with 'MANAGE_ACCOUNTS' authority"
        User user = users.aUserWithAuthority(AuthorityType.MANAGE_ACCOUNTS)
        users.login(user)

        and: "An existing user"
        users.anActiveUser()

        when: "The user invites a new user with a duplicate #attribute"
        Request request = createRequest(email, username)
        inviteUser.invite(request)

        then: "The user is not invited"
        thrown InvitedUserExistsException

        where:
        attribute  | email          | username      | _
        "email"    | EXISTING_EMAIL | NAME          | _
        "username" | EMAIL          | EXISTING_NAME | _
    }

    private void validateUser(User user) {
        assertThat(user.getEmail()).as("email").isEqualTo(EMAIL)
        assertThat(user.getUsername()).as("name").isEqualTo(NAME)
        assertThat(user.getStatus()).as("status").isEqualTo(UserStatus.INVITED)
    }

    private void validateUserRoles(User user) {
        Set<GroupType> expectedGroups = new HashSet<>(ROLES.stream().map({ r -> r.getGroupType() }).collect(Collectors.toSet()))
        expectedGroups.add(GroupType.USR)
        Set<GroupType> actualGroups = user.getGroups().stream().map({ g -> g.getType() }).collect(Collectors.toSet())
        assertThat(actualGroups).containsExactlyElementsOf(expectedGroups)
    }

    private void validateEventWasPublishedForInvite(User user) {
        List<UserInvitedEvent> events = eventPublisher.getAllEventsFor(UserInvitedEvent.class)
        assertThat(events).hasSize(1)
        assertThat(events.get(0).getUser()).isEqualTo(user)
    }

    private Request createRequest(String email, String username) {
        return Request.newBuilder()
                .withEmail(email)
                .withUsername(username)
                .withRoles(ROLES)
                .build()
    }
}
