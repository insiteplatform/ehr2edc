package com.custodix.insite.local.ehr2edc.mongo.app;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.custodix.insite.local.ehr2edc.mongo.app.configuration.EHR2EDCMongoDBAppConfiguration;
import com.custodix.insite.local.ehr2edc.mongo.app.jackson.AppMongoJacksonConfiguration;

@Configuration
@EnableAutoConfiguration
@Import({ EHR2EDCMongoDBAppConfiguration.class, AppMongoJacksonConfiguration.class })
@ConditionalOnMissingBean(name = "mainTestContext")
public class AppMongoTestContext {
}
