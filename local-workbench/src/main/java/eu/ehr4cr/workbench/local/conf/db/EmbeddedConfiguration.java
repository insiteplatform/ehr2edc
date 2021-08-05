package eu.ehr4cr.workbench.local.conf.db;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(value = "lwbDatabaseConfig",
					   havingValue = "embedded",
					   matchIfMissing = true)
@Configuration
class EmbeddedConfiguration {
	@Bean
	DataSourceSettings dataSourceSettings() {
		return new EmbeddedDataSourceSettings();
	}

	@Bean
	HibernateSettings hibernateSettings() {
		return new EmbeddedHibernateSettings();
	}
}
