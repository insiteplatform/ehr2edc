package com.custodix.insite.local.user


import com.custodix.insite.local.LocalWorkbenchTest
import com.custodix.insite.local.cohort.scenario.objectMother.AuthenticationAttempts
import com.custodix.insite.local.cohort.scenario.objectMother.Features
import com.custodix.insite.local.cohort.scenario.objectMother.Users
import eu.ehr4cr.workbench.local.TestEventPublisher
import eu.ehr4cr.workbench.local.eventpublisher.DomainEventPublisher
import eu.ehr4cr.workbench.local.properties.AccountSecuritySettings
import eu.ehr4cr.workbench.local.properties.PropertyProvider
import eu.ehr4cr.workbench.local.service.DomainTime
import eu.ehr4cr.workbench.local.service.TestTimeService
import eu.ehr4cr.workbench.local.service.email.TestMailService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.web.FilterChainProxy
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

@LocalWorkbenchTest
@Transactional
abstract class AbstractSpecification extends Specification {

    @Autowired
    protected Users users
    @Autowired
    protected AuthenticationAttempts authenticationAttempts
    @Autowired
    protected Features features
    @Autowired
    protected TestMailService testMailService
    @Autowired
    protected PropertyProvider propertyProvider
    @Autowired
    protected AccountSecuritySettings invitationSettings;
    @Autowired
    protected TestEventPublisher eventPublisher
    @Autowired
    protected TestTimeService testTimeService

    @Autowired
    protected WebApplicationContext wac

    @Autowired
    private FilterChainProxy filterChainProxy

    protected MockMvc mockMvc

    def setup() {
        mockMvc = webAppContextSetup(wac)
                .apply(springSecurity(filterChainProxy))
                .build()
        testMailService.clear()
        eventPublisher.clear()
        DomainEventPublisher.setPublisher(eventPublisher)
        DomainTime.setTime(testTimeService)
    }

}
