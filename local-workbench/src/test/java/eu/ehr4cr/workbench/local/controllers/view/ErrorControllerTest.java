package eu.ehr4cr.workbench.local.controllers.view;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.AbstractMockMvcTest;

@RunWith(MockitoJUnitRunner.class)
public class ErrorControllerTest extends AbstractMockMvcTest {
	private static final String TRACKING_ID_KEY = "trackingId";
	private static final String TRACKING_ID_VALUE = "7c6abb06-4174-48d9-913f-cca465cb4313";

	@InjectMocks
	private ErrorController controller;

	@Test
	public void error() {
		ModelAndView result = given().params(buildParameters())
				.when()
				.get(WebRoutes.error)
				.then()
				.statusCode(HttpServletResponse.SC_OK)
				.extract()
				.response()
				.mvcResult()
				.getModelAndView();
		validateModel(result.getModel());
	}

	private void validateModel(Map<String, Object> model) {
		assertThat(model.get(TRACKING_ID_KEY)).as("tracking id incorrect")
				.isEqualTo(TRACKING_ID_VALUE);
	}

	private Map<String, String> buildParameters() {
		Map<String, String> map = new HashMap<>();
		map.put(TRACKING_ID_KEY, TRACKING_ID_VALUE);
		return map;
	}

	@Override
	public Object getController() {
		return controller;
	}
}
