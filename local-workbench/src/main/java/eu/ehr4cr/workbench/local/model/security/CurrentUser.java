package eu.ehr4cr.workbench.local.model.security;

import java.util.Optional;

public interface CurrentUser {

    Boolean isSystemUser();

    User get();

    Optional<User> findCurrentUser();

}
