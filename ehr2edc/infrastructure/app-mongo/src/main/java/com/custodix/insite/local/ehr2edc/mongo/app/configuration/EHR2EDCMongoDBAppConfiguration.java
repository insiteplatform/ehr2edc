package com.custodix.insite.local.ehr2edc.mongo.app.configuration;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.List;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.convert.MappingContextTypeInformationMapper;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.PlatformTransactionManager;

import com.custodix.insite.local.ehr2edc.RegistrationRecord;
import com.custodix.insite.local.ehr2edc.mongo.app.audit.PopulationContextDocument;
import com.custodix.insite.local.ehr2edc.mongo.app.audit.ReviewContextDocument;
import com.custodix.insite.local.ehr2edc.mongo.app.document.EDCConnectionDocument;
import com.custodix.insite.local.ehr2edc.mongo.app.document.EventMongoDocument;
import com.custodix.insite.local.ehr2edc.mongo.app.document.StudyDocument;
import com.custodix.insite.local.ehr2edc.mongo.app.jackson.AppMongoJacksonConfiguration;
import com.custodix.insite.local.ehr2edc.mongo.app.querymapping.PersistedItemQueryMapping;
import com.custodix.insite.local.ehr2edc.mongo.app.study.InstantReadConverter;
import com.custodix.insite.local.ehr2edc.mongo.app.study.InstantWriteConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cloudyrock.mongock.SpringMongock;
import com.github.cloudyrock.mongock.SpringMongockBuilder;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

@Configuration
@EnableConfigurationProperties(EHR2EDCMongoDBAppConfigurationProperties.class)
@EnableMongoRepositories(basePackages = "com.custodix.insite.local.ehr2edc.mongo.app",
						 mongoTemplateRef = "ehr2edcMongoAppMongoTemplate",
						 considerNestedRepositories = true)
@ComponentScan(basePackages = "com.custodix.insite.local.ehr2edc.mongo.app")
public class EHR2EDCMongoDBAppConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(EHR2EDCMongoDBAppConfiguration.class);
	private static final String DOT_REPLACEMENT = "(dot)";

	private final EHR2EDCMongoDBAppConfigurationProperties properties;
	private final MongoClientOptions options;
	private final MongoClientFactory factory;
	private final ApplicationContext applicationContext;

	private MongoClient mongo;

	public EHR2EDCMongoDBAppConfiguration(EHR2EDCMongoDBAppConfigurationProperties properties,
			ObjectProvider<MongoClientOptions> options, Environment environment,
			ApplicationContext applicationContext) {
		this.properties = properties;
		this.options = options.getIfAvailable();
		this.applicationContext = applicationContext;
		this.factory = new MongoClientFactory(properties.getMongoDbApp(), environment);
	}

	@PreDestroy
	public void close() {
		if (this.mongo != null) {
			this.mongo.close();
		}
	}

	@Bean(name = "ehr2edcMongoAppMongoTransactionManager")
	PlatformTransactionManager transactionManager(
			@Qualifier("ehr2edcMongoAppMongoDbFactory") MongoDbFactory mongoDbFactory,
			@Value("${ehr2edc.mongo.transactions:false}") boolean enableTransactions) {
		if (enableTransactions) {
			return new MongoTransactionManager(mongoDbFactory);
		}
		return new DummyMongoTransactionManager();
	}

	@Bean(name = "ehr2edcMongoAppMongoDbFactory")
	public SimpleMongoDbFactory mongoDbFactory(@Qualifier("ehr2edcMongoAppMongoClient") MongoClient mongo) {
		String database = this.properties.getMongoDbApp()
				.getMongoClientDatabase();
		return new SimpleMongoDbFactory(mongo, database);
	}

	@Bean(name = "ehr2edcMongoAppMongoTemplate")
	public MongoTemplate mongoTemplate(@Qualifier("ehr2edcMongoAppMongoDbFactory") MongoDbFactory mongoDbFactory,
			@Qualifier("ehr2edcMongoAppMappingMongoConverter") MongoConverter mappingMongoConverter) {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, mappingMongoConverter);

		initializeCollections(mongoTemplate);

		return mongoTemplate;
	}

	private void initializeCollections(MongoTemplate mongoTemplate) {
		List<Class<?>> documents = asList(EventMongoDocument.class, RegistrationRecord.class, StudyDocument.class,
				PersistedItemQueryMapping.class, ReviewContextDocument.class, PopulationContextDocument.class,
				EDCConnectionDocument.class);

		documents.forEach(c -> {
			try {
				mongoTemplate.createCollection(c);
			} catch (Exception e) {
				LOGGER.trace("Could not create collections, possibly because they already exist", e);
			}
		});
	}

	@Bean(name = "ehr2edcMongoAppMongoClient")
	public MongoClient mongoClient() {
		this.mongo = this.factory.createMongoClient(this.options);
		return this.mongo;
	}

	@Bean
	public MappingMongoConverter ehr2edcMongoAppMappingMongoConverter(
			@Qualifier("ehr2edcMongoAppMongoDbFactory") MongoDbFactory factory,
			@Qualifier("ehr2edcMongoAppMappingContext") MongoMappingContext context,
			@Qualifier("ehr2edcMongoAppCustomConversions") MongoCustomConversions conversions) {
		DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
		MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
		mappingConverter.setCustomConversions(conversions);
		mappingConverter.setTypeMapper(new CustomMongoTypeMapper(new EntityScanningTypeInformationMapper(
				Collections.singletonList("com.custodix.insite.local.ehr2edc.mongo.app")),
				new MappingContextTypeInformationMapper(context)));
		return mappingConverter;
	}

	@Bean
	public MongoMappingContext ehr2edcMongoAppMappingContext(
			@Qualifier("ehr2edcMongoAppCustomConversions") MongoCustomConversions conversions)
			throws ClassNotFoundException {
		MongoMappingContext context = new MongoMappingContext();
		context.setInitialEntitySet(new EntityScanner(this.applicationContext).scan(Document.class, Persistent.class));
		Class<?> strategyClass = this.properties.getMongoDbApp()
				.getFieldNamingStrategy();
		if (strategyClass != null) {
			context.setFieldNamingStrategy((FieldNamingStrategy) BeanUtils.instantiateClass(strategyClass));
		}
		context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
		return context;
	}

	@Bean
	public MongoCustomConversions ehr2edcMongoAppCustomConversions() {
		final ObjectMapper objectMapper = AppMongoJacksonConfiguration.createObjectMapper();
		return new MongoCustomConversions(Lists.newArrayList(new InstantReadConverter(), new InstantWriteConverter(),
				new ItemQueryMappingReadConverter(objectMapper, DOT_REPLACEMENT),
				new ItemQueryMappingWriterConverter(objectMapper, DOT_REPLACEMENT)));
	}

	@Bean
	public SpringMongock mongock(Environment environment,
			@Qualifier("ehr2edcMongoAppMongoClient") MongoClient mongoclient) {
		return new SpringMongockBuilder(mongoclient, this.properties.getMongoDbApp()
				.getMongoClientDatabase(),
				"com.custodix.insite.local.ehr2edc.mongo.app.migration").setSpringEnvironment(environment)
				.setLockQuickConfig()
				.setEnabled(true)
				.build();
	}

}
