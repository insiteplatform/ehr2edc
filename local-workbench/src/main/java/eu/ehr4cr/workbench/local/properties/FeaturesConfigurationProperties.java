package eu.ehr4cr.workbench.local.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("features-configuration")
class FeaturesConfigurationProperties implements FeaturesConfiguration {
	private Map<String, Boolean> features = new HashMap<>();

	@Override
	public Map<String, Boolean> getFeatures() {
		return features;
	}

	public void setFeatures(final Map<String, Boolean> features) {
		this.features = features;
	}
}
