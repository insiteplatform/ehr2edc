package eu.ehr4cr.workbench.local;

import java.util.ArrayList;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import com.custodix.insite.local.LocalWorkbenchTest;

import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.security.SecurityContextUser;

@LocalWorkbenchTest
public class SpringAppTestConfig {

	public MockHttpSession login(String username, String password) {
		User user = new User(username, password);
		return userMockSession(user);

	}

	public MockHttpSession login(User user) {
		return userMockSession(user);
	}

	private MockHttpSession userMockSession(User user) {
		SecurityContextUser securityContextUser = new SecurityContextUser(user);

		SecurityContext securityContext = new SecurityContextImpl();

		securityContext.setAuthentication(
				new UsernamePasswordAuthenticationToken(securityContextUser, "", new ArrayList<>()));
		SecurityContextHolder.setContext(securityContext);
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				SecurityContextHolder.getContext());
		return session;
	}

}
