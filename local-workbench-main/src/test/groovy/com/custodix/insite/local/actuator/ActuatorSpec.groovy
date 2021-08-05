package com.custodix.insite.local.actuator

import com.custodix.insite.local.LocalWorkbenchTest
import eu.ehr4cr.workbench.local.health.FlywayHealthIndicator
import eu.ehr4cr.workbench.local.security.CustomPermissionEvaluator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import javax.servlet.Filter

import static org.mockito.Mockito.when
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity

@Transactional
@LocalWorkbenchTest
abstract class ActuatorSpec extends Specification {
    @Autowired
    private WebApplicationContext context
    @Autowired
    private Filter springSecurityFilterChain
    @MockBean
    private FlywayHealthIndicator flywayHealthIndicator
    @MockBean
    private CustomPermissionEvaluator customPermissionEvaluator

    protected MockMvc mvc

    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(springSecurityFilterChain))
                .build()
        when(flywayHealthIndicator.health()).thenReturn(Health.up().build())
    }

}
