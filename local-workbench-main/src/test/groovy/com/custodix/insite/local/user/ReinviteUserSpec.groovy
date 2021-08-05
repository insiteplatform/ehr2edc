package com.custodix.insite.local.user

import eu.ehr4cr.workbench.local.dao.SecurityDao
import eu.ehr4cr.workbench.local.eventhandlers.user.UserInvitedEvent
import eu.ehr4cr.workbench.local.exception.security.InvalidPermissionsException
import eu.ehr4cr.workbench.local.global.AuthorityType
import eu.ehr4cr.workbench.local.model.security.User
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title
import spock.lang.Unroll

import static com.custodix.insite.local.user.ReinviteUser.Request
import static eu.ehr4cr.workbench.local.model.security.UserStatus.*
import static org.assertj.core.api.Java6Assertions.assertThat

@Title("Re-invite user")
class ReinviteUserSpec extends AbstractSpecification {
    @Autowired
    private ReinviteUser reinviteUser
    @Autowired
    private SecurityDao securityDao

    @Unroll
    def "A user with 'ManageUsers_Edit' authority can reinvite a user in state '#status'"() {
        given: "A user with 'MANAGE_ACCOUNTS' authority"
        User authorizedUser = users.aUserWithAuthority(AuthorityType.MANAGE_ACCOUNTS)
        users.login(authorizedUser)

        and: "A user in state '#status'"
        User user = users.aUser(status)
        String tempPassword = user.getTempPassword()

        when: "The user reinvites the user"
        Request request = Request.newBuilder().withUserId(user.getId()).build()
        reinviteUser.reinvite(request)

        then: "The user's invitation is reset"
        User updatedUser = securityDao.findUserById(user.getId())
        updatedUser.tempPassword != tempPassword
        !updatedUser.expired

        and: "A user invited event is published"
        validateEventWasPublishedForInvite(updatedUser)

        where:
        status             | _
        INVITED            | _
        EXPIRED_INVITATION | _
    }

    @Unroll
    def "A user with 'ManageUsers_Edit' authority cannot reinvite a user in state '#status'"() {
        given: "A user with 'MANAGE_ACCOUNTS' authority"
        User authorizedUser = users.aUserWithAuthority(AuthorityType.MANAGE_ACCOUNTS)
        users.login(authorizedUser)

        and: "A user in state '#status'"
        User user = users.aUser(status)

        when: "The user reinvites the user"
        Request request = Request.newBuilder().withUserId(user.getId()).build()
        reinviteUser.reinvite(request)

        then: "The user is not reinvited"
        thrown IllegalStateException

        where:
        status           | _
        PENDING          | _
        ACTIVE           | _
        RECOVERING       | _
        EXPIRED_RECOVERY | _
        DELETED          | _
    }

    def "A user without 'ManageUsers_Edit' authority cannot reinvite a user"() {
        given: "A user without 'MANAGE_ACCOUNTS' authority"
        User unauthorizedUser = users.aUserWithoutAuthority(AuthorityType.MANAGE_ACCOUNTS)
        users.login(unauthorizedUser)

        and: "A previously invited user"
        User user = users.anInvitedUser()

        when: "The user reinvites the previously invited user"
        Request request = Request.newBuilder().withUserId(user.getId()).build()
        reinviteUser.reinvite(request)

        then: "Access is denied"
        thrown InvalidPermissionsException
    }

    private void validateEventWasPublishedForInvite(User user) {
        List<UserInvitedEvent> events = eventPublisher.getAllEventsFor(UserInvitedEvent.class)
        assertThat(events).hasSize(1)
        assertThat(events.get(0).getUser()).isEqualTo(user)
    }
}
