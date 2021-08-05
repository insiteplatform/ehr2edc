package com.custodix.insite.local.ehr2edc.query.fhir

import ca.uhn.fhir.context.FhirContext
import org.apache.commons.io.IOUtils
import org.springframework.core.io.ClassPathResource
import spock.lang.Specification

import static java.nio.charset.Charset.defaultCharset

@FhirQueryDataTest
abstract class AbstractFhirDstu2Spec extends Specification {

    static FhirContext fhirDstu2Context = FhirContext.forDstu2()

    String readToString(final String locationOnClasspath) {
        try {
            return IOUtils.toString(new ClassPathResource(locationOnClasspath).getInputStream(), defaultCharset())
        } catch (IOException e) {
            throw new RuntimeException(String.format("Cannot read file from %s" , locationOnClasspath), e)
        }
    }
}
