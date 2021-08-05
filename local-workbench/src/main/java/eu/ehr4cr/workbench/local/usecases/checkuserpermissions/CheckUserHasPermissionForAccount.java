package eu.ehr4cr.workbench.local.usecases.checkuserpermissions;

import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier;

public interface CheckUserHasPermissionForAccount {
	boolean check(User user, UserIdentifier accountUserIdentifier);

	boolean check(UserIdentifier accountUserIdentifier);
}
