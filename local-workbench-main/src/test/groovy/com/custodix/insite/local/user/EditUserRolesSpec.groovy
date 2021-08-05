package com.custodix.insite.local.user

import eu.ehr4cr.workbench.local.exception.security.InvalidPermissionsException
import eu.ehr4cr.workbench.local.global.AuthorityType
import eu.ehr4cr.workbench.local.global.GroupType
import eu.ehr4cr.workbench.local.model.security.User
import eu.ehr4cr.workbench.local.model.security.UserRole
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title

import java.util.stream.Collectors

import static com.custodix.insite.local.user.EditUserRoles.Request
import static org.assertj.core.api.Java6Assertions.assertThat

@Title("Edit user roles")
class EditUserRolesSpec extends AbstractSpecification {
    @Autowired
    private EditUserRoles editUserRoles

    def "A user with 'ManageUsers_Edit' authority can edit a user's roles"() {
        given: "I have 'MANAGE_ACCOUNTS' authority"
        User authorizedUser = users.aUserWithAuthority(AuthorityType.MANAGE_ACCOUNTS)
        users.login(authorizedUser)

        and: "A user with role PWR"
        User user = users.aPowerUser()

        when: "I edit the user's role to DRM"
        Request request = Request.newBuilder()
                .withUserId(user.getId())
                .withUserRoles(Collections.singletonList(UserRole.DRM))
                .build()
        editUserRoles.editRoles(request)

        then: "The user no longer has the PWR role but has the DRM role"
        Set<GroupType> actualGroups = getGroupsOfUser(user)
        assertThat(actualGroups).containsExactlyInAnyOrder(GroupType.USR, GroupType.DRM)
    }

    def "A user without 'ManageUsers_Edit' authority cannot edit a user's roles"() {
        given: "I don't have 'MANAGE_ACCOUNTS' authority"
        User unauthorizedUser = users.aUserWithoutAuthority(AuthorityType.MANAGE_ACCOUNTS)
        users.login(unauthorizedUser)

        and: "A user with role PWR"
        User user = users.aPowerUser()

        when: "I edit the user's role to DRM"
        Request request = Request.newBuilder()
                .withUserId(user.getId())
                .withUserRoles(Collections.singletonList(UserRole.DRM))
                .build()
        editUserRoles.editRoles(request)

        then: "Access is denied"
        thrown InvalidPermissionsException
    }

    private Set<GroupType> getGroupsOfUser(User user) {
        return user.getGroups()
                .stream()
                .map({ g -> g.getType() })
                .collect(Collectors.toSet())
    }

    }
