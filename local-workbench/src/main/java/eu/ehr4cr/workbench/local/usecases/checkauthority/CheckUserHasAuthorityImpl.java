package eu.ehr4cr.workbench.local.usecases.checkauthority;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.security.SecurityContextUser;

@Component
class CheckUserHasAuthorityImpl implements CheckUserHasAuthority {

	@Override
	public boolean check(AuthorityType authorityType) {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		User user = ((SecurityContextUser) auth.getPrincipal()).getWorkbenchUser();
		return user.hasAuthority(authorityType);
	}

}
