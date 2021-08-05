package eu.ehr4cr.workbench.local.conf.db;

import java.util.Date;
import java.util.Properties;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;

import eu.ehr4cr.workbench.local.conf.TransactionConfiguration;

@Import(TransactionConfiguration.class)
@Configuration
public class DataSourceConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfiguration.class);
	private static final String BASELINE_VERSION = "1.7.3.4"; // NOSONAR

	@ConditionalOnProperty(value = "lwbDatabaseConfig",
						   havingValue = "embedded",
						   matchIfMissing = true)
	@Bean(name = "appDataSource",
		  initMethod = "init",
		  destroyMethod = "close")
	DataSource atomikosEmbeddedDataSourceBean(DataSourceSettings settings) {
		AtomikosNonXADataSourceBean ds = new AtomikosNonXADataSourceBean();
		ds.setUniqueResourceName(settings.getId() + "-" + new Date().getTime());
		ds.setDriverClassName(settings.getDriverClass());
		ds.setUrl(settings.getUrl());
		ds.setUser(settings.getUser());
		ds.setPassword(settings.getPassword());
		ds.setPoolSize(settings.getPoolSize());
		return ds;
	}

	@ConditionalOnProperty(value = "lwbDatabaseConfig",
						   havingValue = "external")
	@Bean(name = "appDataSource",
		  initMethod = "init",
		  destroyMethod = "close")
	DataSource atomikosDataSourceBean(DataSourceSettings settings) {
		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
		ds.setUniqueResourceName(settings.getId());
		ds.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
		Properties p = new Properties();
		p.setProperty("url", settings.getUrl());
		p.setProperty("user", settings.getUser());
		p.setProperty("password", settings.getPassword());
		ds.setXaProperties(p);
		ds.setPoolSize(settings.getPoolSize());
		return ds;
	}

	@Bean
	public Flyway flyway(Environment environment, @Qualifier("appDataSource") DataSource dataSource,
			@Value("${spring.flyway.locations:db/migration/postgresql}") String flywayLocation) {
		Flyway fly = Flyway.configure()
				.baselineVersion(BASELINE_VERSION)
				.dataSource(dataSource)
				.baselineOnMigrate(true)
				.validateOnMigrate(false)
				.table("schema_version")
				.locations(flywayLocation)
				.load();
		baselineAndMigrate(environment, fly);
		return fly;
	}

	private void baselineAndMigrate(Environment environment, Flyway fly) {
		if (environment.acceptsProfiles(Profiles.of("FLYWAY_MIGRATE"))) {
			baselineOnEmptyDB(fly);
			fly.migrate();
		}
	}

	private void baselineOnEmptyDB(Flyway fly) {
		try {
			fly.baseline();
			LOGGER.debug("Baseline succeeded");
		} catch (FlywayException e) {
			LOGGER.debug("Baseline failed, database already exists", e);
		}
	}

	@Bean
	@DependsOn("flyway")
	LocalSessionFactoryBean localSessionFactoryBean(@Qualifier("appDataSource") DataSource atomikosDataSourceBean,
			JtaTransactionManager jtaTransactionManager, HibernateSettings hibernateSettings) {
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
		localSessionFactoryBean.setDataSource(atomikosDataSourceBean);
		localSessionFactoryBean.setJtaTransactionManager(jtaTransactionManager);
		localSessionFactoryBean.setPackagesToScan("eu.ehr4cr.workbench.local",
				"com.custodix.insite.local.user.infra.jpa", "com.custodix.insite.local.recruitment.domain.model");
		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.transaction.coordinator_class", "jta");
		hibernateProperties.setProperty("hibernate.dialect", hibernateSettings.getDialect());
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", hibernateSettings.getHbm2Ddl());
		hibernateProperties.setProperty("hibernate.id.new_generator_mappings", "false");
		localSessionFactoryBean.setHibernateProperties(hibernateProperties);
		return localSessionFactoryBean;
	}
}
