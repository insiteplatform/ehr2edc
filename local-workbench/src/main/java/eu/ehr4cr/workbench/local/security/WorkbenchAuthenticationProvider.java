package eu.ehr4cr.workbench.local.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.user.application.api.Authenticate;
import com.custodix.insite.local.user.application.api.Authenticate.Request;
import com.custodix.insite.local.user.application.api.Authenticate.Response;
import com.custodix.insite.local.user.vocabulary.Email;
import com.custodix.insite.local.user.vocabulary.Password;
import com.custodix.insite.local.user.vocabulary.validation.TranslationService;

import eu.ehr4cr.workbench.local.exception.SystemException;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.model.security.User;

@Component
@Order(2)
class WorkbenchAuthenticationProvider implements AuthenticationProvider {
	private final Authenticate authenticate;
	private final TranslationService translationService;
	private final HttpServletRequest request;

	WorkbenchAuthenticationProvider(Authenticate authenticate, TranslationService translationService,
			HttpServletRequest request) {
		this.authenticate = authenticate;
		this.translationService = translationService;
		this.request = request;
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		User user = doAuthenticate(authentication);
		request.getSession()
				.setAttribute("user_id", user.getId());
		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
		return new UsernamePasswordAuthenticationToken(new SecurityContextUser(user), authentication.getCredentials(),
				getUserAuthorities(user));
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	private User doAuthenticate(Authentication authentication) {
		Response response = authenticate.authenticate(createRequest(authentication));
		switch (response.getResult()) {
		case SUCCESS:
			return response.getUser();
		case BAD_CREDENTIALS:
			throw new BadCredentialsException(translationService.translate(response.getMessage()));
		case PASSWORD_EXPIRED:
			throw new CredentialsExpiredException(translationService.translate(response.getMessage()));
		case ACCOUNT_LOCKED:
			throw new LockedException(translationService.translate(response.getMessage()));
		default:
			throw new SystemException("Unknown authentication result " + response.getResult());
		}
	}

	private Request createRequest(Authentication authentication) {
		return Request.newBuilder()
				.withEmail(Email.of(String.valueOf(authentication.getPrincipal())))
				.withPassword(Password.of(String.valueOf(authentication.getCredentials())))
				.build();
	}

	private List<GrantedAuthority> getUserAuthorities(User user) {
		return Arrays.stream(AuthorityType.values())
				.filter(user::hasAuthority)
				.map(WorkbenchGrantedAuthority::new)
				.collect(Collectors.toList());
	}
}
