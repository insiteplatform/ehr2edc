package com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.mapper;

import java.util.Collections;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model.ClinicalData;
import com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model.ODM;
import com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model.StudyEventData;
import com.custodix.insite.local.ehr2edc.infra.edc.openclinica.odm.model.SubjectData;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public final class ClinicalDataODMMapper {

	public ClinicalDataODMMapper() {
	}

	public ODM mapSubmittedEventODMFor(SubmittedEvent submittedEvent, Study study,
			ExternalEDCConnection connection) {
		EDCSubjectReference edcSubjectReference = study.getEDCSubjectReference(submittedEvent.getSubjectId());

		StudyEventData studyEventData = new ReviewedEventMapper(submittedEvent, connection).mapStudyEventData();
		SubjectData subject = mapUpdateSubjectData(edcSubjectReference, studyEventData);
		ClinicalData clinicalData = mapClinicalData(study, subject, connection);

		return mapODM(clinicalData, submittedEvent);
	}

	private SubjectData mapUpdateSubjectData(EDCSubjectReference edcSubjectReference,
			StudyEventData studyEventData) {
		return SubjectData.newBuilder()
				.withSubjectKey(edcSubjectReference.getId())
				.withStudyEventData(studyEventData)
				.build();
	}

	private ClinicalData mapClinicalData(Study study, SubjectData subject, ExternalEDCConnection connection) {
		StudyId studyId = connection.getStudyIdOverride()
				.orElse(study.getStudyId());
		return ClinicalData.newBuilder()
				.withStudyOID(studyId.getId())
				.withMetaDataVersionOID(study.getMetadata()
						.getId())
				.withSubjectData(Collections.singletonList(subject))
				.build();
	}

	private ODM mapODM(ClinicalData clinicalData, SubmittedEvent submittedEvent) {
		return ODM.newBuilder()
				.withClinicalData(clinicalData)
				.withCreationDateTime(submittedEvent.getSubmittedDate())
				.build();
	}

}
