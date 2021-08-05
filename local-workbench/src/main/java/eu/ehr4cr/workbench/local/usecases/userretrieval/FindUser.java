package eu.ehr4cr.workbench.local.usecases.userretrieval;

import java.util.Optional;

import com.custodix.insite.local.user.vocabulary.Email;

import eu.ehr4cr.workbench.local.model.security.User;

public interface FindUser {
	Optional<User> findByEmail(Email email);
}