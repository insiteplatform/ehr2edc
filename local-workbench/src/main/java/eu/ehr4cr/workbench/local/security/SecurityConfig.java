package eu.ehr4cr.workbench.local.security;

import static eu.ehr4cr.workbench.local.WebRoutes.oidcLogin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.security.rsa.RsaKeyResolver;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,
							securedEnabled = true)
class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final CustomPermissionEvaluator customPermissionEvaluator;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final List<AuthenticationProvider> authenticationProviders;

	@Value("${oidc.enabled}")
	private boolean oidcEnabled;

	@Value("${oidc.logoutUri}")
	private String oidcLogoutUrl;

	@Value("${oidc.keystore.location}")
	private Resource keystore;

	@Autowired
	private OAuth2RestTemplate restTemplate;

	@Autowired
	private SecurityDao securityDao;

	@Autowired
	private ObjectMapper objectMapper;

	SecurityConfig(final List<AuthenticationProvider> authenticationProviders,
			final CustomPermissionEvaluator customPermissionEvaluator,
			final CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
		this.authenticationProviders = authenticationProviders;
		this.customPermissionEvaluator = customPermissionEvaluator;
		this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		if (this.oidcEnabled) {
			http.addFilterAfter(new OAuth2ClientContextFilter(), AbstractPreAuthenticatedProcessingFilter.class)
					.addFilterAfter(openIdConnectFilter(), OAuth2ClientContextFilter.class)
					.httpBasic()
					.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(oidcLogin))
					.and()
					.authorizeRequests()
					.antMatchers("/app/**")
					.authenticated()
					.antMatchers("/**")
					.permitAll()
					.and()
					.authorizeRequests()
					.anyRequest()
					.authenticated()
					.and()
					.csrf()
					.ignoringAntMatchers(WebRoutes.hawtioActuator +"/**", WebRoutes.login)
					.and()
					.logout()
					.logoutSuccessUrl(oidcLogoutUrl)
					.logoutRequestMatcher(new AntPathRequestMatcher(WebRoutes.logout));
		} else {
			http.authorizeRequests()
					.requestMatchers(EndpointRequest.to("info", "health"))
					.permitAll()
					.requestMatchers(EndpointRequest.toAnyEndpoint())
					.hasRole(AuthorityType.VIEW_ACTUATOR_DETAILS.name())
					.antMatchers("/app/**")
					.authenticated()
					.antMatchers("/api/**")
					.authenticated()
					.antMatchers("/**")
					.permitAll()
					.and()
					.exceptionHandling()
					.authenticationEntryPoint(customAuthenticationEntryPoint)
					.and()
					.httpBasic()
					.and()
					.formLogin()
					.loginPage(WebRoutes.login)
					.failureUrl(WebRoutes.login)
					.permitAll()
					.and()
					.csrf()
					.ignoringAntMatchers(WebRoutes.hawtioActuator +"/**", WebRoutes.login)
					.and()
					.logout()
					.logoutRequestMatcher(new AntPathRequestMatcher(WebRoutes.logout));
		}
	}

	public OpenIDConnectFilter openIdConnectFilter() {
		OpenIDConnectFilter filter = new OpenIDConnectFilter(oidcLogin, restTemplate, userAuthenticationFactory(),
				rsaKeyResolver());
		filter.setContinueChainBeforeSuccessfulAuthentication(false);
		filter.setAuthenticationManager(new NoopAuthenticationManager());
		return filter;
	}

	private RsaKeyResolver rsaKeyResolver() {
		return new RsaKeyResolver(objectMapper, keystore);
	}

	private UserAuthenticationFactory userAuthenticationFactory() {
		return new UserAuthenticationFactory(securityDao);
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) {
		authenticationProviders.forEach(auth::authenticationProvider);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		DefaultWebSecurityExpressionHandler webSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
		webSecurityExpressionHandler.setPermissionEvaluator(customPermissionEvaluator);
		web.expressionHandler(webSecurityExpressionHandler);
		super.configure(web);
	}

	private static class NoopAuthenticationManager implements AuthenticationManager {
		@Override
		public Authentication authenticate(Authentication authentication) {
			throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
		}
	}

}