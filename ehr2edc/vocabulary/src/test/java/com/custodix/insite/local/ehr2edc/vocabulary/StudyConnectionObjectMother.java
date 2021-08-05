package com.custodix.insite.local.ehr2edc.vocabulary;

import java.net.URI;

public class StudyConnectionObjectMother {

	public static ExternalEDCConnection aDefaultStudyConnectionOfType(StudyId studyId, StudyConnectionType type) {
		return aDefaultStudyConnectionBuilder(studyId, type).build();
	}

	public static ExternalEDCConnection aDefaultStudyConnectionOfType(StudyConnectionType type) {
		return aDefaultStudyConnectionBuilder(type).build();
	}

	public static ExternalEDCConnection aDefaultStudyConnection() {
		return aDefaultStudyConnectionBuilder().build();
	}

	public static ExternalEDCConnection aDefaultReadSubjectsStudyConnection() {
		return aDefaultStudyConnectionOfType(StudyConnectionType.READ_SUBJECTS);
	}

	public static ExternalEDCConnection aDefaultWriteSubjectStudyConnection() {
		return aDefaultStudyConnectionOfType(StudyConnectionType.WRITE_SUBJECT);
	}

	public static ExternalEDCConnection aDefaultSubmitEventStudyConnection() {
		return aDefaultStudyConnectionOfType(StudyConnectionType.SUBMIT_EVENT);
	}

	public static ExternalEDCConnection aDefaultReadLabnamesStudyConnection() {
		return aDefaultStudyConnectionOfType(StudyConnectionType.READ_LABNAMES);
	}

	public static ExternalEDCConnection.Builder aDefaultStudyConnectionBuilder() {
		return aDefaultStudyConnectionBuilder(StudyConnectionType.READ_SUBJECTS);
	}

	public static ExternalEDCConnection.Builder aDefaultStudyConnectionBuilder(StudyConnectionType type) {
		return aDefaultStudyConnectionBuilder(StudyIdObjectMother.aRandomStudyId(), type);
	}

	public static ExternalEDCConnection.Builder aDefaultStudyConnectionBuilder(StudyId studyId,
			StudyConnectionType type) {
		return aBasicStudyConnectionBuilder(studyId, type).withStudyIdOVerride(StudyIdObjectMother.aRandomStudyId());
	}

	public static ExternalEDCConnection.Builder aDefaultStudyConnectionBuilderWithPort(StudyId studyId,
			StudyConnectionType type, String port) {
		return aDefaultStudyConnectionBuilder(studyId, type).withClinicalDataURI(
				URI.create("http://localhost:" + port + "/RaveWebServices/studies/" + studyId.getId() + "/Subjects"));
	}

	public static ExternalEDCConnection.Builder aBasicStudyConnectionBuilder(StudyId studyId,
			StudyConnectionType type) {
		return ExternalEDCConnection.newBuilder()
				.withStudyId(studyId)
				.withConnectionType(type)
				.withEdcSystem(EDCSystem.RAVE)
				.withExternalSiteId(ExternalSiteId.of("external-site-id"))
				.withClinicalDataURI(
						URI.create("http://localhost:8888/RaveWebServices/studies/" + studyId.getId() + "/Subjects"))
				.withUsername("username")
				.withPassword("password")
				.withEnabled(true);
	}
}
