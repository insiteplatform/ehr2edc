package com.custodix.insite.mongodb.export.patient.main;

import static com.custodix.insite.mongodb.export.patient.main.MongoMigratorBatchConfiguration.MONGO_TEMPLATE_MONGO_MIGRATOR;
import static java.util.Arrays.asList;

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
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.annotation.Persistent;
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

import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.converter.SubjectIdReaderConverter;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.converter.SubjectIdWriterConverter;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.activesubject.ActiveSubjectDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.demographic.DemographicDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue.LabValueDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.medication.MedicationDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.observationsummary.ObservationSummaryDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.subjectmigration.SubjectMigrationDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.vitalsign.VitalSignDocument;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

@Configuration
@EnableConfigurationProperties(MigratorMongoDBConfigurationProperties.class)
@EnableMongoRepositories(basePackages = "com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository",
						 mongoTemplateRef = MONGO_TEMPLATE_MONGO_MIGRATOR)
public class MigratorMongoDBConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(MigratorMongoDBConfiguration.class);

	private final MigratorMongoDBConfigurationProperties properties;
	private final MongoClientOptions options;
	private final MongoClientFactory factory;
	private ApplicationContext applicationContext;

	private MongoClient mongo;

	public MigratorMongoDBConfiguration(MigratorMongoDBConfigurationProperties properties,
			ObjectProvider<MongoClientOptions> options, Environment environment,
			ApplicationContext applicationContext) {
		this.properties = properties;
		this.options = options.getIfAvailable();
		this.applicationContext = applicationContext;
		this.factory = new MongoClientFactory(properties.getMongoDb(), environment);
	}

	@PreDestroy
	public void close() {
		if (this.mongo != null) {
			this.mongo.close();
		}
	}

	@Bean(name = "mongoMigratorMongoTransactionManager")
	PlatformTransactionManager transactionManager(
			@Qualifier("mongoMigratorMongoDbFactory") MongoDbFactory mongoDbFactory,
			@Value("${ehr2edc.mongo.transactions:false}") boolean enableTransactions) {
		if (enableTransactions){
			return new MongoTransactionManager(mongoDbFactory);
		}
		return new DummyMongoTransactionManager();
	}

	@Bean
	public SimpleMongoDbFactory mongoMigratorMongoDbFactory(@Qualifier("mongoMigratorMongoClient") MongoClient mongo) {
		String database = this.properties.getMongoDb()
				.getMongoClientDatabase();
		return new SimpleMongoDbFactory(mongo, database);
	}

	@Bean
	public MongoTemplate mongoMigratorMongoTemplate(
			@Qualifier("mongoMigratorMongoDbFactory") MongoDbFactory mongoDbFactory,
			@Qualifier("mongoMigratorMappingMongoConverter") MongoConverter converter) {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, converter);
		initializeCollections(mongoTemplate);
		return mongoTemplate;
	}

	private void initializeCollections(MongoTemplate mongoTemplate) {
		List<Class<?>> documents = asList(ActiveSubjectDocument.class, DemographicDocument.class, LabValueDocument.class,
				ObservationSummaryDocument.class, SubjectMigrationDocument.class, VitalSignDocument.class,
				MedicationDocument.class);

		documents.forEach(c -> {
			try {
				mongoTemplate.createCollection(c);
			} catch (Exception e) {
				LOGGER.debug("Could not create collections, possibly because they already exist", e);
			}
		});
	}

	@Bean
	public MongoClient mongoMigratorMongoClient() {
		this.mongo = this.factory.createMongoClient(this.options);
		return this.mongo;
	}

	@Bean
	public MappingMongoConverter mongoMigratorMappingMongoConverter(
			@Qualifier("mongoMigratorMongoDbFactory") MongoDbFactory factory,
			@Qualifier("mongoMigratorMongoMappingContext") MongoMappingContext context,
			@Qualifier("mongoMigratorMongoCustomConversions") MongoCustomConversions conversions) {
		DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
		MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
		mappingConverter.setCustomConversions(conversions);
		mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
		return mappingConverter;
	}

	@Bean
	public MongoMappingContext mongoMigratorMongoMappingContext(
			@Qualifier("mongoMigratorMongoCustomConversions") MongoCustomConversions conversions)
			throws ClassNotFoundException {
		MongoMappingContext context = new MongoMappingContext();
		context.setInitialEntitySet(new EntityScanner(this.applicationContext).scan(Document.class, Persistent.class));
		Class<?> strategyClass = this.properties.getMongoDb()
				.getFieldNamingStrategy();
		if (strategyClass != null) {
			context.setFieldNamingStrategy((FieldNamingStrategy) BeanUtils.instantiateClass(strategyClass));
		}
		context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
		return context;
	}

	@Bean
	public MongoCustomConversions mongoMigratorMongoCustomConversions() {
		return new MongoCustomConversions(
				asList(new SubjectIdWriterConverter(), new SubjectIdReaderConverter()));
	}
}
