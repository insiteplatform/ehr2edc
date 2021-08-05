package eu.ehr4cr.workbench.local.security;

import java.io.Serializable;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.exception.security.InvalidPermissionsException;
import eu.ehr4cr.workbench.local.exception.security.MissingUserException;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.model.security.User;

@Service
public class CustomPermissionEvaluator implements PermissionEvaluator {

	@Autowired
	protected SecurityDao securityDao;

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomPermissionEvaluator.class);

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		return validatePermission(authentication, permission);
	}

	@Override
	public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
		return validatePermission(auth, permission);
	}

	private boolean validatePermission(Authentication authentication, Object permission) {
		boolean hasPermission = true;
		if (authentication != null && permission instanceof String) {
			AuthorityType authorityType = AuthorityType.valueOf(String.valueOf(permission));
			Authentication auth = SecurityContextHolder.getContext()
					.getAuthentication();
			User user = ((SecurityContextUser) auth.getPrincipal()).getWorkbenchUser();
			if (user != null && !securityDao.userHasAuthority(user.getId(), authorityType)) {
				String pattern = "User {0} does not have the permissions needed for this call.";
				LOGGER.error(MessageFormat.format(pattern, user.getUsername()));
				throw new InvalidPermissionsException(user);
			} else if (user == null) {
				throw new MissingUserException();
			}
		} else {
			hasPermission = false;
		}
		return hasPermission;
	}
}
