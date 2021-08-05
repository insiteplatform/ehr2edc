package com.custodix.insite.local.user.application.command;

import static com.custodix.insite.local.user.vocabulary.AuthenticateResult.ACCOUNT_LOCKED;
import static com.custodix.insite.local.user.vocabulary.AuthenticateResult.BAD_CREDENTIALS;
import static eu.ehr4cr.workbench.local.service.DomainTime.now;

import java.util.Optional;

import org.springframework.validation.annotation.Validated;

import com.custodix.insite.local.shared.annotations.Command;
import com.custodix.insite.local.user.application.api.Authenticate;
import com.custodix.insite.local.user.domain.AuthenticationAttempt;
import com.custodix.insite.local.user.domain.AuthenticationAttemptResult;
import com.custodix.insite.local.user.domain.repository.AuthenticationAttemptRepository;
import com.custodix.insite.local.user.domain.service.AuthenticationResultMessageFactory;
import com.custodix.insite.local.user.vocabulary.AuthenticateResult;
import com.custodix.insite.local.user.vocabulary.Email;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.model.security.User;

@Validated
@Command
class AuthenticateCommand implements Authenticate {
	private final SecurityDao securityDao;
	private final AuthenticationAttemptRepository authenticationAttemptRepository;
	private final AuthenticationResultMessageFactory authenticationResultMessageFactory;

	AuthenticateCommand(SecurityDao securityDao, AuthenticationAttemptRepository authenticationAttemptRepository,
			AuthenticationResultMessageFactory authenticationResultMessageFactory) {
		this.securityDao = securityDao;
		this.authenticationAttemptRepository = authenticationAttemptRepository;
		this.authenticationResultMessageFactory = authenticationResultMessageFactory;
	}

	@Override
	public Response authenticate(Request request) {
		AuthenticateResult result = doAuthenticate(request);
		saveAuthenticationAttempt(request.getEmail(), result);
		return createResponse(request.getEmail(), result);
	}

	private AuthenticateResult doAuthenticate(Request request) {
		if (authenticationAttemptRepository.getAttempts(request.getEmail()).isAccountLocked()) {
			return ACCOUNT_LOCKED;
		} else {
			Optional<User> optionalUser = securityDao.findActiveUserByEmail(request.getEmail());
			return optionalUser.map(u -> u.authenticate(request.getPassword()))
					.orElse(BAD_CREDENTIALS);
		}
	}

	private void saveAuthenticationAttempt(Email email, AuthenticateResult result) {
		AuthenticationAttempt authenticationAttempt = AuthenticationAttempt.newBuilder()
				.withEmail(email)
				.withResult(AuthenticationAttemptResult.of(result))
				.withTimestamp(now())
				.build();
		authenticationAttemptRepository.save(authenticationAttempt);
	}

	private Response createResponse(Email email, AuthenticateResult result) {
		Optional<User> optionalUser = securityDao.findActiveUserByEmail(email);
		Response.Builder response = Response.newBuilder()
				.withResult(result)
				.withMessage(authenticationResultMessageFactory.createMessage(email, result));
		optionalUser.ifPresent(response::withUser);
		return response.build();
	}
}
