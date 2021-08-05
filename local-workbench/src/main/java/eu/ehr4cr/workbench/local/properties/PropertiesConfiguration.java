package eu.ehr4cr.workbench.local.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ FeaturesConfigurationProperties.class,
								 SupportSettingsProperties.class,
								 TerminologyConfigProperties.class})
class PropertiesConfiguration {
}