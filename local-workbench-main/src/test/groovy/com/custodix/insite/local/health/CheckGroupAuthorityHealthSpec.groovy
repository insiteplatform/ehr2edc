package com.custodix.insite.local.health

import eu.ehr4cr.workbench.local.dao.SecurityDao
import eu.ehr4cr.workbench.local.global.AuthorityType
import eu.ehr4cr.workbench.local.global.GroupType
import eu.ehr4cr.workbench.local.model.security.Authority
import eu.ehr4cr.workbench.local.model.security.Group
import eu.ehr4cr.workbench.local.usecases.checkgroupauthorityhealth.CheckGroupAuthorityHealth
import eu.ehr4cr.workbench.local.usecases.checkgroupauthorityhealth.CheckGroupAuthorityHealthUseCase
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Title

import static eu.ehr4cr.workbench.local.global.AuthorityType.MANAGE_ACCOUNTS
import static eu.ehr4cr.workbench.local.global.GroupType.USR
import static java.util.Arrays.stream
import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.when

@Title("Check status of group to authority mapping")
@ContextConfiguration(classes = CheckGroupAuthorityHealthUseCase.class)
class CheckGroupAuthorityHealthSpec extends Specification {

    private static final AuthorityType MISSING_AUTHORITY_TYPE = MANAGE_ACCOUNTS
    private static final GroupType UNHEALTHY_GROUP_TYPE = USR

    @Autowired
    private CheckGroupAuthorityHealth checkGroupAuthorityHealth
    @MockBean
    private SecurityDao securityDao

    @Before
    public void init() {
        when(securityDao.findAuthorityByName(anyString())).thenAnswer({ inv -> mockAuthority(inv.getArgument(0)) })
        when(securityDao.findGroupByGroupname(anyString())).thenAnswer(
                { inv -> mockGroupWithValidAuthorities(inv.getArgument(0)) })
    }

    def "All groups and authorities are assigned correctly"() {
        when: "I ask for the group and authority health"
        CheckGroupAuthorityHealth.Response response = checkGroupAuthorityHealth.checkHealth()

        then: "All groups and authorities are marked as healthy"
        assertThat(response.isHealthy()).isTrue()
        assertAllAuthoritiesHealthy(response.getAuthorityHealthInfo())
        assertAllGroupsHealthy(response.getGroupHealthInfo())
    }

    def "One authority is missing in the database"() {
        given: "An authority that is missing in the database"
        when(securityDao.findAuthorityByName(MISSING_AUTHORITY_TYPE.getInnerName())).thenReturn(null)

        when: "I ask for the group and authority health"
        CheckGroupAuthorityHealth.Response response = checkGroupAuthorityHealth.checkHealth()

        then: "That authority is marked as unhealthy"
        assertThat(response.isHealthy()).isFalse()
        assertOneAuthorityUnhealthy(response.getAuthorityHealthInfo(), MISSING_AUTHORITY_TYPE)
        assertAllGroupsHealthy(response.getGroupHealthInfo())
    }

    def "One group is missing in the database"() {
        given: "A group that is missing in the database"
        when(securityDao.findGroupByGroupname(UNHEALTHY_GROUP_TYPE.getInnerName())).thenReturn(null)

        when: "I ask for the group and authority health"
        CheckGroupAuthorityHealth.Response response = checkGroupAuthorityHealth.checkHealth()

        then: "That group is marked as unhealthy"
        assertThat(response.isHealthy()).isFalse()
        assertAllAuthoritiesHealthy(response.getAuthorityHealthInfo())
        assertOneGroupMissingInDatabase(response.getGroupHealthInfo(), UNHEALTHY_GROUP_TYPE)
    }

    def "One group has missing authorities"() {
        given: "A group with no authorities in the database"
        when(securityDao.findGroupByGroupname(UNHEALTHY_GROUP_TYPE.getInnerName())).thenReturn(
                mockGroupWithNoAuthorities(UNHEALTHY_GROUP_TYPE.getInnerName()))

        when: "I ask for the group and authority health"
        CheckGroupAuthorityHealth.Response response = checkGroupAuthorityHealth.checkHealth()

        then: "That group is marked as unhealthy"
        assertThat(response.isHealthy()).isFalse()
        assertAllAuthoritiesHealthy(response.getAuthorityHealthInfo())
        assertOneGroupMissingAuthorities(response.getGroupHealthInfo(), UNHEALTHY_GROUP_TYPE)
    }

    def "One group has too many authorities"() {
        given: "A group with too many authorities in the database"
        when(securityDao.findGroupByGroupname(UNHEALTHY_GROUP_TYPE.getInnerName())).thenReturn(
                mockGroupWithAllAuthorities(UNHEALTHY_GROUP_TYPE.getInnerName()))

        when: "I ask for the group and authority health"
        CheckGroupAuthorityHealth.Response response = checkGroupAuthorityHealth.checkHealth()


        then: "That group is marked as unhealthy"
        assertThat(response.isHealthy()).isFalse()
        assertAllAuthoritiesHealthy(response.getAuthorityHealthInfo())
        assertOneGroupForbiddenAuthorities(response.getGroupHealthInfo(), UNHEALTHY_GROUP_TYPE)
    }

