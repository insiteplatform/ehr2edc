package com.custodix.insite.local.ehr2edc.event.controller

import com.custodix.insite.AsyncEventsTest
import com.custodix.insite.local.ehr2edc.command.RecordSubjectRegistrationChange
import com.custodix.insite.local.ehr2edc.ehr.mongo.command.SubjectMigration
import com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller.SubjectMigrationController
import com.custodix.insite.local.ehr2edc.event.handler.SubjectDeregisteredEventHandler
import com.custodix.insite.mongodb.export.patient.application.api.ActivateSubject
import com.custodix.insite.mongodb.export.patient.application.api.DeactivateSubject
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import

@Import([
        SubjectDeregisteredEventHandler,
        SubjectRegisteredEventHandler,
        SubjectRegistrationController,
        SubjectDeactivationController,
        SubjectActivationController,
        SubjectMigrationController
])
class EHR2EDCSyncEventControllerSpec extends AsyncEventsTest {

    @MockBean
    protected RecordSubjectRegistrationChange recordSubjectRegistrationChange

    @MockBean
    protected ActivateSubject activateSubject

    @MockBean
    protected DeactivateSubject deactivateSubject

    @MockBean
    protected SubjectMigration subjectMigration
}
