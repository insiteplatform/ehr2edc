package com.custodix.insite.local.ehr2edc.rest

import com.custodix.insite.local.ehr2edc.rest.jackson.JacksonWebConfiguration
import com.custodix.insite.local.ehr2edc.security.EHR2EDCAuthenticationProvider
import com.custodix.insite.local.ehr2edc.security.WebSecurityConfig
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import spock.lang.Specification

@WithMockUser
@WebAppConfiguration
@ContextConfiguration(classes = [WebSecurityConfig, TestWebConfig, EHR2EDCAuthenticationProvider])
abstract class ControllerSpec extends Specification {
    @Autowired
    private WebApplicationContext wac

    protected MockMvc mockMvc

    def setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(SecurityMockMvcConfigurers.springSecurity()).build()
    }

    @Import([JacksonWebConfiguration])
    @EnableWebMvc
    static class TestWebConfig implements WebMvcConfigurer {
        @Autowired
        private ObjectMapper objectMapper

        @Override
        void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(new StringHttpMessageConverter())
            converters.add(new MappingJackson2HttpMessageConverter(objectMapper))
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper()
        }
    }
}
