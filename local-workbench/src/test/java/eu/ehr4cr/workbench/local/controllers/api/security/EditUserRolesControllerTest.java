package eu.ehr4cr.workbench.local.controllers.api.security;

import static eu.ehr4cr.workbench.local.model.security.UserRole.ADM;
import static eu.ehr4cr.workbench.local.model.security.UserRole.DRM;
import static eu.ehr4cr.workbench.local.model.security.UserRole.PWR;
import static eu.ehr4cr.workbench.local.FileLoader.loadFileContent;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.custodix.insite.local.user.EditUserRoles;
import com.custodix.insite.local.user.EditUserRoles.Request;
import eu.ehr4cr.workbench.local.model.security.UserRole;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.AbstractMockMvcTest;
import io.restassured.http.ContentType;

public class EditUserRolesControllerTest extends AbstractMockMvcTest {
	private static final String REQUEST_DATA_JSON_PATH = "samples/security-assign-user-to-group.json";
	private static final long USER_ID = 1L;
	private static final List<UserRole> USER_ROLES = Arrays.asList(ADM, DRM, PWR);

	@Mock
	private EditUserRoles editUserRoles;
	@InjectMocks
	private EditUserRolesController editUserRolesController;

	@Test
	public void doPost() {
		given().contentType(ContentType.JSON)
				.body(loadFileContent(REQUEST_DATA_JSON_PATH))
				.when()
				.post(WebRoutes.assignMembers)
				.then()
				.statusCode(HttpServletResponse.SC_OK);
		verify(editUserRoles).editRoles(matchRequest());
	}

	private Request matchRequest() {
		return argThat(r -> USER_ID == r.getUserId() && USER_ROLES.equals(r.getUserRoles()));
	}

	@Override
	public Object getController() {
		return editUserRolesController;
	}
}
