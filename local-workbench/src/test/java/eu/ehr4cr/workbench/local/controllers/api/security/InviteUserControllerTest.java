package eu.ehr4cr.workbench.local.controllers.api.security;

import static eu.ehr4cr.workbench.local.FileLoader.loadFileContent;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.custodix.insite.local.user.InviteUser;
import com.custodix.insite.local.user.InviteUser.Request;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.AbstractMockMvcTest;
import eu.ehr4cr.workbench.local.model.security.UserRole;
import io.restassured.http.ContentType;

public class InviteUserControllerTest extends AbstractMockMvcTest {
	private static final String REQUEST_DATA_JSON_PATH = "samples/security-invite-user.json";
	private static final String NAME = "foobar";
	private static final String EMAIL = "test@example.com";
	private static final List<UserRole> ROLES = Collections.singletonList(UserRole.PWR);

	@Mock
	private InviteUser inviteUser;
	@InjectMocks
	private InviteUserController inviteUserController;

	@Test
	public void doPost() {
		given().contentType(ContentType.JSON)
				.body(loadFileContent(REQUEST_DATA_JSON_PATH))
				.when()
				.post(WebRoutes.inviteUser)
				.then()
				.statusCode(HttpServletResponse.SC_OK);
		verify(inviteUser).invite(matchRequest());
	}

	private Request matchRequest() {
		return argThat(r -> NAME.equals(r.getUsername()) && EMAIL.equals(r.getEmail()) && ROLES.equals(r.getRoles()));
	}

	@Override
	public Object getController() {
		return inviteUserController;
	}

}
