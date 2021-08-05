package eu.ehr4cr.workbench.local.controllers.api.security;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import eu.ehr4cr.workbench.local.FileLoader;
import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.AbstractMockMvcTest;
import eu.ehr4cr.workbench.local.usecases.user.register.RegisterUser;
import eu.ehr4cr.workbench.local.usecases.user.register.RegisterUser.Request;
import io.restassured.http.ContentType;

public class RegisterUserControllerTest extends AbstractMockMvcTest {
	private static final String JSON_SECURITY_REGISTER_REQUEST = "samples/security-register-request.json";

	@Mock
	private RegisterUser registerUser;
	@Captor
	private ArgumentCaptor<Request> registerUserRequest;
	@InjectMocks
	private RegisterUserController registerUserController;

	private static final String TEST_VALID_EMAIL = "user@example.com";
	private static final String TEST_VALID_USERNAME = "user123";

	@Test
	public void doPost() {
		given().body(getRequest())
				.contentType(ContentType.JSON)
				.when()
				.post(WebRoutes.registerAccount)
				.then()
				.statusCode(HttpServletResponse.SC_OK);
		validateUsecaseCall();
	}

	private void validateUsecaseCall() {
		verify(registerUser).register(registerUserRequest.capture());
		Request request = registerUserRequest.getValue();
		assertThat(request.getUserName()).isEqualTo(TEST_VALID_USERNAME);
		assertThat(request.getUserEmail()).isEqualTo(TEST_VALID_EMAIL);
	}

	private String getRequest() {
		return FileLoader.loadFileContent(JSON_SECURITY_REGISTER_REQUEST);
	}

	@Override
	public Object getController() {
		return registerUserController;
	}
}
