package eu.ehr4cr.workbench.local.model.feature;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import eu.ehr4cr.workbench.local.exception.FeatureNotFoundException;

public class Features {
	private final Set<Feature> featureSet;

	public Features(Set<Feature> featureSet) {
		this.featureSet = featureSet;
	}

	public Map<String, Boolean> getFeatureMapping() {
		return featureSet.stream()
				.collect(Collectors.toMap(Feature::getFeatureId, Feature::isEnabled));
	}

	public Feature getFeature(String featureKey) {
		return featureSet.stream()
				.filter(f -> f.getFeatureId()
						.equalsIgnoreCase(featureKey))
				.findFirst()
				.orElseThrow(() -> new FeatureNotFoundException(featureKey));
	}
}

