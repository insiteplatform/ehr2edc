package com.custodix.insite.local.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.Group;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.model.security.UserRole;
import eu.ehr4cr.workbench.local.security.annotation.HasPermission;

@Component
class EditUserRolesUsecase implements EditUserRoles {
	private final SecurityDao securityDao;

	EditUserRolesUsecase(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	@HasPermission(AuthorityType.MANAGE_ACCOUNTS)
	@Transactional
	@Override
	public void editRoles(Request request) {
		User user = securityDao.findUserById(request.getUserId());
		List<Group> allGroups = securityDao.findAllGroups();
		List<GroupType> newGroups = request.getUserRoles()
				.stream()
				.map(UserRole::getGroupType)
				.collect(Collectors.toList());
		user.setGroups(newGroups, allGroups);
		securityDao.merge(user);
	}
}
