package eu.ehr4cr.workbench.local.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@Configuration
@EnableOAuth2Client
public class OpenIDConnectConfig {

	@Value("${oidc.clientId}")
	private String clientId;

	@Value("${oidc.clientSecret}")
	private String clientSecret;

	@Value("${oidc.accessTokenUri}")
	private String accessTokenUri;

	@Value("${oidc.userAuthorizationUri}")
	private String userAuthorizationUri;

	@Value("${oidc.redirectUri}")
	private String redirectUri;

	@Bean
	public OAuth2ProtectedResourceDetails openId() {
		AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
		details.setClientId(clientId);
		details.setClientSecret(clientSecret);
		details.setAccessTokenUri(accessTokenUri);
		details.setUserAuthorizationUri(userAuthorizationUri);
		details.setScope(Arrays.asList("openid", "email"));
		details.setPreEstablishedRedirectUri(redirectUri);
		details.setUseCurrentUri(false);
		return details;
	}

	@Bean
	public OAuth2RestTemplate openIdConnectTemplate(OAuth2ClientContext clientContext) {
		return new OAuth2RestTemplate(openId(), clientContext);
	}

}
