package com.custodix.insite.local.ehr2edc

import com.custodix.insite.local.ehr2edc.SpringBootEHR2EDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.jms.core.JmsTemplate
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

@SpringBootTest(classes = SpringBootEHR2EDC.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = ["management.endpoints.web.exposure.include=*","management.server.port=0","jms.local.enabled=false",
        "users.datasource.username=sa",
        "ehr2edc.oidc.enabled=false",
        "users.datasource.password=sa",
        "users.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1",
        "users.datasource.driver-class-name=org.h2.Driver"])
@MockBean(JmsTemplate.class)
abstract class AbstractIntegrationSpecification extends Specification {
    @LocalServerPort
    protected int port
    @Value('${local.management.port}')
    protected int managementPort
}
