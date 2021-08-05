package com.custodix.insite.local.ehr2edc.web

import com.custodix.insite.local.ehr2edc.AbstractIntegrationSpecification
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*

class EtagConfigurationSpec extends AbstractIntegrationSpecification {

    def "Does not add an ETag header if we don't request the bundle-resource"() {
        given: "A valid request"
        String location = "http://localhost:" + port + "/build/other-resource"

        when: "Performing the request"
        ResponseEntity response = get(location, entity())

        then: "The response does not contain an ETag-header"
        response.headers
        !response.headers.getETag()
    }

    def "Responds with an ETag header for the bundle-resource"() {
        given: "A request for the bundle resource"
        String location = "http://localhost:" + port + "/build/bundle.js"

        when: "Performing the request"
        ResponseEntity response = get(location, entity())

        then: "The response contains an ETag-header"
        response.headers
        response.headers.getETag()
    }

    def "Returns 'Not Modified' when requesting an unmodified bundle-resource"() {
        given: "An ETag for the bundle resource from a previous request"
        String location = "http://localhost:" + port + "/build/bundle.js"
        String eTag = get(location, entity()).headers.getETag()
        and: "A request for the bundle resource with the ETag"
        HttpEntity entity = entityWith(["If-None-Match": eTag])

        when: "Retrieving the bundle resource once more"
        ResponseEntity response = get(location, entity)

        then: "The response indicate the resource hasn't changed"
        response.statusCode == HttpStatus.NOT_MODIFIED
    }

    static HttpEntity entity() {
        return new HttpEntity<>()
    }

    static HttpEntity entityWith(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.entrySet().forEach {
            e -> httpHeaders.add(e.key, e.value)
        }
        return new HttpEntity<>(httpHeaders)
    }

    static ResponseEntity get(String location, HttpEntity entity) {
        return new TestRestTemplate().exchange(location, HttpMethod.GET, entity, String.class)
    }
}
