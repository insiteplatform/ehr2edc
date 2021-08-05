package com.custodix.insite.local.ehr2edc.rest

import com.custodix.insite.local.ehr2edc.shared.exceptions.*
import groovy.json.JsonSlurper
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import spock.lang.Shared
import spock.lang.Specification

import static java.nio.charset.StandardCharsets.UTF_8
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

class EHR2EDCControllerAdviceSpec extends Specification {

    @RestController
    static class Controller {

        @GetMapping("/domainException")
        def getDomainException() {
            throw new DomainException("Domain error")
        }

        @GetMapping("/constraintViolation")
        def getConstraintViolation() {
            throw new UseCaseConstraintViolationException([
                    new UseCaseConstraintViolation("field", "Field validation failed."),
                    new UseCaseConstraintViolation("field", "And something else is wrong with this field too"),
                    new UseCaseConstraintViolation("another-field", "Another field validation failed.")
            ])
        }

        @GetMapping("/userException")
        def getUserException() {
            throw new UserException("User error")
        }

        @GetMapping("/unexpectedException")
        def getUnexpectedException() {
            throw new RuntimeException("Internal error")
        }

        @GetMapping("/systemException")
        def getSystemException() {
            throw new SystemException("System error")
        }

        @GetMapping("/accessDenied")
        def getAccessDenied() {
            throw new AccessDeniedException("Insufficient privileges")
        }
    }

    static Controller controller = new Controller()

    @Shared
    MockMvc mockMvc

    def setupSpec() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new EHR2EDCControllerAdvice())
                .build()
    }

    def "DomainExceptions get handled as a bad request"() {
        when: "A domain exception is triggered"
        def response = mockMvc.perform(get("/domainException")).andReturn().response

        then: "The response indicates a bad request"
        response.status == HttpStatus.BAD_REQUEST.value()
        and: "Contains JSON"
        response.contentType == MediaType.APPLICATION_JSON.toString()
        and: "Explains what went wrong"
        def outcome = new JsonSlurper().parseText(response.contentAsString)
        outcome.issues.size == 1
        outcome.issues.every({
            it.message == "Domain error" &&
                    !it.field &&
                    !it.reference
        })
        JSONAssert.assertEquals(response.contentAsString, readJsonForController("operationOutcome-domainException"), JSONCompareMode.STRICT)
    }

    def "ConstraintViolationException get handled as a bad request"() {
        when: "A costraint violation exception is triggered"
        def response = mockMvc.perform(get("/constraintViolation")).andReturn().response

        then: "The response indicates a bad request"
        response.status == HttpStatus.BAD_REQUEST.value()
        and: "Contains JSON"
        response.contentType == MediaType.APPLICATION_JSON.toString()
        and: "Has a message for each constraintViolation"
        def outcome = new JsonSlurper().parseText(response.contentAsString)
        outcome.issues.size == 3
        outcome.issues.any({
            it.field == "field" &&
                    (it.message == "Field validation failed." ||
                            it.message == "Field validation failed.") &&
                    !it.reference
        })
        outcome.issues.any({
            it.field == "another-field" &&
                    it.message == "Another field validation failed." &&
                    !it.reference
        })
        JSONAssert.assertEquals(response.contentAsString, readJsonForController("operationOutcome-constraintViolationException"), JSONCompareMode.STRICT)
    }

    def "UserException is handled as a bad request"() {
        when: "A user exception is triggered"
        def response = mockMvc.perform(get("/userException")).andReturn().response

        then: "The response indicates a bad request"
        response.status == HttpStatus.BAD_REQUEST.value()
        and: "Contains JSON"
        response.contentType == MediaType.APPLICATION_JSON.toString()
        and: "Explains what went wrong"
        def outcome = new JsonSlurper().parseText(response.contentAsString)
        def expected = new JsonSlurper().parseText(readJsonForController("operationOutcome-userException"))
        outcome.issues.size == 1
        outcome.issues.every({
            it.message == "User error" &&
                    !it.field &&
                    !it.reference
        })
        JSONAssert.assertEquals(response.contentAsString, readJsonForController("operationOutcome-userException"), JSONCompareMode.STRICT)
    }

    def "Unexpected exceptions get handled as internal server errors"() {
        when: "An unexpected exception is triggered"
        def response = mockMvc.perform(get("/unexpectedException")).andReturn().response

        then: "The response indicates an internal server error"
        response.status == HttpStatus.INTERNAL_SERVER_ERROR.value()
        and: "Contains JSON"
        response.contentType == MediaType.APPLICATION_JSON.toString()
        and: "Has an issue with a generic error message and a reference code"
        def outcome = new JsonSlurper().parseText(response.contentAsString)
        def expected = new JsonSlurper().parseText(readJsonForController("operationOutcome-unexpectedException"))
        outcome.issues.size == 1
        outcome.issues.every({
            it.reference &&
                    it.message == expected.issues[0].message &&
                    !it.field
        })
    }

    def "System exceptions get handled as internal server errors"() {
        when: "A system exception is triggered"
        def response = mockMvc.perform(get("/systemException")).andReturn().response

        then: "The response indicates an internal server error"
        response.status == HttpStatus.INTERNAL_SERVER_ERROR.value()
        and: "Contains JSON"
        response.contentType == MediaType.APPLICATION_JSON.toString()
        and: "Has an issue with a generic error message and a reference code"
        def outcome = new JsonSlurper().parseText(response.contentAsString)
        def expected = new JsonSlurper().parseText(readJsonForController("operationOutcome-systemException"))
        outcome.issues.size == 1
        outcome.issues.every({
            it.reference &&
                    it.message == expected.issues[0].message &&
                    !it.field
        })
    }

    def "AccessDeniedException is handled as a forbidden request"() {
        when: "A access denied exception is triggered"
        def response = mockMvc.perform(get("/accessDenied")).andReturn().response

        then: "The response indicates a forbidden request"
        response.status == HttpStatus.FORBIDDEN.value()
        and: "Contains JSON"
        response.contentType == MediaType.APPLICATION_JSON.toString()
        and: "Explains what went wrong"
        def outcome = new JsonSlurper().parseText(response.contentAsString)
        outcome.issues.size == 1
        outcome.issues.every({
            it.message == "Insufficient privileges" &&
                    !it.field &&
                    !it.reference
        })
        JSONAssert.assertEquals(response.contentAsString, readJsonForController("operationOutcome-accessDeniedException"), JSONCompareMode.STRICT)
    }

    static String readJsonForController(String path) {
        def sampleResource = new ClassPathResource("samples/controlleradvice/"+path+".json")
        return new String(FileCopyUtils.copyToByteArray(sampleResource.inputStream), UTF_8)
    }
}
