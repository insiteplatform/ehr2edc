package com.custodix.insite.local.ehr2edc.infra.edc.openclinica;

import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;

class OpenClinicaIsRegisteredSubject {
	private final OpenClinicaGetSubjects getSubjects;

	OpenClinicaIsRegisteredSubject(OpenClinicaGetSubjects getSubjects) {
		this.getSubjects = getSubjects;
	}

	boolean isRegisteredSubject(ExternalEDCConnection connection, EDCSubjectReference reference) {
		return getSubjects.getSubjects(connection)
				.contains(reference);
	}
}
