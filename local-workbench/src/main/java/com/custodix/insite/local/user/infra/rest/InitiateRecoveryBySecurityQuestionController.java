package com.custodix.insite.local.user.infra.rest;

import static org.springframework.http.ResponseEntity.ok;

import java.util.Locale;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.custodix.insite.local.user.application.api.InitiateRecovery;
import com.custodix.insite.local.user.infra.rest.model.InitiateRecoveryRequest;
import com.custodix.insite.local.user.vocabulary.Email;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.BaseController;
import eu.ehr4cr.workbench.local.exception.security.UserRecoveryUnavailableException;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.usecases.userrecovery.RetrieveUserSecurityQuestion;
import eu.ehr4cr.workbench.local.usecases.userrecovery.RetrieveUserSecurityQuestionRequest;
import eu.ehr4cr.workbench.local.usecases.userretrieval.FindUser;

@RestController
class InitiateRecoveryBySecurityQuestionController extends BaseController {
	private final FindUser findUser;
	private final InitiateRecovery initiateRecovery;
	private final RetrieveUserSecurityQuestion securityQuestionUseCase;

	InitiateRecoveryBySecurityQuestionController(FindUser findUser, InitiateRecovery initiateRecovery,
			RetrieveUserSecurityQuestion securityQuestionUseCase) {
		this.findUser = findUser;
		this.initiateRecovery = initiateRecovery;
		this.securityQuestionUseCase = securityQuestionUseCase;
	}

	@PostMapping(value = WebRoutes.recoverAccount,
				 produces = MIME_TYPE_JSON_UTF8)
	protected ResponseEntity<String> doPost(@RequestBody InitiateRecoveryRequest request, Locale locale) {
		return request.getUserAnswer()
				.map(a -> recoverUser(a, request.getUserMail()))
				.orElseGet(() -> askQuestion(locale, findUser(request)));
	}

	private User findUser(InitiateRecoveryRequest request) {
		return findUser.findByEmail(request.getUserMail())
				.orElseThrow(UserRecoveryUnavailableException::new);
	}

	private ResponseEntity<String> askQuestion(Locale locale, User user) {
		RetrieveUserSecurityQuestionRequest request = buildSecurityQuestionRequest(locale, user);

		String question = securityQuestionUseCase.retrieveQuestion(request);
		return ok(question);
	}

	private ResponseEntity<String> recoverUser(String securityAnswer, Email email) {
		InitiateRecovery.BySecurityQuestionRequest recoveryRequest = InitiateRecovery.BySecurityQuestionRequest.newBuilder()
				.withEmail(email)
				.withQuestionAnswer(securityAnswer)
				.build();
		initiateRecovery.initiateBySecurityQuestion(recoveryRequest);
		return ok().build();
	}

	private RetrieveUserSecurityQuestionRequest buildSecurityQuestionRequest(Locale locale, User user) {
		return RetrieveUserSecurityQuestionRequest.builder()
				.withLocale(locale)
				.withUser(user)
				.build();
	}
}