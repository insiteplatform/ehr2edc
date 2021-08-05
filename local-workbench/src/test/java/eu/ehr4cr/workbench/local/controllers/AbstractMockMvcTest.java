package eu.ehr4cr.workbench.local.controllers;

import static eu.ehr4cr.workbench.local.DummySecurityContext.initDummySecurityContext;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.mockMvc;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.custodix.insite.local.shared.exceptions.handlers.UserExceptionHandlerAdvice;

import eu.ehr4cr.workbench.local.service.DomainTime;
import eu.ehr4cr.workbench.local.service.TestTimeService;

public abstract class AbstractMockMvcTest {

	@Mock
	private IndexSegmentSpring indexSegmentSpring;
	private MockMvc mvc;

	public abstract Object getController();

	@Before
	public void init() {
		DomainTime.setTime(new TestTimeService());
		initMocks(this);
		initDummySecurityContext();
		mvc = standaloneSetup(initController()).setControllerAdvice(new UserExceptionHandlerAdvice())
				.build();
		mockMvc(mvc);
	}

	private Object initController() {
		Object controller = getController();
		if (controller instanceof BaseController) {
			ReflectionTestUtils.setField(controller, "indexSegmentSpring", indexSegmentSpring);
		}
		return controller;
	}
}
