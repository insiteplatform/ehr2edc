package com.custodix.insite.local.ehr2edc.mongo.app.configuration;

import java.util.Arrays;

import org.springframework.data.convert.TypeInformationMapper;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;

/**
 * An implementation whereby a type discriminator with key '_type' is added to a document
 * when its class is annotated with {@link org.springframework.data.annotation.TypeAlias}.
 *
 * No type discriminator is added when the class is not annotated.
 */
class CustomMongoTypeMapper extends DefaultMongoTypeMapper {
	CustomMongoTypeMapper(TypeInformationMapper... typeInformationMappers) {
		super("_type", Arrays.asList(typeInformationMappers));
	}
}
