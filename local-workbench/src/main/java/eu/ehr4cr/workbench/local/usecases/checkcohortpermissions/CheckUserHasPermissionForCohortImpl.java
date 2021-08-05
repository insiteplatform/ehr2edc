package eu.ehr4cr.workbench.local.usecases.checkcohortpermissions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.security.SecurityContextUser;

@Component("checkUserHasPermissionForCohort")
class CheckUserHasPermissionForCohortImpl implements CheckUserHasPermissionForCohort {
	private final SecurityDao securityDao;

	@Autowired
	public CheckUserHasPermissionForCohortImpl(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	@Override
	public boolean check(Object principal, Long cohortId) {
		final SecurityContextUser securityContextUser = (SecurityContextUser) principal;
		User workbenchUser = securityContextUser.getWorkbenchUser();
		return check(workbenchUser, cohortId);
	}

	@Override
	public boolean check(User user, Long cohortId) {
		Long userId = user.getId();
		return securityDao.userHasAuthority(userId, AuthorityType.VIEW_PATIENT_FACTS);
	}
}