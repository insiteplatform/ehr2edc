package eu.ehr4cr.workbench.local.usecases.feature;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import eu.ehr4cr.workbench.local.dao.FeatureDao;
import eu.ehr4cr.workbench.local.model.feature.Feature;
import eu.ehr4cr.workbench.local.model.feature.Features;
import eu.ehr4cr.workbench.local.properties.FeaturesConfiguration;
import eu.ehr4cr.workbench.local.usecases.UseCase;

@UseCase
class GetFeaturesUseCase implements GetFeatures {
	private final FeatureDao featureDao;
	private final FeaturesConfiguration featuresConfigurationProperties;

	GetFeaturesUseCase(FeatureDao featureDao, final FeaturesConfiguration featuresConfigurationProperties) {
		this.featureDao = featureDao;
		this.featuresConfigurationProperties = featuresConfigurationProperties;
	}

	@Override
	public Response getFeatures() {
		Set<Feature> featureSet = new HashSet<>();
		featureSet.addAll(getFeaturesFromProperties());
		featureSet.addAll(getFeaturesFromDao());
		return Response.newBuilder()
				.withFeatures(new Features(featureSet))
				.build();
	}

	private List<Feature> getFeaturesFromProperties() {
		return featuresConfigurationProperties.getFeatures()
				.entrySet()
				.stream()
				.map(f -> new Feature(f.getKey(), f.getValue()))
				.collect(Collectors.toList());
	}

	private List<Feature> getFeaturesFromDao() {
		return featureDao.findAllFeatures();
	}

}