package eu.ehr4cr.workbench.local.usecases.checkgroupauthorityhealth;

import static eu.ehr4cr.workbench.local.global.AuthorityType.*;
import static eu.ehr4cr.workbench.local.global.GroupType.ADM;
import static eu.ehr4cr.workbench.local.global.GroupType.DRM;
import static eu.ehr4cr.workbench.local.global.GroupType.PWR;
import static eu.ehr4cr.workbench.local.global.GroupType.USR;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.Group;
import eu.ehr4cr.workbench.local.usecases.checkgroupauthorityhealth.CheckGroupAuthorityHealth.Response.AuthorityHealth;
import eu.ehr4cr.workbench.local.usecases.checkgroupauthorityhealth.CheckGroupAuthorityHealth.Response.GroupHealth;

@Component
class CheckGroupAuthorityHealthUseCase implements CheckGroupAuthorityHealth {
	private static final Map<GroupType, List<AuthorityType>> EXPECTED_GROUP_TO_AUTHORITY_MAPPING;

	static {
		EXPECTED_GROUP_TO_AUTHORITY_MAPPING = new EnumMap<>(GroupType.class);
		EXPECTED_GROUP_TO_AUTHORITY_MAPPING.put(DRM,
				asList(VIEW_AUDIT_QUERIES, MANAGE_PLACEMENT, VIEW_RECRUITMENT_STUDIES, ASSIGN_INVESTIGATORS,
						ACCEPT_RECRUITMENT_STUDIES, DECLINE_RECRUITMENT_STUDIES, ARCHIVE_RECRUITMENT_STUDIES,
						EDIT_RECRUITMENT_STUDIES));
		EXPECTED_GROUP_TO_AUTHORITY_MAPPING.put(ADM, asList(VIEW_ACCOUNTS, MANAGE_ACCOUNTS, VIEW_ACTUATOR_DETAILS));
		EXPECTED_GROUP_TO_AUTHORITY_MAPPING.put(USR, asList(VIEW_SPONSOR_STUDIES, CREATE_COHORTS));
		EXPECTED_GROUP_TO_AUTHORITY_MAPPING.put(PWR, asList(VIEW_PATIENT_FACTS));
	}

	private final SecurityDao securityDao;

	@Autowired
	public CheckGroupAuthorityHealthUseCase(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	public static List<AuthorityType> getExpectedAuthoritiesForGroup(GroupType groupType) {
		return EXPECTED_GROUP_TO_AUTHORITY_MAPPING.get(groupType);
	}

	@Override
	public Response checkHealth() {
		List<AuthorityHealth> authorityHealthInfo = getAuthorityHealthInfo();
		List<GroupHealth> groupHealthInfo = getGroupHealthInfo();
		return Response.builder()
				.withHealthy(isResponseHealthy(authorityHealthInfo, groupHealthInfo))
				.withAuthorityHealthInfo(authorityHealthInfo)
				.withGroupHealthInfo(groupHealthInfo)
				.build();
	}

	private boolean isResponseHealthy(List<AuthorityHealth> authorityHealthInfo, List<GroupHealth> groupHealthInfo) {
		boolean allAuthoritiesHealthy = authorityHealthInfo.stream()
				.allMatch(AuthorityHealth::isHealthy);
		boolean allGroupsHealthy = groupHealthInfo.stream()
				.allMatch(GroupHealth::isHealthy);
		return allAuthoritiesHealthy && allGroupsHealthy;
	}

	private List<AuthorityHealth> getAuthorityHealthInfo() {
		return Arrays.stream(AuthorityType.values())
				.map(this::checkAuthorityHealth)
				.collect(toList());
	}

	private List<GroupHealth> getGroupHealthInfo() {
		return Arrays.stream(GroupType.values())
				.map(this::checkGroupHealth)
				.collect(toList());
	}

	private AuthorityHealth checkAuthorityHealth(AuthorityType authorityType) {
		return AuthorityHealth.builder()
				.withName(authorityType.getInnerName())
				.withHealthy(isAuthorityHealthy(authorityType))
				.build();
	}

	private GroupHealth checkGroupHealth(GroupType groupType) {
		boolean presentInDatabase = isGroupPresentInDatabase(groupType);
		if (presentInDatabase) {
			return checkGroupAuthorities(groupType);
		} else {
			return groupMissingInDatabase(groupType);
		}

	}

	private boolean isGroupPresentInDatabase(GroupType groupType) {
		return securityDao.findGroupByGroupname(groupType.getInnerName()) != null;
	}

	private boolean isAuthorityHealthy(AuthorityType authorityType) {
		return securityDao.findAuthorityByName(authorityType.getInnerName()) != null;
	}

	private GroupHealth groupMissingInDatabase(GroupType groupType) {
		return GroupHealth.builder()
				.withHealthy(false)
				.withName(groupType.getInnerName())
				.withPresentInDatabase(false)
				.withRequiredAuthorities(emptyList())
				.withForbiddenAuthorities(emptyList())
				.build();
	}

	private GroupHealth checkGroupAuthorities(GroupType groupType) {
		List<AuthorityType> missingAuthorities = getRequiredAuthorities(groupType);
		List<AuthorityType> undesiredAuthorities = getForbiddenAuthorities(groupType);
		return GroupHealth.builder()
				.withHealthy(missingAuthorities.isEmpty() && undesiredAuthorities.isEmpty())
				.withName(groupType.getInnerName())
				.withPresentInDatabase(true)
				.withRequiredAuthorities(missingAuthorities)
				.withForbiddenAuthorities(undesiredAuthorities)
				.build();
	}

	private List<AuthorityType> getRequiredAuthorities(GroupType groupType) {
		Group group = securityDao.findGroupByGroupname(groupType.getInnerName());
		List<AuthorityType> expectedAuthorities = getExpectedAuthoritiesForGroup(groupType);
		return expectedAuthorities.stream()
				.filter(a -> !group.hasAuthority(a))
				.collect(toList());
	}

	private List<AuthorityType> getForbiddenAuthorities(GroupType groupType) {
		Group group = securityDao.findGroupByGroupname(groupType.getInnerName());
		List<AuthorityType> expectedAuthorities = getExpectedAuthoritiesForGroup(groupType);
		return Arrays.stream(AuthorityType.values())
				.filter(a -> !expectedAuthorities.contains(a) && group.hasAuthority(a))
				.collect(toList());
	}
}