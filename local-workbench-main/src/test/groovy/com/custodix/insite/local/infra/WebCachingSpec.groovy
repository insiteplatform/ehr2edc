package com.custodix.insite.local.infra

import eu.ehr4cr.workbench.local.global.web.WebMvcConfigurerImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@Title("Web resource caching")
@Narrative("The HTTP response of requests for static resources contains appropriate cache control headers")
@SpringBootTest
@ContextConfiguration(classes = WebMvcConfigurerImpl.class)
@TestPropertySource(properties = ["web.resources.cache.maxage.value=365", "web.resources.cache.maxage.unit=DAYS"])
@EnableWebMvc
class WebCachingSpec extends Specification {
    @Autowired
    private WebApplicationContext context
    private MockMvc mvc

    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .build()
    }

    def "Static resources are cached on the client"() {
        given: "The cache period is configured to be 1 year"

        when: "I request a static resource"
        def result = mvc.perform(get("/test-resource.js"))

        then: "The resource is retrieved"
        def response = result.andReturn().getResponse()
        response.getContentAsString() == "alert(\"test\");"

        and: "The resource is cached for 1 year"
        response.getHeader(HttpHeaders.CACHE_CONTROL) == "max-age=31536000"
    }

    def "Localized message resources are not cached on the client"() {
        when: "I request a localized message resource"
        def result = mvc.perform(get("/locales/test-locale.json"))

        then: "The resource is retrieved"
        def response = result.andReturn().getResponse()
        response.getContentAsString() == "{ \"test\": \"test\" }"

        and: "The resource is not cached"
        response.getHeader(HttpHeaders.CACHE_CONTROL) == "no-store"
    }

    def "Static resources can be retrieved using their MD5 content hash in the request URL"() {
        when: "I request a static resource by its MD5 content hash"
        def result = mvc.perform(get("/test-resource-0a2bb9a287c0c6d331fdd8aaea015195.js"))

        then: "The resource is retrieved"
        result.andReturn().getResponse().getContentAsString() == "alert(\"test\");"
    }

}
