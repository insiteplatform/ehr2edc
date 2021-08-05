package eu.ehr4cr.workbench.local.controllers.api.feature;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.controllers.AbstractMockMvcTest;
import eu.ehr4cr.workbench.local.model.feature.Feature;
import eu.ehr4cr.workbench.local.model.feature.Features;
import eu.ehr4cr.workbench.local.usecases.feature.GetFeatures;
import eu.ehr4cr.workbench.local.usecases.feature.GetFeatures.Response;

public class FeaturesControllerTest extends AbstractMockMvcTest {
	@Mock
	private GetFeatures getFeatures;
	@Spy
	private ObjectMapper objectMapper;
	@InjectMocks
	private FeaturesController featuresController;

	@Before
	public void initUseCase() {
		when(getFeatures.getFeatures()).thenReturn(createResponse());
	}

	@Test
	public void getFeatures() {
		String result = given().when()
				.get(WebRoutes.features)
				.then()
				.contentType("text/javascript")
				.statusCode(HttpServletResponse.SC_OK)
				.extract()
				.response()
				.asString();
		assertThat(result).isEqualTo("var features = {\"feature.x.b\":false,\"feature.x.a\":true,\"feature.y\":true}");
	}

	private Response createResponse() {
		return Response.newBuilder()
				.withFeatures(createFeatures())
				.build();
	}

	private Features createFeatures() {
		Set<Feature> featureList = new HashSet<>();
		featureList.add(new Feature("feature.x.a", true));
		featureList.add(new Feature("feature.x.b", false));
		featureList.add(new Feature("feature.y", true));
		return new Features(featureList);
	}

	@Override
	public Object getController() {
		return featuresController;
	}
}