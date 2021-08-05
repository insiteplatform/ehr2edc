package com.custodix.insite.local.ehr2edc.query.fhir.mongo.configuration;

import java.util.Arrays;

import javax.annotation.PreDestroy;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.custodix.insite.local.ehr2edc.query.fhir.mongo.repository.StudyIdReaderConverter;
import com.custodix.insite.local.ehr2edc.query.fhir.mongo.repository.StudyIdWriterConverter;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

@Configuration
@EnableConfigurationProperties(Ehr2EdcFhirDbProperties.class)
@EnableMongoRepositories(basePackages = "com.custodix.insite.local.ehr2edc.query.fhir.mongo.repository",
						 mongoTemplateRef = "ehr2edcFhirMongoTemplate",
						 considerNestedRepositories = true
)
public class Ehr2EdcFhirDbConfiguration {

	private final ApplicationContext applicationContext;
	private final Ehr2EdcFhirDbProperties properties;
	private final MongoClientOptions options;
	private final MongoClientFactory factory;

	private MongoClient mongo;

	public Ehr2EdcFhirDbConfiguration(ApplicationContext applicationContext,
									  Ehr2EdcFhirDbProperties properties,
									  ObjectProvider<MongoClientOptions> options,
									  Environment environment) {
		this.applicationContext = applicationContext;
		this.properties = properties;
		this.options = options.getIfAvailable();
		this.factory = new MongoClientFactory(properties.getFhirDb(), environment);
	}

	@PreDestroy
	public void close() {
		if (this.mongo != null) {
			this.mongo.close();
		}
	}

	@Bean
	public SimpleMongoDbFactory ehr2edcFhirMongoDbFactory(
			@Qualifier("ehr2edcFhirMongoClient") MongoClient mongo) {
		String database = this.properties.getFhirDb()
				.getMongoClientDatabase();
		return new SimpleMongoDbFactory(mongo, database);
	}

	@Bean
	public MongoTemplate ehr2edcFhirMongoTemplate(
			@Qualifier("ehr2edcFhirMongoDbFactory") MongoDbFactory mongoDbFactory,
			@Qualifier("ehr2edcFhirMappingMongoConverter") MongoConverter converter) {
		return new MongoTemplate(mongoDbFactory, converter);
	}

	@Bean
	public MongoClient ehr2edcFhirMongoClient() {
		this.mongo = this.factory.createMongoClient(this.options);
		return this.mongo;
	}

	@Bean
	public MappingMongoConverter ehr2edcFhirMappingMongoConverter(
			@Qualifier("ehr2edcFhirMongoDbFactory") MongoDbFactory factory,
			@Qualifier("ehr2edcFhirMongoMappingContext") MongoMappingContext context,
			@Qualifier("ehr2edcFhirMongoCustomConversions") MongoCustomConversions conversions) {
		DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
		MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
		mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
		mappingConverter.setMapKeyDotReplacement("");
		mappingConverter.setCustomConversions(conversions);
		return mappingConverter;
	}

	@Bean
	public MongoMappingContext ehr2edcFhirMongoMappingContext(
			@Qualifier("ehr2edcFhirMongoCustomConversions") MongoCustomConversions conversions)
			throws ClassNotFoundException {
		MongoMappingContext context = new MongoMappingContext();
		context.setInitialEntitySet(new EntityScanner(this.applicationContext).scan(Document.class, Persistent.class));
		Class<?> strategyClass = this.properties.getFhirDb()
				.getFieldNamingStrategy();
		if (strategyClass != null) {
			context.setFieldNamingStrategy((FieldNamingStrategy) BeanUtils.instantiateClass(strategyClass));
		}
		context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
		return context;
	}

	@Bean
	public MongoCustomConversions ehr2edcFhirMongoCustomConversions() {
		return new MongoCustomConversions(Arrays.asList(new StudyIdReaderConverter(), new StudyIdWriterConverter()));
	}
}
