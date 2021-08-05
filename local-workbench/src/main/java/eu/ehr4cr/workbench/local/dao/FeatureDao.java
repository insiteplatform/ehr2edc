package eu.ehr4cr.workbench.local.dao;

import java.util.List;
import java.util.Optional;

import eu.ehr4cr.workbench.local.model.feature.Feature;

public interface FeatureDao {
	List<Feature> findAllFeatures();

	Optional<Feature> findFeature(String featureId);

	void persist(Feature toPersist);
}
