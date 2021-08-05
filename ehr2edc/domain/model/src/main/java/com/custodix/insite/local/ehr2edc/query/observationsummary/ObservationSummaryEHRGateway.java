package com.custodix.insite.local.ehr2edc.query.observationsummary;

import java.util.List;

import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public interface ObservationSummaryEHRGateway {
	List<ObservationSummaryItem> findForSubject(SubjectId subjectId);
}