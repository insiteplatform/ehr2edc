package com.custodix.insite.local.ehr2edc.query.mongo.patient


import com.custodix.insite.local.ehr2edc.patient.PatientDomain
import com.custodix.insite.local.ehr2edc.query.mongo.MongoQueryDataTest
import com.custodix.insite.local.ehr2edc.query.mongo.patient.gateway.InProcessPatientGateway
import com.custodix.insite.local.ehr2edc.query.mongo.patient.model.PatientIdDocument
import com.custodix.insite.local.ehr2edc.query.mongo.patient.repository.PatientIdDocumentRepository
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference
import com.custodix.insite.local.ehr2edc.vocabulary.PatientSearchCriteria
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

@MongoQueryDataTest
class InProcessPatientGatewaySpec extends Specification {
    private static final String ABC_123 = "abc-123"
    private static final String XYZ_123 = "xyz-123"
    private static final String MASTER_PATIENT_INDEX = "MASTER_PATIENT_INDEX"
    private static final String OTHER_PATIENT_INDEX = "OTHER_PATIENT_INDEX"
    private static final LocalDate DATE_2009_09_09 = LocalDate.of(2009, 9, 9)
    private static final LocalDate DATE_2010_10_10 = LocalDate.of(2010, 10, 10)

    @Autowired
    InProcessPatientGateway patientGateway
    @Autowired
    PatientIdDocumentRepository patientIdDocumentRepository

    def setup() {
        patientIdDocumentRepository.deleteAll()
    }

    def "The exists method returns true when a patient exists for a given PatientId and birth date"() {
        given: "A patient with PatientId 'MASTER_PATIENT_INDEX/abc-123' and born on 2009-09-09"
        PatientCDWReference patientId = aPatientId(MASTER_PATIENT_INDEX, ABC_123)
        savePatient(patientId, DATE_2009_09_09)

        when: "The exists method is called with patient id 'MASTER_PATIENT_INDEX/abc-123' and birth date on 2009-09-09"
        boolean result = patientGateway.exists(aPatientSearchCriteria(patientId, DATE_2009_09_09))

        then: "True is returned"
        result
    }

    @Ignore("Until E2E-630 is implemented")
    def "The exists method returns false when no patient exists for a given PatientId and  birth date"() {
        given: "A patient with PatientId 'OTHER_PATIENT_INDEX/xyz-123' and born on 2010-10-10"
        PatientCDWReference patientId = aPatientId(OTHER_PATIENT_INDEX, XYZ_123)
        savePatient(patientId, DATE_2010_10_10)

        when: "The exists method is called with patient id  'MASTER_PATIENT_INDEX/abc-123' and birth date on 2009-09-09"
        boolean result = patientGateway.exists(aPatientSearchCriteria(patientId, DATE_2009_09_09))

        then: "False is returned"
        !result
    }

    @Ignore("Until E2E-630 is implemented")
    def "The exists method returns false when patient exists for a given PatientId and not for given birth date"() {
        given: "A patient with PatientId 'MASTER_PATIENT_INDEX/abc-123' and born on 2010-10-10"
        PatientCDWReference patientId = aPatientId(OTHER_PATIENT_INDEX, XYZ_123)
        savePatient(patientId, DATE_2010_10_10)

        when: "The exists method is called with patient id 'MASTER_PATIENT_INDEX/abc-123' and birth date on 2009-09-09"
        boolean result = patientGateway.exists(aPatientSearchCriteria(patientId, DATE_2009_09_09))

        then: "False is returned"
        !result
    }

    def "The exists method returns false when patient exists for given birth date, but not for given patient id"() {
        given: "A patient with PatientId 'OTHER_PATIENT_INDEX/xyz-123' and born on 2009-09-09"
        PatientCDWReference patientId = aPatientId(OTHER_PATIENT_INDEX, XYZ_123)
        savePatient(patientId, DATE_2009_09_09)

        when: "The exists method is called with patient id  'MASTER_PATIENT_INDEX/abc-123' and birth date on 2009-09-09"
        boolean result = patientGateway.exists(aPatientSearchCriteria( aPatientId(MASTER_PATIENT_INDEX, ABC_123), DATE_2009_09_09))

        then: "False is returned"
        !result
    }

    def "The exists method returns false when a patient exists with the same source but a different id"() {
        given: "A PatientId for which a patient exists with the same source but a different id"
        PatientCDWReference patientId = aPatientId(MASTER_PATIENT_INDEX, XYZ_123)
        savePatient(patientId, DATE_2009_09_09)

        when: "The exists method is called with patient id  'MASTER_PATIENT_INDEX/abc-123' and birth date on 2009-09-09"
        boolean result = patientGateway.exists(aPatientSearchCriteria(aPatientId(MASTER_PATIENT_INDEX, ABC_123), DATE_2009_09_09))

        then: "False is returned"
        !result
    }

