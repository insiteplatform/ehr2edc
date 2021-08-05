package com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.mapper;

import java.util.Collections;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.*;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSubjectReference;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

public final class ClinicalDataODMMapper {

	private ClinicalDataODMMapper() {
	}

	public static ODM mapSubjectODMFor(Study study, EDCSubjectReference reference, ExternalEDCConnection connection) {
		SiteRef siteRef = mapSiteRef(connection);
		SubjectData subject = mapInsertSubjectData(reference, siteRef);
		ClinicalData clinicalData = mapClinicalData(study, subject, connection);

		return mapClinicalData(clinicalData);
	}

	public static ODM mapSubmittedEventODMFor(SubmittedEvent submittedEvent, Study study, ExternalEDCConnection connection) {
		EDCSubjectReference edcSubjectReference = study.getEDCSubjectReference(submittedEvent.getSubjectId());

		SiteRef siteRef = mapSiteRef(connection);
		StudyEventData studyEventData = new ReviewedEventMapper(submittedEvent, connection).mapStudyEventData();
		SubjectData subject = mapUpdateSubjectData(edcSubjectReference, studyEventData, siteRef);
		ClinicalData clinicalData = mapClinicalData(study, subject, connection);

		return mapClinicalData(clinicalData);
	}

	private static SiteRef mapSiteRef(ExternalEDCConnection connection) {
		return SiteRef.newBuilder()
				.withLocationOID(connection.getExternalSiteId()
						.getId())
				.build();
	}

	private static SubjectData mapInsertSubjectData(EDCSubjectReference reference, SiteRef siteRef) {
		return SubjectData.newBuilder()
				.withSubjectKey(reference.getId())
				.withSiteRef(siteRef)
				.withTransactionType(TransactionType.INSERT)
				.build();
	}

	private static SubjectData mapUpdateSubjectData(EDCSubjectReference edcSubjectReference,
			StudyEventData studyEventData, SiteRef siteRef) {
		return SubjectData.newBuilder()
				.withSubjectKey(edcSubjectReference.getId())
				.withSiteRef(siteRef)
				.withTransactionType(TransactionType.UPDATE)
				.withStudyEventData(studyEventData)
				.build();
	}

	private static ClinicalData mapClinicalData(Study study, SubjectData subject, ExternalEDCConnection connection) {
		StudyId studyId = connection.getStudyIdOverride()
				.orElse(study.getStudyId());
		return ClinicalData.newBuilder()
				.withStudyOID(studyId.getId())
				.withMetaDataVersionOID(study.getMetadata()
						.getId())
				.withSubjectData(Collections.singletonList(subject))
				.build();
	}

	private static ODM mapClinicalData(ClinicalData clinicalData) {
		return ODM.newBuilder()
				.withClinicalData(clinicalData)
				.build();
	}

}
