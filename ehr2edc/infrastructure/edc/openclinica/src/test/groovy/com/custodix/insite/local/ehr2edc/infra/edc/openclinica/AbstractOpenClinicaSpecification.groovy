package com.custodix.insite.local.ehr2edc.infra.edc.openclinica

import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.Rule
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.FileCopyUtils
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import static java.nio.charset.StandardCharsets.UTF_8

@ContextConfiguration(classes = OpenClinicaConfiguration)
abstract class AbstractOpenClinicaSpecification extends Specification {
    protected static final AuthenticationToken AUTHENTICATION_TOKEN = new AuthenticationToken("token")

    @Rule
    protected WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort())
    @Autowired
    protected OpenClinicaEDCStudyGateway openClinicaEDCStudyGateway
    @SpringBean
    protected AuthenticationTokenProvider authenticationTokenProvider = Stub()

    protected String readFile(String path) {
        def sampleResource = new ClassPathResource(path)
        new String(FileCopyUtils.copyToByteArray(sampleResource.inputStream), UTF_8)
    }
}
