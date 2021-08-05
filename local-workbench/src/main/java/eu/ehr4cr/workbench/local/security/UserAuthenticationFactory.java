package eu.ehr4cr.workbench.local.security;

import static java.util.stream.Collectors.toList;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.custodix.insite.local.user.vocabulary.Email;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

import eu.ehr4cr.workbench.local.dao.SecurityDao;
import eu.ehr4cr.workbench.local.global.AuthorityType;
import eu.ehr4cr.workbench.local.model.security.User;

public class UserAuthenticationFactory {
	private final SecurityDao securityDao;

	public UserAuthenticationFactory(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	Authentication create(String accessToken) throws ParseException {
		User user = extractUser(accessToken);

		OAuth2User authenticatedUser = new SecurityContextUser(user);
		return new OAuth2AuthenticationToken(authenticatedUser, getUserAuthorities(user), Long.toString(user.getId()));
	}

	private User extractUser(String jwtString) throws ParseException {
		JWT jwt = JWTParser.parse(jwtString);
		String subject = jwt.getJWTClaimsSet()
				.getSubject();
		return securityDao.getActiveUserByEmail(Email.of(subject));
	}

	private List<GrantedAuthority> getUserAuthorities(User user) {
		return Arrays.stream(AuthorityType.values())
				.filter(user::hasAuthority)
				.map(WorkbenchGrantedAuthority::new)
				.collect(toList());
	}
}