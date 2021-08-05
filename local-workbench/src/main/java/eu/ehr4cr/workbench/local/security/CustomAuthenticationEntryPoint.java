package eu.ehr4cr.workbench.local.security;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.security.apiChecker.ApiChecker;
import eu.ehr4cr.workbench.local.security.apiChecker.ApiCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private final AuthenticationEntryPoint http401AuthenticationEntryPoint;
	private final AuthenticationEntryPoint loginUrlAuthenticationEntryPoint;
	private final ApiCheckerFactory apiCheckerFactory;

	@Autowired
	public CustomAuthenticationEntryPoint(final ApiCheckerFactory apiCheckerFactory) {
		this.apiCheckerFactory = apiCheckerFactory;
		this.http401AuthenticationEntryPoint = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
		this.loginUrlAuthenticationEntryPoint = new LoginUrlAuthenticationEntryPoint(WebRoutes.login);
	}

	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
						 AuthenticationException e) throws IOException, ServletException {
		if (isApiCall(httpServletRequest)) {
			http401AuthenticationEntryPoint.commence(httpServletRequest, httpServletResponse, e);
		} else {
			loginUrlAuthenticationEntryPoint.commence(httpServletRequest, httpServletResponse, e);
		}
	}

	private boolean isApiCall(HttpServletRequest req) {
		Optional<? extends ApiChecker> apiCheckerOptional = apiCheckerFactory.create(req);

		return apiCheckerOptional.map(apiChecker -> apiChecker.isApiCall()).orElse(false);
	}
}
