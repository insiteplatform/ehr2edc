package eu.ehr4cr.workbench.local.usecases.checkcohortpatientpermissions;

import static eu.ehr4cr.workbench.local.global.AuthorityType.VIEW_PATIENT_FACTS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.security.SecurityContextUser;

@Component("CheckUserHasPermissionForCohortPatientImpl")
public class CheckUserHasPermissionForCohortPatientImpl implements CheckUserHasPermissionForCohortPatient {

	private final SecurityDao securityDao;

	@Autowired
	public CheckUserHasPermissionForCohortPatientImpl(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	@Override
	public boolean check(Object principal, Long patientId) {
		if (principal instanceof User) {
			return doCheck((User)principal, patientId);
		} else {
			return doCheck(((SecurityContextUser) principal).getWorkbenchUser(), patientId);
		}
	}
	private boolean doCheck(User user, Long patientId) {
		return securityDao.userHasAuthority(user.getId(), VIEW_PATIENT_FACTS);
	}
}