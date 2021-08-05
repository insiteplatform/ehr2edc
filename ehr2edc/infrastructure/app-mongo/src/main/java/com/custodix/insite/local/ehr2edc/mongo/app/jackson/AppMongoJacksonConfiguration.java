package com.custodix.insite.local.ehr2edc.mongo.app.jackson;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import java.io.IOException;

import org.bson.types.ObjectId;

import com.custodix.insite.local.ehr2edc.jackson.ProjectionModule;
import com.custodix.insite.local.ehr2edc.jackson.QueryModule;
import com.custodix.insite.local.ehr2edc.mongo.app.jackson.mixin.AppMongoJacksonMixinsModule;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public final class AppMongoJacksonConfiguration {

	private AppMongoJacksonConfiguration() {
	}

	public static ObjectMapper createObjectMapper() {

		return new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
				.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
				.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
				.registerModule(objectIdModule())
				.registerModule(AppMongoJacksonMixinsModule.create())
				.registerModule(ProjectionModule.create())
				.registerModule(QueryModule.create())
				.findAndRegisterModules();
	}

	private static SimpleModule objectIdModule() {
		SimpleModule objectIdModule = new SimpleModule();
		objectIdModule.addDeserializer(ObjectId.class, createObjectIdDeserializer());
		return objectIdModule;
	}

	private static JsonDeserializer<ObjectId> createObjectIdDeserializer() {
		return new JsonDeserializer<ObjectId>() {
			@Override
			public ObjectId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
				TreeNode oid = p.readValueAsTree()
						.get("$oid");
				String string = oid.toString()
						.replaceAll("\"", "");

				return new ObjectId(string);
			}
		};
	}

}
