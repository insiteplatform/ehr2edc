package com.custodix.insite.local.ehr2edc.query.fhir;

import java.lang.annotation.*;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;

import com.custodix.insite.local.ehr2edc.EHRConnectionRepository;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MockBeans({ @MockBean(EHRConnectionRepository.class) })
@SpringBootTest(classes = { FhirDstu2Configuration.class, FhirQueryDataTest.TestConfiguration.class },
				properties = "spring.main.allow-bean-definition-overriding=true")
public @interface FhirQueryDataTest {
	@EnableAutoConfiguration
	class TestConfiguration {
	}
}
