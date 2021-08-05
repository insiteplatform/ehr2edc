package eu.ehr4cr.workbench.local.usecases.checkuserpermissions;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.security.SecurityContextUser;
import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier;

@Component
class CheckUserHasPermissionForAccountImpl implements CheckUserHasPermissionForAccount {
	@Override
	public boolean check(User user, UserIdentifier accountUserIdentifier) {
		return user.getIdentifier().equals(accountUserIdentifier);
	}

	@Override
	public boolean check(UserIdentifier accountUserIdentifier) {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		User user = ((SecurityContextUser) auth.getPrincipal()).getWorkbenchUser();
		return check(user, accountUserIdentifier);
	}
}
