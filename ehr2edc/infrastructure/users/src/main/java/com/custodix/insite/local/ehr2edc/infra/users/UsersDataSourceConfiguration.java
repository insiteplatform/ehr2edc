package com.custodix.insite.local.ehr2edc.infra.users;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilderCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@EnableConfigurationProperties(UsersDatasourceSettings.class)
@EnableJpaRepositories(basePackages = "com.custodix.insite.local.ehr2edc.infra.users.repository",
					   entityManagerFactoryRef = "usersEntityFactoryBean")
class UsersDataSourceConfiguration {
	@Bean("usersDataSource")
	public DataSource dataSource(UsersDatasourceSettings usersDatasourceSettings) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName(usersDatasourceSettings.getDriverClassName());
		dataSource.setUrl(usersDatasourceSettings.getUrl());
		dataSource.setUsername(usersDatasourceSettings.getUsername());
		dataSource.setPassword(usersDatasourceSettings.getPassword());

		return dataSource;
	}

	@Bean
	@ConditionalOnMissingBean
	public JpaVendorAdapter jpaVendorAdapter(JpaProperties properties,
			@Qualifier("usersDataSource") DataSource usersDataSource) {
		AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setShowSql(properties.isShowSql());
		adapter.setDatabase(properties.determineDatabase(usersDataSource));
		adapter.setDatabasePlatform(properties.getDatabasePlatform());
		adapter.setGenerateDdl(properties.isGenerateDdl());
		return adapter;
	}

	@Bean
	@ConditionalOnMissingBean
	public EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaVendorAdapter jpaVendorAdapter,
			ObjectProvider<PersistenceUnitManager> persistenceUnitManager,
			ObjectProvider<EntityManagerFactoryBuilderCustomizer> customizers) {
		EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(jpaVendorAdapter, Collections.emptyMap(),
				persistenceUnitManager.getIfAvailable());
		customizers.orderedStream()
				.forEach(customizer -> customizer.customize(builder));
		return builder;
	}

	@PersistenceContext(unitName = "users")
	@Bean(name = "usersEntityFactoryBean")
	public LocalContainerEntityManagerFactoryBean usersEntityFactoryBean(EntityManagerFactoryBuilder builder,
			@Qualifier("usersDataSource") DataSource usersDataSource) {
		Map<String, String> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto",
				EmbeddedDatabaseConnection.isEmbedded(usersDataSource) ? "create-drop" : "none");
		return builder.dataSource(usersDataSource)
				.persistenceUnit("users")
				.packages("com.custodix.insite.local.ehr2edc.infra.users")
				.properties(properties)
				.build();
	}

}