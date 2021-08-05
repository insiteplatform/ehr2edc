package eu.ehr4cr.workbench.local.model.security;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import eu.ehr4cr.workbench.local.exception.SystemException;
import eu.ehr4cr.workbench.local.security.SecurityContextUser;

@Component
public class CurrentUserImpl implements CurrentUser{

    @Override
    public Boolean isSystemUser() {
        return findCurrentUser().map(new SystemUser()::equals).orElse(false);
    }

    @Override
    public User get() {
        return findCurrentUser().orElseThrow(() -> new SystemException("User is not authenticated"));
    }

    @Override
    public Optional<User> findCurrentUser() {
        Optional<SecurityContextUser> securityContextUser = getAuthentication().map(this::getPrincipal);
        return securityContextUser.map(SecurityContextUser::getWorkbenchUser);
    }

    private SecurityContextUser getPrincipal(Authentication auth) {
        return (SecurityContextUser) auth.getPrincipal();
    }

    private Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(getContext().getAuthentication());
    }
}