    private Group mockGroupWithValidAuthorities(String groupName) {
        Group group = mockGroupWithNoAuthorities(groupName)
        GroupType groupType = GroupType.fromInnerName(groupName)

        List<AuthorityType> authorityTypes = CheckGroupAuthorityHealthUseCase.getExpectedAuthoritiesForGroup(groupType)
        authorityTypes.forEach({ a -> addAuthorityToGroup(group, a) })
        return group
    }

    private Group mockGroupWithAllAuthorities(String groupName) {
        Group group = mockGroupWithNoAuthorities(groupName)
        stream(AuthorityType.values()).forEach({ a -> addAuthorityToGroup(group, a) })
        return group
    }

    private boolean addAuthorityToGroup(Group group, AuthorityType authorityType) {
        return group.addAuthority(mockAuthority(authorityType.getInnerName()))
    }

    private Group mockGroupWithNoAuthorities(String groupName) {
        return new Group(groupName)
    }

    private Authority mockAuthority(String authorityName) {
        AuthorityType authorityType = AuthorityType.valueOf(authorityName)
        Authority authority = new Authority(authorityName)
        authority.setId(authorityType.ordinal() + 0L)
        return authority
    }

    private void assertAllAuthoritiesHealthy(List<CheckGroupAuthorityHealth.Response.AuthorityHealth> healthItems) {
        assertHealthItemsForAllAuthoritiesPresent(healthItems)

        stream(AuthorityType.values()).map({ a -> findAuthorityHealth(healthItems, a) })
                .forEach({ aHealth -> assertAuthorityIsHealthy(aHealth) })
    }

    private void assertOneAuthorityUnhealthy(List<CheckGroupAuthorityHealth.Response.AuthorityHealth> healthItems, AuthorityType unhealthyAuthority) {
        AuthorityType[] authorityTypes = AuthorityType.values()
        assertHealthItemsForAllAuthoritiesPresent(healthItems)

        stream(authorityTypes).filter({ authorityType -> authorityType != unhealthyAuthority })
                .map({ a -> findAuthorityHealth(healthItems, a) })
                .forEach({ aHealth -> assertAuthorityIsHealthy(aHealth) })

        CheckGroupAuthorityHealth.Response.AuthorityHealth unhealthyItem = findAuthorityHealth(healthItems, unhealthyAuthority)
        assertAuthorityIsUnhealthy(unhealthyItem)
    }

    private CheckGroupAuthorityHealth.Response.AuthorityHealth findAuthorityHealth(List<CheckGroupAuthorityHealth.Response.AuthorityHealth> healthItems, AuthorityType authorityType) {
        String name = authorityType.getInnerName()
        return healthItems.stream()
                .filter({ hi -> name.equalsIgnoreCase(hi.getName()) })
                .findFirst()
                .orElse(null)
    }

    private void assertHealthItemsForAllAuthoritiesPresent(List<CheckGroupAuthorityHealth.Response.AuthorityHealth> healthItems) {
        AuthorityType[] authorityTypes = AuthorityType.values()
        assertThat(healthItems).as("Expected a health item for each authority")
                .hasSize(authorityTypes.length)
    }

    private void assertAuthorityIsHealthy(CheckGroupAuthorityHealth.Response.AuthorityHealth aHealth) {
        assertThat(aHealth).as("Expected an health item to be found for the authority")
                .isNotNull()
        assertThat(aHealth.isHealthy()).as("Expected the authority to be healthy")
                .isTrue()
    }

    private void assertAuthorityIsUnhealthy(CheckGroupAuthorityHealth.Response.AuthorityHealth aHealth) {
        assertThat(aHealth).as("Expected an health item to be found for the authority")
                .isNotNull()
        assertThat(aHealth.isHealthy()).as("Expected the authority to be unhealthy")
                .isFalse()
    }

    private void assertAllGroupsHealthy(List<CheckGroupAuthorityHealth.Response.GroupHealth> healthItems) {
        assertHealthItemsForAllGroupsPresent(healthItems)
        stream(GroupType.values()).map({ g -> findGroupHealth(healthItems, g) })
                .forEach({ g -> assertGroupIsHealthy(g) })
    }

    private void assertOneGroupMissingInDatabase(List<CheckGroupAuthorityHealth.Response.GroupHealth> healthItems, GroupType missingGroup) {
        assertHealthItemsForAllGroupsPresent(healthItems)
        assertOtherGroupsAreHealthy(healthItems, missingGroup)

        CheckGroupAuthorityHealth.Response.GroupHealth missingGroupHealth = findGroupHealth(healthItems, missingGroup)
        assertGroupIsMissingInDatabase(missingGroupHealth)
    }