    def "The exists method returns false when a patient exists with the same id but a different source"() {
        given: "A PatientId for which a patient exists with the same id but a different source"
        PatientCDWReference patientId = aPatientId(OTHER_PATIENT_INDEX, ABC_123)
        savePatient(patientId, DATE_2009_09_09)

        when: "The exists method is called with patient id  'MASTER_PATIENT_INDEX/abc-123' and birth date on 2009-09-09"
        boolean result = patientGateway.exists(aPatientSearchCriteria(aPatientId(MASTER_PATIENT_INDEX, ABC_123), DATE_2009_09_09))

        then: "False is returned"
        !result
    }

    @Unroll
    def "The find by source and partial identifier returns the expected patients"(List<PatientCDWReference> patients,
                                                                                  String domain,
                                                                                  String filter,
                                                                                  Integer limit,
                                                                                  Set<PatientCDWReference> expected) {
        given: "Known patients #patients"
        patients.each { it -> savePatient(it) }

        when: "Looking for patients with source #domain and partial id #filter"
        Set<PatientCDWReference> result = patientGateway.getFiltered(domain, filter, limit)

        then: "Patient #expected are returned"
        result.size() == expected.size()
        result.containsAll(expected)

        where:
        patients                                             | domain    | filter    | limit    || expected
        [aPatientId("dom", "abc"), aPatientId("src", "abc")] | "dom"     | "a"       | 100      || [aPatientId("dom", "abc")]
        [aPatientId("dom", "abc"), aPatientId("src", "abc")] | "src"     | "a"       | 100      || [aPatientId("src", "abc")]
        [aPatientId("dom", "abc"), aPatientId("src", "abc")] | "dom"     | "abc"     | 100      || [aPatientId("dom", "abc")]
        [aPatientId("dom", "abc"), aPatientId("src", "abc")] | "dom"     | "b"       | 100      || [aPatientId("dom", "abc")]
        [aPatientId("dom", "abc"), aPatientId("src", "abc")] | "dom"     | "bc"      | 100      || [aPatientId("dom", "abc")]
        [aPatientId("dom", "abc"), aPatientId("src", "abc")] | "dom"     | "c"       | 100      || [aPatientId("dom", "abc")]
        [aPatientId("dom", "abc"), aPatientId("src", "abc")] | "dom"     | ""        | 100      || [aPatientId("dom", "abc")]
        [aPatientId("dom", "abc"), aPatientId("src", "abc")] | "dom"     | null      | 100      || [aPatientId("dom", "abc")]
        [aPatientId("dom", "pap"), aPatientId("dom", "aap")] | "dom"     | "ap"      | 100      || [aPatientId("dom", "pap"), aPatientId("dom", "aap")]
        [aPatientId("dom", "pap"), aPatientId("dom", "aap")] | "dom"     | "ap"      | 1        || [aPatientId("dom", "pap")]
        [aPatientId("dom", "abc"), aPatientId("src", "abc")] | "dom"     | "unknown" | 100      || []
        [aPatientId("dom", "abc"), aPatientId("src", "abc")] | "dom"     | "ac"      | 100      || []
        [aPatientId("dom", "abc"), aPatientId("src", "abc")] | "dom"     | "d"       | 100      || []
        [aPatientId("dom", "abc"), aPatientId("src", "abc")] | "unknown" | "a"       | 100      || []
    }

    @Unroll
    def "The get all domains returns all known domains"(List<PatientCDWReference> patients,
                                                        List<PatientDomain> expected) {
        given: "Known patients #patients"
        patients.each { it -> savePatient(it) }

        when: "Retrieving all known patient domains"
        Set<PatientDomain> result = patientGateway.getPatientDomains()

        then: "Patient #expected are returned"
        result.size() == expected.size()
        result.containsAll(expected)

        where:
        patients                                             || expected
        [aPatientId("dom", "abc")]                           || [aDomain("dom")]
        [aPatientId("dom", "abc"), aPatientId("dom", "xyz")] || [aDomain("dom")]
        [aPatientId("dom", "abc"), aPatientId("src", "abc")] || [aDomain("dom"), aDomain("src")]
        []                                                   || []
    }

    def aPatientId(String source, String id) {
        PatientCDWReference.newBuilder().withId(id).withSource(source).build()
    }

    def aPatientId() {
        aPatientId(ABC_123, MASTER_PATIENT_INDEX)
    }

    def aDomain(String name) {
        PatientDomain.of(name)
    }

    def savePatient(PatientCDWReference patientId) {
        savePatient(patientId, DATE_2009_09_09)
    }

    def savePatient(PatientCDWReference patientId, LocalDate birthDate) {
        patientIdDocumentRepository.save(new PatientIdDocument(patientId.source, patientId.id, birthDate))
    }

    def aPatientSearchCriteria( patientSearchCriteria, birthDate) {
        PatientSearchCriteria.newBuilder().withPatientCDWReference(patientSearchCriteria).withBirthDate(birthDate).build()
    }

}
