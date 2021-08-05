package com.custodix.insite.local.user

import com.custodix.insite.local.cohort.scenario.UserGroup
import eu.ehr4cr.workbench.local.global.AuthorityType
import eu.ehr4cr.workbench.local.global.GroupType
import eu.ehr4cr.workbench.local.model.security.User
import org.assertj.core.api.SoftAssertions
import spock.lang.Title
import spock.lang.Unroll

import java.util.stream.Stream

import static eu.ehr4cr.workbench.local.usecases.checkgroupauthorityhealth.CheckGroupAuthorityHealthUseCase.getExpectedAuthoritiesForGroup
import static java.util.stream.Collectors.toList

@Title("")
class GroupPermissionsSpec extends AbstractSpecification {

    @Unroll("Group #group.groupName can #expectedAuthorities")
    def "Group authorities for specific groups"() {
        when: "I am a user that is part of the #group.groupName group"
        User user = users.aUser(group)

        then: "I have all expected authorities: #expectedAuthorities"
        SoftAssertions softAssert = new SoftAssertions()
        expectedAuthorities
                .forEach({ a -> assertUserHasAuthority(user, a, softAssert) })
        and: "I don't have any other authorities"
        unexpectedAuthorities
                .forEach({ a -> assertUserDoesNotHaveAuthority(user, a, softAssert) })

        softAssert.assertAll()

        where:
        group << UserGroup.values()
        expectedAuthorities = getExpectedAuthorities(group)
        unexpectedAuthorities = getUnexpectedAuthorities(expectedAuthorities)
    }


    private List<AuthorityType> getExpectedAuthorities(UserGroup userGroup) {
        GroupType groupType = GroupType.fromInnerName(userGroup.groupName)
        return getExpectedAuthoritiesForGroup(groupType)
    }

    private List<AuthorityType> getUnexpectedAuthorities(List<AuthorityType> expectedAuthorities) {
        return Stream.of(AuthorityType.values())
                .filter({ a -> !expectedAuthorities.contains(a) })
                .collect(toList())
    }

    private void assertUserHasAuthority(User user, AuthorityType authorityType, SoftAssertions softAssert) {
        softAssert.assertThat(user.hasAuthority(authorityType))
                .as("Expected user to have the " + authorityType.name() + " authority")
                .isTrue()
    }

    private void assertUserDoesNotHaveAuthority(User user, AuthorityType authorityType, SoftAssertions softAssert) {
        softAssert.assertThat(user.hasAuthority(authorityType))
                .as("Expected user not to have the " + authorityType.name() + " authority")
                .isFalse()
    }
}