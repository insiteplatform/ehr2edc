package eu.ehr4cr.workbench.local.conf.db;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(value = "lwbDatabaseConfig",
					   havingValue = "external")
@EnableConfigurationProperties({ExternalDataSourceSettings.class, ExternalHibernateSettings.class})
@Configuration
class ExternalConfiguration {

}
