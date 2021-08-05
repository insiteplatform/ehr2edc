package com.custodix.insite.mongodb.export.patient.application.command

import com.custodix.insite.mongodb.export.patient.application.api.ExportPatient
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.demographic.DemographicDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue.LabValueDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.labvalue.LabValueDocumentObjectMother
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.medication.MedicationDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.medication.MedicationDocumentObjectMother
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.observationsummary.ObservationSummaryDocument
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.observationsummary.ObservationSummaryDocumentObjectMother
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.vitalsign.VitalSignDocument
import com.custodix.insite.mongodb.vocabulary.Namespace
import com.custodix.insite.mongodb.vocabulary.PatientId
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier
import com.custodix.insite.mongodb.vocabulary.SubjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Query
import spock.lang.Title
import spock.lang.Unroll

import static com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.vitalsign.VitalSignConceptObjectMother.aDefaultVitalSignConceptBuilder
import static com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.vitalsign.VitalSignDocumentObjectMother.aDefaultVitalSignDocumentBuilder
import static org.springframework.data.mongodb.core.query.Criteria.where

@Title("Export patient delete the existing data for a given subject")
class ExportPatientDeleteDocumentSpec extends AbstractExportPatientSpec {

    private static final String FAKE_DEMOGRAPHIC_TYPE = "fakeDemographicType"
    private static final String FAKE_DEMOGRAPHIC_VALUE = "fakeDemographicValue"
    private static final String PATIENT_ID = "7"
    private static final String NAMESPACE = "MASTER_PATIENT_INDEX"
    private static final String SUBJECT_ID = "999-888"
    private static final String FAKE_LAB_VENDOR = "FAKE_LAB_VENDOR"
    private static final String FAKE_ROUTE = "fakeRoute"
    private static final String VITAL_SIGN_CONCEPT_POSITION = "vitalSignConceptPosition"
    private static final String OBSERVATION_SUMMARY_CATEGORY = "ObservationSummaryCategory_123"

    @Autowired
    ExportPatient exportPatient

    @Unroll
    def "Ensure that existing demographic data for given subject id are deleted."(PatientId patientId, Namespace namespace, SubjectId subjectId) {
        given: "a existing demographic with subject id #subjectId"
        def anExistingDemographic = DemographicDocument.newBuilder().withSubjectId(subjectId).withDemographicType(FAKE_DEMOGRAPHIC_TYPE).withValue(FAKE_DEMOGRAPHIC_VALUE).build()
        mongoTemplate.insert(Arrays.asList(anExistingDemographic), DemographicDocument.class)
        assertExistingDemographic()

        when: "exporting patient"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        then: "existing demographic is deleted"
        assertExistingDemographicIsNotPresent()

        where:
        patientId                | namespace               | subjectId                | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | _
    }

    def "Ensure that existing lab value data for given subject id are deleted."(PatientId patientId, Namespace namespace, SubjectId subjectId) {
        given: "a existing lab value with subject id #subjectId"
        def anExistingLabValue = LabValueDocumentObjectMother.aDefaultLabValueDocumentBuilder().forSubject(subjectId).withVendor(FAKE_LAB_VENDOR).build()
        mongoTemplate.insert(Arrays.asList(anExistingLabValue), LabValueDocument.class)
        assertExistingLabValue()

        when: "exporting patient"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        then: "existing lab value is deleted"
        assertExistingLabValueIsNotPresent()

        where:
        patientId                | namespace               | subjectId                | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | _
    }

    @Unroll
    def "Ensure that existing medication data for given subject id are deleted."(PatientId patientId, Namespace namespace, SubjectId subjectId) {
        given: "a existing medication with subject id #subjectId"
        def anExistingMedication = MedicationDocumentObjectMother.aDefaultMedicationDocumentBuilder().withSubjectId(subjectId).withRoute(FAKE_ROUTE).build()
        mongoTemplate.insert(Arrays.asList(anExistingMedication), MedicationDocument.class)
        assertExistingMedication()

        when: "exporting patient"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        then: "existing medication is deleted"
        assertExistingMedicationIsNotPresent()

        where:
        patientId                | namespace               | subjectId                | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | _
    }

    @Unroll
    def "Ensure that existing vital sign data for given subject id are deleted."(PatientId patientId, Namespace namespace, SubjectId subjectId) {
        given: "a existing vital sign with subject id #subjectId"
        def anExistingVitalSign = aDefaultVitalSignDocumentBuilder().withSubjectId(subjectId).withConcept(aDefaultVitalSignConceptBuilder().withPosition(VITAL_SIGN_CONCEPT_POSITION).build()).build()
        mongoTemplate.insert(Arrays.asList(anExistingVitalSign), VitalSignDocument.class)
        assertExistingVitalSign()

        when: "exporting patient"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        then: "existing vital sign is deleted"
        assertExistingVitalSignIsNotPresent()

        where:
        patientId                | namespace               | subjectId                | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | _
    }

