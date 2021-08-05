package eu.ehr4cr.workbench.local.usecases.userrecovery;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.ehr4cr.workbench.local.exception.security.UserRecoveryUnavailableException;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.service.ISecurityQuestionService;

@Service
public class RetrieveUserSecurityQuestionUseCase implements RetrieveUserSecurityQuestion {
	private final ISecurityQuestionService securityQuestionService;

	@Autowired
	public RetrieveUserSecurityQuestionUseCase(ISecurityQuestionService securityQuestionService) {
		this.securityQuestionService = securityQuestionService;
	}

	@Override
	public String retrieveQuestion(RetrieveUserSecurityQuestionRequest request) {
		checkIfRecoverable(request.getUser());
		return getSecurityQuestion(request.getLocale(), request.getUser());
	}

	private void checkIfRecoverable(User user) {
		if (!user.isRecoverable() || !user.hasSecurityQuestion()) {
			throw new UserRecoveryUnavailableException();
		}
	}

	private String getSecurityQuestion(Locale locale, User foundUser) {
		String questionId = foundUser.getSecurityQuestionId();
		return securityQuestionService.getQuestionByIdForLocale(questionId, locale)
				.getText();
	}
}