    private void assertOneGroupMissingAuthorities(List<CheckGroupAuthorityHealth.Response.GroupHealth> healthItems, GroupType missingAuthoritiesGroup) {
        assertHealthItemsForAllGroupsPresent(healthItems)
        assertOtherGroupsAreHealthy(healthItems, missingAuthoritiesGroup)

        CheckGroupAuthorityHealth.Response.GroupHealth missingGroupHealth = findGroupHealth(healthItems, missingAuthoritiesGroup)
        assertGroupHasMissingAuthorities(missingGroupHealth)
    }

    private void assertOneGroupForbiddenAuthorities(List<CheckGroupAuthorityHealth.Response.GroupHealth> healthItems, GroupType forbiddenAuthoritiesGroup) {
        assertHealthItemsForAllGroupsPresent(healthItems)
        assertOtherGroupsAreHealthy(healthItems, forbiddenAuthoritiesGroup)

        CheckGroupAuthorityHealth.Response.GroupHealth forbiddenGroupHealth = findGroupHealth(healthItems, forbiddenAuthoritiesGroup)
        assertGroupHasForbiddenAuthorities(forbiddenGroupHealth)
    }

    private void assertOtherGroupsAreHealthy(List<CheckGroupAuthorityHealth.Response.GroupHealth> healthItems, GroupType unhealthyGroupType) {
        stream(GroupType.values()).filter({ g -> g != unhealthyGroupType })
                .map({ g -> findGroupHealth(healthItems, g) })
                .forEach({ g -> assertGroupIsHealthy(g) })
    }

    private void assertHealthItemsForAllGroupsPresent(List<CheckGroupAuthorityHealth.Response.GroupHealth> healthItems) {
        GroupType[] groupTypes = GroupType.values()
        assertThat(healthItems).as("Expected the group health info not to be empty")
                .hasSameSizeAs(groupTypes)
    }

    private CheckGroupAuthorityHealth.Response.GroupHealth findGroupHealth(List<CheckGroupAuthorityHealth.Response.GroupHealth> healthItems, GroupType groupType) {
        String name = groupType.getInnerName()
        return healthItems.stream()
                .filter({ hi -> name.equalsIgnoreCase(hi.getName()) })
                .findFirst()
                .orElse(null)
    }

    private void assertGroupIsHealthy(CheckGroupAuthorityHealth.Response.GroupHealth groupHealth) {
        assertThat(groupHealth.isHealthy()).as("Expected the group to be healthy")
                .isTrue()
        assertThat(groupHealth.isPresentInDatabase()).as("Expected the group to be present in the database")
                .isTrue()
        assertThat(groupHealth.hasRequiredAuthorities()).as(
                "Expected the group to not have missing required authorities")
                .isFalse()
        assertThat(groupHealth.hasForbiddenAuthorities()).as("Expected the group to not have forbidden authorities")
                .isFalse()
    }

    private void assertGroupIsMissingInDatabase(CheckGroupAuthorityHealth.Response.GroupHealth groupHealth) {
        assertThat(groupHealth.isHealthy()).as("Expected the group not to be healthy")
                .isFalse()
        assertThat(groupHealth.isPresentInDatabase()).as("Expected the group not to be present in the database")
                .isFalse()
        assertThat(groupHealth.hasRequiredAuthorities()).as(
                "Expected the group to not have missing required authorities")
                .isFalse()
        assertThat(groupHealth.hasForbiddenAuthorities()).as("Expected the group to not have forbidden authorities")
                .isFalse()
    }

    private void assertGroupHasMissingAuthorities(CheckGroupAuthorityHealth.Response.GroupHealth groupHealth) {
        assertThat(groupHealth.isHealthy()).as("Expected the group not to be healthy")
                .isFalse()
        assertThat(groupHealth.isPresentInDatabase()).as("Expected the group to be present in the database")
                .isTrue()
        assertThat(groupHealth.hasRequiredAuthorities()).as("Expected the group to have missing required authorities")
                .isTrue()
        assertThat(groupHealth.hasForbiddenAuthorities()).as("Expected the group to not have forbidden authorities")
                .isFalse()
    }

    private void assertGroupHasForbiddenAuthorities(CheckGroupAuthorityHealth.Response.GroupHealth groupHealth) {
        assertThat(groupHealth.isHealthy()).as("Expected the group not to be healthy")
                .isFalse()
        assertThat(groupHealth.isPresentInDatabase()).as("Expected the group to be present in the database")
                .isTrue()
        assertThat(groupHealth.hasRequiredAuthorities()).as(
                "Expected the group to have no missing required authorities")
                .isFalse()
        assertThat(groupHealth.hasForbiddenAuthorities()).as("Expected the group to have forbidden authorities")
                .isTrue()
    }
}