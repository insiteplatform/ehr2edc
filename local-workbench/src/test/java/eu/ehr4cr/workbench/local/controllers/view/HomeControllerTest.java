package eu.ehr4cr.workbench.local.controllers.view;

import static eu.ehr4cr.workbench.local.DummySecurityContext.initDummySecurityContext;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMethod;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.AbstractMockControllerTest;

public class HomeControllerTest extends AbstractMockControllerTest {

	private static final String SERVLET_CONTEXT_FIELD_NAME = "context";
	private static final String ENVIRONMENT_FIELD_NAME = "context";

	@Mock
	private Environment environment;

	@Mock
	private ServletContext servletContext;

	@InjectMocks
	private HomeController homeController;

	private MockMvc mvc;

	@Before
	public void setup() {
		initMocks(this);
		ReflectionTestUtils.setField(homeController, SERVLET_CONTEXT_FIELD_NAME, servletContext);
		Mockito.when(environment.getProperty("menubar.showInsiteStudiesTab", Boolean.class, true)).thenReturn(true);

		mvc = buildMockMVC();
		initDummySecurityContext();
	}

	@Test
	public void doGet() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = createBuilder(RequestMethod.GET, WebRoutes.home);
		doMockMvcRequestAndGetResult(mvc, requestBuilder, HttpStatus.FOUND.value());
	}

	private MockMvc buildMockMVC() {
		return MockMvcBuilders.standaloneSetup(homeController)
				.build();
	}
}
