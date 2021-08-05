package com.custodix.insite.local.ehr2edc.security.oidc;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.filter.ForwardedHeaderFilter;

import com.custodix.insite.local.ehr2edc.query.security.GetUser;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableOAuth2Client
@ConditionalOnProperty("ehr2edc.oidc.enabled")
@EnableConfigurationProperties(EHR2EDCOpenIDConnectProperties.class)
class EHR2EDCOpenIDConnectConfig {

	@Bean
	public OAuth2ProtectedResourceDetails openId(EHR2EDCOpenIDConnectProperties ehr2EDCOpenIDConnectProperties) {
		AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
		details.setClientId(ehr2EDCOpenIDConnectProperties.getClientId());
		details.setClientSecret(ehr2EDCOpenIDConnectProperties.getClientSecret());
		details.setAccessTokenUri(ehr2EDCOpenIDConnectProperties.getAccessTokenUri());
		details.setUserAuthorizationUri(ehr2EDCOpenIDConnectProperties.getUserAuthorizationUri());
		details.setScope(Arrays.asList("openid", "email", "username"));
		details.setPreEstablishedRedirectUri(ehr2EDCOpenIDConnectProperties.getRedirectUri());
		details.setUseCurrentUri(false);
		return details;
	}

	@Bean
	FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {

		final FilterRegistrationBean<ForwardedHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<ForwardedHeaderFilter>();

		filterRegistrationBean.setFilter(new ForwardedHeaderFilter());
		filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

		return filterRegistrationBean;
	}

	@Bean
	public OAuth2RestTemplate openIdConnectTemplate(OAuth2ClientContext clientContext,
			OAuth2ProtectedResourceDetails openId) {
		return new OAuth2RestTemplate(openId, clientContext);
	}

	@Bean
	public EHR2EDCOpenIDConnectFilter openIdConnectFilter(OAuth2RestTemplate oAuth2RestTemplate, GetUser getUser,
			ObjectMapper objectMapper, EHR2EDCOpenIDConnectProperties ehr2EDCOpenIDConnectProperties) {
		EHR2EDCOpenIDConnectFilter filter = new EHR2EDCOpenIDConnectFilter(ehr2EDCOpenIDConnectProperties.getLoginUri(),
				oAuth2RestTemplate, userAuthenticationFactory(getUser),
				rsaKeyResolver(objectMapper, ehr2EDCOpenIDConnectProperties));
		filter.setContinueChainBeforeSuccessfulAuthentication(false);
		filter.setAuthenticationManager(new NoopAuthenticationManager());
		return filter;
	}

	private EHR2EDCRsaKeyResolver rsaKeyResolver(ObjectMapper objectMapper,
			EHR2EDCOpenIDConnectProperties ehr2EDCOpenIDConnectProperties) {
		return new EHR2EDCRsaKeyResolver(objectMapper, ehr2EDCOpenIDConnectProperties.getKeystore());
	}

	private EHR2EDCUserAuthenticationFactory userAuthenticationFactory(GetUser getUser) {
		return new EHR2EDCUserAuthenticationFactory(getUser);
	}

	private static class NoopAuthenticationManager implements AuthenticationManager {
		@Override
		public Authentication authenticate(Authentication authentication) {
			throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
		}
	}
}
