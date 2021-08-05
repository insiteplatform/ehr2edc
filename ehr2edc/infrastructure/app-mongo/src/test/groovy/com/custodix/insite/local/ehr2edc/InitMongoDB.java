package com.custodix.insite.local.ehr2edc;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;

import org.bson.BsonDocument;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.FileCopyUtils;

import com.custodix.insite.local.ehr2edc.mongo.app.audit.ReviewContextDocument;
import com.custodix.insite.local.ehr2edc.mongo.app.document.EDCConnectionDocument;

@Configuration
public class InitMongoDB {
	private final MongoTemplate mongoTemplate;
	private final ResourceLoader resourceLoader;

	public InitMongoDB(@Qualifier("ehr2edcMongoAppMongoTemplate") MongoTemplate mongoTemplate,
			ResourceLoader resourceLoader) throws IOException {
		this.mongoTemplate = mongoTemplate;
		this.resourceLoader = resourceLoader;
		insertReviewContextDocuments();
		insertEDCConnectionDocuments();
	}

	private void insertReviewContextDocuments() throws IOException {
		insertDocuments("classpath:/migrations/reviewcontextdocument/init/*.json", ReviewContextDocument.COLLECTION);
	}

	private void insertEDCConnectionDocuments() throws IOException {
		insertDocuments("classpath:/migrations/edcconnectiondocument/init/*.json", EDCConnectionDocument.COLLECTION);
		insertDocuments("classpath:/migrations/edcconnectiondocument/add-edc-system/before*.json",
				EDCConnectionDocument.COLLECTION);
	}

	private void insertDocuments(String pathPattern, String collection) throws IOException {
		Resource[] documents = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
				.getResources(pathPattern);
		Arrays.stream(documents)
				.map(this::readResource)
				.map(BsonDocument::parse)
				.forEach(d -> mongoTemplate.insert(d, collection));
	}

	private String readResource(Resource resource) {
		try {
			return new String(FileCopyUtils.copyToByteArray(resource.getFile()));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
