package com.custodix.insite.local.cohort.scenario.objectMother;

import eu.ehr4cr.workbench.local.dao.FeatureDao;
import eu.ehr4cr.workbench.local.model.feature.Feature;

public class Features {
	private final FeatureDao featureDao;

	public Features(FeatureDao featureDao) {
		this.featureDao = featureDao;
	}

	public Feature aFeature() {
		return aFeature("feature.test.functionality", true);
	}

	public Feature aFeature(String key, boolean state) {
		Feature feature = new Feature(key, state);
		featureDao.persist(feature);
		return feature;
	}
}
