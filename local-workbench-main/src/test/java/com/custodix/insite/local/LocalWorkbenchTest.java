package com.custodix.insite.local;

import java.lang.annotation.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import com.custodix.local.workbench.configuration.SpringBootLocalWorkBench;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited

@ContextConfiguration(classes = LocalWorkBenchTestConfiguration.class)
@ActiveProfiles(profiles = { "EMBEDDED_POSTGRES" })
@EnableWebMvc
@WebAppConfiguration
@SpringBootTest(classes = SpringBootLocalWorkBench.class,
				properties = "spring.main.allow-bean-definition-overriding=true")
@TestPropertySource(locations = "classpath:application-test.properties")
public @interface LocalWorkbenchTest {
}
