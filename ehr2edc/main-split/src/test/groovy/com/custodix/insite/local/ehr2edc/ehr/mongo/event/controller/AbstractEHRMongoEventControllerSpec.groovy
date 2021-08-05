package com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller

import com.custodix.insite.AsyncEventsTest
import com.custodix.insite.local.ehr2edc.ehr.mongo.command.FailSubjectRegistration
import com.custodix.insite.local.ehr2edc.ehr.mongo.command.StartSubjectRegistration
import com.custodix.insite.local.ehr2edc.ehr.mongo.command.SucceedSubjectRegistration
import com.custodix.insite.mongodb.export.patient.application.api.EndSubjectMigration
import com.custodix.insite.mongodb.export.patient.application.api.FailSubjectMigration
import com.custodix.insite.mongodb.export.patient.application.api.StartSubjectMigration
import com.custodix.insite.mongodb.export.patient.event.controller.EndSubjectMigrationController
import com.custodix.insite.mongodb.export.patient.event.controller.FailSubjectMigrationController
import com.custodix.insite.mongodb.export.patient.event.controller.StartSubjectMigrationController
import com.custodix.insite.mongodb.export.patient.event.handler.ExportPatientEndedHandler
import com.custodix.insite.mongodb.export.patient.event.handler.ExportPatientFailedHandler
import com.custodix.insite.mongodb.export.patient.event.handler.ExportPatientStartingHandler
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import

@Import([
        FailSubjectMigrationController,
        EndSubjectMigrationController,
        StartSubjectMigrationController,
        SucceedSubjectRegistrationController,
        FailSubjectRegistrationController,
        StartSubjectRegistrationController,
        ExportPatientEndedHandler,
        ExportPatientFailedHandler,
        ExportPatientStartingHandler
])
abstract class AbstractEHRMongoEventControllerSpec extends AsyncEventsTest{

    @MockBean
    private StartSubjectMigration startSubjectMigration
    @MockBean
    private EndSubjectMigration endSubjectMigration
    @MockBean
    private FailSubjectMigration failSubjectMigration
    @MockBean
    protected SucceedSubjectRegistration succeedEHRRegistration
    @MockBean
    protected FailSubjectRegistration failEHRRegistration
    @MockBean
    protected StartSubjectRegistration startSubjectRegistration
}
