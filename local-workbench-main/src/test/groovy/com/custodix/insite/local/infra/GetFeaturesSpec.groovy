package com.custodix.insite.local.infra

import com.custodix.insite.local.user.AbstractSpecification
import eu.ehr4cr.workbench.local.model.feature.Feature
import eu.ehr4cr.workbench.local.usecases.feature.GetFeatures
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title

import static eu.ehr4cr.workbench.local.usecases.feature.GetFeatures.Response

@Title("View application features availability")
class GetFeaturesSpec extends AbstractSpecification {
    private static final String CONFIGURED_FEATURE_KEY = "feature.test.getfeatures";

    @Autowired
    private GetFeatures getFeatures

    def "I can view the availability of application features"() {
        given: "A configured feature"
        and: "A persisted feature"
        Feature persistedFeature = features.aFeature()

        when: "I view the availability of application features"
        Response response = getFeatures.getFeatures()

        then: "The configured features are returned"
        def features = response.features
        features.getFeature(CONFIGURED_FEATURE_KEY).enabled

        and: "The persisted features are returned"
        features.getFeature(persistedFeature.featureId).enabled
    }

    def "Configured feature state has priority over persisted feature state"() {
        given: "A configured feature which is enabled"
        and: "A persisted feature with the same key which is disabled"
        features.aFeature(CONFIGURED_FEATURE_KEY, false)

        when: "I view the availability of application features"
        Response response = getFeatures.getFeatures()

        then: "The feature is returned with state enabled"
        def features = response.features
        features.getFeature(CONFIGURED_FEATURE_KEY).enabled
    }
}
