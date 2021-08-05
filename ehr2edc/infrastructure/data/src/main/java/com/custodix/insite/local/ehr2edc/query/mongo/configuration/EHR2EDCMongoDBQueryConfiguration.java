package com.custodix.insite.local.ehr2edc.query.mongo.configuration;

import com.custodix.insite.local.ehr2edc.query.mongo.converter.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
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

import javax.annotation.PreDestroy;
import java.util.Arrays;

@Configuration
@EnableConfigurationProperties(EHR2EDCMongoDBQueryProperties.class)
@EnableMongoRepositories(basePackages = "com.custodix.insite.local.ehr2edc.query.mongo",
						 mongoTemplateRef = "ehr2edcMongoQueryMongoTemplate")
class EHR2EDCMongoDBQueryConfiguration {

	private final ApplicationContext applicationContext;
	private final EHR2EDCMongoDBQueryProperties properties;
	private final MongoClientOptions options;
	private final MongoClientFactory factory;

	private MongoClient mongo;

	public EHR2EDCMongoDBQueryConfiguration(ApplicationContext applicationContext,
			EHR2EDCMongoDBQueryProperties properties, ObjectProvider<MongoClientOptions> options,
			Environment environment) {
		this.applicationContext = applicationContext;
		this.properties = properties;
		this.options = options.getIfAvailable();
		this.factory = new MongoClientFactory(properties.getMongoDbQuery(), environment);
	}

	@PreDestroy
	public void close() {
		if (this.mongo != null) {
			this.mongo.close();
		}
	}

	@Bean
	public SimpleMongoDbFactory ehr2edcMongoQueryMongoDbFactory(
			@Qualifier("ehr2edcMongoQueryMongoClient") MongoClient mongo) {
		String database = this.properties.getMongoDbQuery()
				.getMongoClientDatabase();
		return new SimpleMongoDbFactory(mongo, database);
	}

	@Bean
	public MongoTemplate ehr2edcMongoQueryMongoTemplate(
			@Qualifier("ehr2edcMongoQueryMongoDbFactory") MongoDbFactory mongoDbFactory,
			@Qualifier("ehr2edcMongoQueryMappingMongoConverter") MongoConverter converter) {
		return new MongoTemplate(mongoDbFactory, converter);
	}

	@Bean
	public MongoClient ehr2edcMongoQueryMongoClient() {
		this.mongo = this.factory.createMongoClient(this.options);
		return this.mongo;
	}

	@Bean
	public MappingMongoConverter ehr2edcMongoQueryMappingMongoConverter(
			@Qualifier("ehr2edcMongoQueryMongoDbFactory") MongoDbFactory factory,
			@Qualifier("ehr2edcMongoQueryMongoMappingContext") MongoMappingContext context,
			@Qualifier("ehr2edcMongoQueryMongoCustomConversions") MongoCustomConversions conversions) {
		DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
		MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
		mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
		mappingConverter.setCustomConversions(conversions);
		mappingConverter.setMapKeyDotReplacement("");
		return mappingConverter;
	}

	@Bean
	public MongoMappingContext ehr2edcMongoQueryMongoMappingContext(
			@Qualifier("ehr2edcMongoQueryMongoCustomConversions") MongoCustomConversions conversions)
			throws ClassNotFoundException {
		MongoMappingContext context = new MongoMappingContext();
		context.setInitialEntitySet(new EntityScanner(this.applicationContext).scan(Document.class, Persistent.class));
		Class<?> strategyClass = this.properties.getMongoDbQuery()
				.getFieldNamingStrategy();
		if (strategyClass != null) {
			context.setFieldNamingStrategy((FieldNamingStrategy) BeanUtils.instantiateClass(strategyClass));
		}
		context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
		return context;
	}

	@Bean
	public MongoCustomConversions ehr2edcMongoQueryMongoCustomConversions() {
		return new MongoCustomConversions(Arrays.asList(new SubjectIdWriterConverter(), new SubjectIdReaderConverter(),
				new PatientExporterIdReaderConverter(), new PatientExporterIdWriterConverter(),
				new ConceptReaderConverter()));
	}
}
