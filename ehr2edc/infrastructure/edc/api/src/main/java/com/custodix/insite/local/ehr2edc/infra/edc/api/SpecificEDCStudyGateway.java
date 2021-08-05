package com.custodix.insite.local.ehr2edc.infra.edc.api;

import java.util.List;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;

public interface SpecificEDCStudyGateway {
	boolean supports(EDCSystem edcSystem);

	List<EDCSubjectReference> findRegisteredSubjectIds(ExternalEDCConnection connection);

	boolean isRegisteredSubject(ExternalEDCConnection connection, EDCSubjectReference reference);

	void createSubject(ExternalEDCConnection connection, Study study, EDCSubjectReference reference);

	String submitReviewedEvent(ExternalEDCConnection connection, SubmittedEvent reviewedEvent, Study study);
}