    @Unroll
    def "Ensure that existing observation summary data for given subject id are deleted."(PatientId patientId, Namespace namespace, SubjectId subjectId) {
        given: "a existing observation summary with subject id #subjectId"
        def anExistingObservationSummary = ObservationSummaryDocumentObjectMother.aDefaultObservationSummaryBuilder().withSubjectId(subjectId).withCategory(OBSERVATION_SUMMARY_CATEGORY).build()
        mongoTemplate.insert(Arrays.asList(anExistingObservationSummary), ObservationSummaryDocument.class)
        assertExistingObservationSummary()

        when: "exporting patient"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        then: "existing observation summary is deleted"
        assertExistingObservationSummaryIsNotPresent()

        where:
        patientId                | namespace               | subjectId                | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | _
    }

    @Unroll
    def "Ensure export patient works when #documentClass data for given subject id is not existing before export."(PatientId patientId, Namespace namespace, SubjectId subjectId, Class<?> documentClass) {
        given: "no existing #documentClass with subject id #subjectId"
        clearDocumentsFor(documentClass)
        assertNoDocumentPresenceFor(documentClass)

        when: "exporting patient"
        exportPatient.export(ExportPatient.Request.newBuilder().withPatientIdentifier(PatientIdentifier.of(patientId, namespace, subjectId)).build())

        then: "existing #documentClass is for subject id #subjectId "
        assertDocumentPresenceFor(documentClass)

        where:
        patientId                | namespace               | subjectId                | documentClass                    | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | DemographicDocument.class        | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | LabValueDocument.class           | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | MedicationDocument.class         | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | VitalSignDocument.class          | _
        PatientId.of(PATIENT_ID) | Namespace.of(NAMESPACE) | SubjectId.of(SUBJECT_ID) | ObservationSummaryDocument.class | _
    }

    private void assertExistingDemographic() {
        Query query = createExistingDemographicQuery()
        assert mongoTemplate.exists(query, DemographicDocument.class)
    }

    private void assertExistingDemographicIsNotPresent() {
        Query query = createExistingDemographicQuery()
        assert !mongoTemplate.exists(query, DemographicDocument.class)
    }

    private Query createExistingDemographicQuery() {
        Query query = new Query();
        query.addCriteria(where("subjectId").is(SUBJECT_ID).and("demographicType").is(FAKE_DEMOGRAPHIC_TYPE).and("value").is(FAKE_DEMOGRAPHIC_VALUE))
        query
    }

    private void assertExistingLabValue() {
        Query query = createExistingLabValueQuery()
        assert mongoTemplate.exists(query, LabValueDocument.class)
    }

    private void assertExistingLabValueIsNotPresent() {
        Query query = createExistingLabValueQuery()
        assert !mongoTemplate.exists(query, LabValueDocument.class)
    }

    private Query createExistingLabValueQuery() {
        Query query = new Query();
        query.addCriteria(where("subjectId").is(SUBJECT_ID).and("vendor").is(FAKE_LAB_VENDOR))
        query
    }

    private void assertExistingMedication() {
        Query query = createExistingMedicationQuery()
        assert mongoTemplate.exists(query, MedicationDocument.class)
    }

    private void assertExistingMedicationIsNotPresent() {
        Query query = createExistingMedicationQuery()
        assert !mongoTemplate.exists(query, MedicationDocument.class)
    }

    private Query createExistingMedicationQuery() {
        Query query = new Query()
        query.addCriteria(where("subjectId").is(SUBJECT_ID).and("route").is(FAKE_ROUTE))
        query
    }

    private void assertExistingVitalSign() {
        Query query = createExistingVitalSignQuery()
        assert mongoTemplate.exists(query, VitalSignDocument.class)
    }

    private void assertExistingVitalSignIsNotPresent() {
        Query query = createExistingVitalSignQuery()
        assert !mongoTemplate.exists(query, VitalSignDocument.class)
    }

    private Query createExistingVitalSignQuery() {
        Query query = new Query()
        query.addCriteria(where("subjectId").is(SUBJECT_ID).and("concept.position").is(VITAL_SIGN_CONCEPT_POSITION))
        query
    }

    private void assertDocumentPresenceFor(Class<?> documentClass) {
        Query query = new Query()
        query.addCriteria(where("subjectId").is(SUBJECT_ID))
        assert mongoTemplate.exists(query, documentClass)
    }

    private void assertNoDocumentPresenceFor(Class<?> documentClass) {
        Query query = new Query()
        query.addCriteria(where("subjectId").is(SUBJECT_ID))
        assert !mongoTemplate.exists(query, documentClass)
    }


    private void assertExistingObservationSummary() {
        Query query = createExistingObservationSummaryQuery()
        assert mongoTemplate.exists(query, ObservationSummaryDocument.class)
    }

    private void assertExistingObservationSummaryIsNotPresent() {
        Query query = createExistingObservationSummaryQuery()
        assert !mongoTemplate.exists(query, ObservationSummaryDocument.class)
    }

    private Query createExistingObservationSummaryQuery() {
        Query query = new Query();
        query.addCriteria(where("subjectId").is(SUBJECT_ID).and("category").is(OBSERVATION_SUMMARY_CATEGORY))
        query
    }
}
