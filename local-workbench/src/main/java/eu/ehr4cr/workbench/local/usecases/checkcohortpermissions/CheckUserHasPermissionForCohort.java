package eu.ehr4cr.workbench.local.usecases.checkcohortpermissions;

import eu.ehr4cr.workbench.local.model.security.User;

public interface CheckUserHasPermissionForCohort {
	boolean check(Object principal, Long cohortId);

	boolean check(User user, Long cohortId);
}