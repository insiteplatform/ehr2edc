package com.custodix.insite.local.ehr2edc.query.executor.objectmother

import com.custodix.insite.local.ehr2edc.query.executor.common.query.ProjectionContext
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId

import java.time.LocalDate

class ProjectionContexts {
    static ProjectionContext aProjectionContext() {
        return ProjectionContext.newBuilder().build()
    }

    static ProjectionContext aProjectionContextWithIdAndRef(SubjectId id, EDCSubjectReference reference) {
        return ProjectionContext.newBuilder()
                .withSubjectId(id)
                .withEdcSubjectReference(reference)
                .build()
    }

    static ProjectionContext aProjectionContextWithIdRefAndConsentDate(SubjectId id, EDCSubjectReference reference, LocalDate consentDate) {
        return ProjectionContext.newBuilder()
                .withSubjectId(id)
                .withEdcSubjectReference(reference)
                .withConsentDate(consentDate)
                .build()
    }
}
