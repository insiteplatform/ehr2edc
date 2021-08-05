package eu.ehr4cr.workbench.local.usecases.checkclinicalstudypermissions;

import eu.ehr4cr.workbench.local.model.security.User;

public interface CheckUserHasPermissionForClinicalStudy {
	boolean check(User user, Long clinicalStudyId);
}
