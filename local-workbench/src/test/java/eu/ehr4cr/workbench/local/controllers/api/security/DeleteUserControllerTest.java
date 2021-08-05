package eu.ehr4cr.workbench.local.controllers.api.security;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import eu.ehr4cr.workbench.local.controllers.api.security.DeleteUserController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.AbstractMockControllerTest;
import eu.ehr4cr.workbench.local.service.IUserMgrService;

public class DeleteUserControllerTest extends AbstractMockControllerTest {

	@Mock
	private IUserMgrService userMgrService;

	@InjectMocks
	private DeleteUserController deleteUserController;

	private MockMvc mvc;

	@Before
	public void setup() {
		initMocks(this);
		mvc = buildMockMVC();
	}

	@Test
	public void doPost() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = createBuilder(RequestMethod.POST, WebRoutes.deleteMember);
		requestBuilder.contentType(MediaType.APPLICATION_JSON)
				.param("userId", "1");
		doMockMvcRequestAndGetResult(mvc, requestBuilder, HttpStatus.OK.value());
		verify(userMgrService).deleteUser(eq(1L));
	}

	private MockMvc buildMockMVC() {
		return MockMvcBuilders.standaloneSetup(deleteUserController)
				.build();
	}

}
