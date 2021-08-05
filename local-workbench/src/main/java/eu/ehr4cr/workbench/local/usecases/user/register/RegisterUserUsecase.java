package eu.ehr4cr.workbench.local.usecases.user.register;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.exception.security.UserExistsException;
import eu.ehr4cr.workbench.local.global.GroupType;
import eu.ehr4cr.workbench.local.model.security.Group;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.properties.PropertyProvider;
import eu.ehr4cr.workbench.local.service.email.MailService;
import eu.ehr4cr.workbench.local.service.email.model.ImmutableUserRegistrationMailContent;
import eu.ehr4cr.workbench.local.service.email.model.UserRegistrationMailContent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;

@Component
class RegisterUserUsecase implements RegisterUser {
	private final SecurityDao securityDao;
	private final MailService mailService;
	private final PropertyProvider propertyProvider;

	RegisterUserUsecase(SecurityDao securityDao, MailService mailService, PropertyProvider propertyProvider) {
		this.securityDao = securityDao;
		this.mailService = mailService;
		this.propertyProvider = propertyProvider;
	}

	@Transactional
	@Override
	public void register(Request request) {
		createUser(request.getUserEmail(), request.getUserName());
		sendMail(request.getUserName());
	}

	private void createUser(String email, String username) {
		if (securityDao.userAlreadyExists(email, username)) {
			throw new UserExistsException();
		}
		Group userGroup = securityDao.findGroup(GroupType.USR);
		User user = new User(username, email, new HashSet<>(Collections.singletonList(userGroup)));
		securityDao.persist(user);
	}

	private void sendMail(String username) {
		UserRegistrationMailContent mailContent = ImmutableUserRegistrationMailContent.builder()
				.username(username)
				.acceptUrl(propertyProvider.getBaseUrl() + WebRoutes.manageMembers)
				.build();
		mailService.sendMail(mailContent, GroupType.ADM);
	}
}
