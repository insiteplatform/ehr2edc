package eu.ehr4cr.workbench.local.controllers.api.feature;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.ehr4cr.workbench.local.WebRoutes;
import eu.ehr4cr.workbench.local.usecases.feature.GetFeatures;
import eu.ehr4cr.workbench.local.usecases.feature.GetFeatures.Response;

@RestController
class FeaturesController {
	private final GetFeatures getFeatures;
	private final ObjectMapper objectMapper;

	FeaturesController(GetFeatures getFeatures, ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		this.getFeatures = getFeatures;
	}

	@GetMapping(value = WebRoutes.features,
				produces = "text/javascript")
	public String getFeatures() throws JsonProcessingException {
		Response response = getFeatures.getFeatures();
		Map<String, Boolean> featureMapping = response.getFeatures().getFeatureMapping();
		return "var features = " + objectMapper.writeValueAsString(featureMapping);
	}
}

