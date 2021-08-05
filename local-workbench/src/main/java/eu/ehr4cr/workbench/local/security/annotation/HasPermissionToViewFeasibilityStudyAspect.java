package eu.ehr4cr.workbench.local.security.annotation;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.model.security.CurrentUser;
import eu.ehr4cr.workbench.local.model.security.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static eu.ehr4cr.workbench.local.global.AuthorityType.VIEW_SPONSOR_STUDIES;

@Aspect
@Component
public class HasPermissionToViewFeasibilityStudyAspect {
	private final SecurityDao securityDao;
	private final CurrentUser currentUser;

	@Autowired
	public HasPermissionToViewFeasibilityStudyAspect(SecurityDao securityDao, final CurrentUser currentUser) {
		this.securityDao = securityDao;
		this.currentUser = currentUser;
	}

	@Transactional
	@Around("@annotation(HasPermissionToViewFeasibilityStudy)")
	public Object handle(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		if (userHasPermission()) {
			return proceedingJoinPoint.proceed();
		} else {
			throw new AccessDeniedException("User does not have access to the trial design application");
		}
	}

	private boolean userHasPermission() {
		Optional<User> user = currentUser.findCurrentUser();
		return user.map(u -> securityDao.userHasAuthority(u.getId(), VIEW_SPONSOR_STUDIES))
				.orElse(false);
	}
}