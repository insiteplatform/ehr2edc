package eu.ehr4cr.workbench.local.security;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;

import eu.ehr4cr.workbench.local.security.rsa.RsaKeyResolver;

public class OpenIDConnectFilter extends AbstractAuthenticationProcessingFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(OpenIDConnectFilter.class);

	private final OAuth2RestOperations restTemplate;
	private final RsaKeyResolver rsaKeyResolver;
	private final UserAuthenticationFactory userAuthenticationFactory;

	OpenIDConnectFilter(String defaultFilterProcessesUrl, OAuth2RestTemplate restTemplate,
			UserAuthenticationFactory userAuthenticationFactory, RsaKeyResolver rsaKeyResolver) {
		super(defaultFilterProcessesUrl);
		this.restTemplate = restTemplate;
		this.rsaKeyResolver = rsaKeyResolver;
		this.userAuthenticationFactory = userAuthenticationFactory;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		try {
			return authenticate();
		} catch (InvalidTokenException | ParseException | JOSEException e) {
			throw new BadCredentialsException("Could not obtain user details from token", e);
		} catch (OAuth2Exception e) {
			throw new BadCredentialsException("Could not obtain access token", e);
		}
	}

	private Authentication authenticate() throws ParseException, JOSEException {
		OAuth2AccessToken accessToken = restTemplate.getAccessToken();
		verifyToken(accessToken);
		return getAuthenticationToken(accessToken);
	}

	private void verifyToken(OAuth2AccessToken accessToken) throws ParseException, JOSEException {
		SignedJWT signedJWT = SignedJWT.parse(accessToken.getValue());
		RSAKey rsaKey = getRsaKey(signedJWT);
		if (!signedJWT.verify(new RSASSAVerifier(rsaKey))) {
			throw new BadCredentialsException("Invalid token received");
		}
	}

	private RSAKey getRsaKey(SignedJWT signedJWT) {
		String keyId = signedJWT.getHeader()
				.getKeyID();
		System.out.println(signedJWT.getHeader());
		System.out.println(signedJWT.getHeader().getKeyID());
		return rsaKeyResolver.resolve(keyId);
	}

	private Authentication getAuthenticationToken(OAuth2AccessToken accessToken) throws ParseException {
		String webToken = getWebToken(accessToken);
		logAccessToken(accessToken);
		return userAuthenticationFactory.create(webToken);
	}

	private String getWebToken(OAuth2AccessToken accessToken) {
		return accessToken.getAdditionalInformation()
				.get("id_token")
				.toString();
	}

	private void logAccessToken(OAuth2AccessToken accessToken) {
		LOGGER.debug("Received access token: {}", accessToken);
	}
}