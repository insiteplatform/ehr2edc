package eu.ehr4cr.workbench.local;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.Locale;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import com.custodix.insite.local.LocalWorkbenchTest;

import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.security.SecurityContextUser;
import eu.ehr4cr.workbench.local.service.DomainTime;
import eu.ehr4cr.workbench.local.service.IUserMgrService;
import eu.ehr4cr.workbench.local.service.TestTimeService;

@RunWith(SpringRunner.class)
@LocalWorkbenchTest
public abstract class AbstractWorkbenchTest {

	@Autowired
	private IUserMgrService userService;

	static {
		DomainTime.setTime(new TestTimeService());
	}

	@Before
	public void initLocale() {
		Locale.setDefault(Locale.ENGLISH);
	}

	public void login(String username, String password) {
		User user = new User(username, password);
		login(user);
	}

	public void login(User user) {
		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
		SecurityContextUser securityContextUser = new SecurityContextUser(user);
		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken(securityContextUser, "", new ArrayList<>()));
	}

	public void logout() {
		SecurityContextHolder.clearContext();
	}

}
