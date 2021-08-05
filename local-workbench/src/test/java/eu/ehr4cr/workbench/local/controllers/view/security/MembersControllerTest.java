package eu.ehr4cr.workbench.local.controllers.view.security;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.servlet.ModelAndView;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.AbstractMockMvcTest;
import eu.ehr4cr.workbench.local.model.security.UserRole;
import eu.ehr4cr.workbench.local.usecases.user.GetDetailedUsers;
import eu.ehr4cr.workbench.local.usecases.user.GetDetailedUsers.Response;
import eu.ehr4cr.workbench.local.usecases.user.GetDetailedUsers.Response.UserInfo;

public class MembersControllerTest extends AbstractMockMvcTest {
	private static final ArrayList<UserInfo> USERS = new ArrayList<>();

	@Mock
	private GetDetailedUsers getDetailedUsers;

	@InjectMocks
	private MembersController membersController;

	@Before
	public void mockUsecases() {
		when(getDetailedUsers.getUsers()).thenReturn(createUsersResponse());
	}

	@Test
	public void doGetOk() {
		ModelAndView modelAndView = given().when()
				.get(WebRoutes.manageMembers)
				.then()
				.statusCode(HttpServletResponse.SC_OK)
				.extract()
				.response()
				.mvcResult()
				.getModelAndView();
		validateModel(modelAndView.getModel());
	}

	private void validateModel(Map<String, Object> model) {
		assertThat(model.get("users")).isEqualTo(USERS);
		assertThat(model.get("roles")).isEqualTo(UserRole.values());
	}

	private Response createUsersResponse() {
		return Response.newBuilder()
				.withUsers(USERS)
				.build();
	}

	@Override
	public Object getController() {
		return membersController;
	}
}
