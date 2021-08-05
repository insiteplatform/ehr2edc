package com.custodix.insite.local.ehr2edc.security.oidc;

import java.text.ParseException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.custodix.insite.local.ehr2edc.query.security.GetUser;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

class EHR2EDCUserAuthenticationFactory {
	private final GetUser getUser;

	EHR2EDCUserAuthenticationFactory(GetUser getUser) {
		this.getUser = getUser;
	}

	Authentication create(String accessToken) throws ParseException {
		GetUser.User user = extractUser(accessToken);

		OAuth2User authenticatedUser = new SecurityContextUser(user);
		return new OAuth2AuthenticationToken(authenticatedUser, authenticatedUser.getAuthorities(),
				user.getUserIdentifier()
						.getId());
	}

	private GetUser.User extractUser(String jwtString) throws ParseException {
		JWT jwt = JWTParser.parse(jwtString);
		String subject = jwt.getJWTClaimsSet()
				.getStringClaim("username");
		return getUser.getUser(GetUser.Request.newBuilder()
				.withUserIdentifier(UserIdentifier.of(subject))
				.build())
				.getUser();
	}

}