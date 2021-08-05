package com.custodix.insite.local.ehr2edc.query.mongo;

import java.lang.annotation.*;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootTest(classes = { MongoQueryDataConfiguration.class, MongoQueryDataTest.TestConfiguration.class },
				properties = "spring.main.allow-bean-definition-overriding=true")
public @interface MongoQueryDataTest {
	@EnableAutoConfiguration
	class TestConfiguration {
		@MockBean
		ExportPatient exportPatient;
	}
}
