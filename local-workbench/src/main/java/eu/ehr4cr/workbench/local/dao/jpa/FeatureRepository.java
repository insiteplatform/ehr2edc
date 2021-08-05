package eu.ehr4cr.workbench.local.dao.jpa;

import java.util.*;

import org.springframework.stereotype.Repository;

import eu.ehr4cr.workbench.local.dao.FeatureDao;
import eu.ehr4cr.workbench.local.model.feature.Feature;

@Repository
class FeatureRepository extends AbstractJpaGenericRepository implements FeatureDao {
	@Override
	public List<Feature> findAllFeatures() {
		final String query = "SELECT f FROM Feature f";
		return find(query, Collections.emptyMap());
	}

	@Override
	public Optional<Feature> findFeature(String featureId) {
		final String query = "SELECT f FROM Feature f WHERE f.featureId = :featureId";
		final Map<String, Object> params = new HashMap<>();
		params.put("featureId", featureId);
		return findSingleOptional(query, params);
	}

	@Override
	public void persist(Feature toPersist) {
		super.persist(toPersist);
	}
}
