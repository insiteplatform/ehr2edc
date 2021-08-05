package eu.ehr4cr.workbench.local.usecases.userretrieval;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.custodix.insite.local.user.vocabulary.Email;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.model.security.User;

@Component
@Transactional(value = "localTransactionManager", readOnly = true)
class FindUserUseCase implements FindUser {

	private final SecurityDao securityDao;

	@Autowired
	public FindUserUseCase(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	@Override
	public Optional<User> findByEmail(Email email) {
		return securityDao.findUserByEmail(email);
	}
}