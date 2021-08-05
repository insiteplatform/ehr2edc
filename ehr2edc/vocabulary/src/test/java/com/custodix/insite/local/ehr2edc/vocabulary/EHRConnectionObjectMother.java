package com.custodix.insite.local.ehr2edc.vocabulary;

import java.net.URI;

public class EHRConnectionObjectMother {
	public static EHRConnection aFhirConnection() {
		return aFhirConnection(StudyIdObjectMother.aRandomStudyId(), "http://hapi.fhir.org/baseDstu2");
	}

	public static EHRConnection aFhirConnection(StudyId studyId) {
		return aFhirConnection(studyId, "http://hapi.fhir.org/baseDstu2");
	}

	public static EHRConnection aFhirConnection(StudyId studyId, String url) {
		return withStudyId(studyId)
				.withUri(URI.create(url))
				.withSystem(EHRSystem.FHIR)
				.build();
	}

	public static EHRConnection aMongoConnection(StudyId studyId) {
		return withStudyId(studyId)
				.withSystem(EHRSystem.MONGO)
				.build();
	}

	private static EHRConnection.Builder withStudyId(StudyId studyId) {
		return EHRConnection.newBuilder()
				.withStudyId(studyId);
	}
}
