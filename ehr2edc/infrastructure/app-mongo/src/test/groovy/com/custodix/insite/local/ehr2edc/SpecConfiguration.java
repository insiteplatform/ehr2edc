package com.custodix.insite.local.ehr2edc;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.custodix.insite.local.ehr2edc.mongo.app.configuration.EHR2EDCAppMongoConfiguration;
import com.custodix.insite.local.ehr2edc.mongo.app.configuration.EHR2EDCMongoDBAppConfiguration;

@Configuration
@EnableAutoConfiguration
@Import({ InitMongoDB.class, EHR2EDCMongoDBAppConfiguration.class, EHR2EDCAppMongoConfiguration.class })
public class SpecConfiguration {

}
