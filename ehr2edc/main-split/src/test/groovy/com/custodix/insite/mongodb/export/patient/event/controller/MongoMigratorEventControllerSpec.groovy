package com.custodix.insite.mongodb.export.patient.event.controller

import com.custodix.insite.AsyncEventsTest
import com.custodix.insite.local.ehr2edc.ehr.mongo.command.FailSubjectRegistration
import com.custodix.insite.local.ehr2edc.ehr.mongo.command.StartSubjectRegistration
import com.custodix.insite.local.ehr2edc.ehr.mongo.command.SucceedSubjectRegistration
import com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller.FailSubjectRegistrationController
import com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller.StartSubjectRegistrationController
import com.custodix.insite.local.ehr2edc.ehr.mongo.event.controller.SucceedSubjectRegistrationController
import com.custodix.insite.mongodb.export.patient.application.api.EndSubjectMigration
import com.custodix.insite.mongodb.export.patient.application.api.FailSubjectMigration
import com.custodix.insite.mongodb.export.patient.application.api.StartSubjectMigration
import com.custodix.insite.mongodb.export.patient.event.handler.ExportPatientEndedHandler
import com.custodix.insite.mongodb.export.patient.event.handler.ExportPatientFailedHandler
import com.custodix.insite.mongodb.export.patient.event.handler.ExportPatientStartingHandler
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import

@Import([
        StartSubjectMigrationController,
        FailSubjectMigrationController,
        EndSubjectMigrationController,
        FailSubjectRegistrationController,
        SucceedSubjectRegistrationController,
        StartSubjectRegistrationController,
        ExportPatientEndedHandler,
        ExportPatientFailedHandler,
        ExportPatientStartingHandler
])
abstract class MongoMigratorEventControllerSpec extends AsyncEventsTest{

    @MockBean
    protected StartSubjectMigration startSubjectMigration
    @MockBean
    protected EndSubjectMigration endSubjectMigration
    @MockBean
    protected FailSubjectMigration failSubjectMigration
    @MockBean
    private SucceedSubjectRegistration succeedEHRRegistration
    @MockBean
    private FailSubjectRegistration failEHRRegistration
    @MockBean
    private StartSubjectRegistration startEHRRegistration

}
