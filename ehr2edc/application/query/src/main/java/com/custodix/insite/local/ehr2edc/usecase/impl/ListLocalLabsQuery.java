package com.custodix.insite.local.ehr2edc.usecase.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.LabAnalyteRangesGateway;
import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyConnectionRepository;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.query.ListLocalLabs;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;
import com.custodix.insite.local.ehr2edc.vocabulary.ExternalEDCConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.LabName;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyConnectionType;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

@Query
class ListLocalLabsQuery implements ListLocalLabs {
	private final StudyRepository studyRepository;
	private final StudyConnectionRepository studyConnectionRepository;
	private final LabAnalyteRangesGateway labAnalyteRangesGateway;
	private final GetCurrentUser currentUser;

	ListLocalLabsQuery(StudyRepository studyRepository, StudyConnectionRepository studyConnectionRepository,
			LabAnalyteRangesGateway labAnalyteRangesGateway, GetCurrentUser currentUser) {
		this.studyRepository = studyRepository;
		this.studyConnectionRepository = studyConnectionRepository;
		this.labAnalyteRangesGateway = labAnalyteRangesGateway;
		this.currentUser = currentUser;
	}

	@Override
	public Response list(Request request) {
		Study study = studyRepository.findStudyByIdAndInvestigator(request.getStudyId(), currentUser.getUserId());
		List<LabName> labNames = new ArrayList<>();
		findUsableStudyConnection(study.getStudyId()).ifPresent(c -> labNames.addAll(labAnalyteRangesGateway.findActiveLabs(c)));
		return Response.newBuilder()
				.withLocalLabs(labNames)
				.build();
	}

	private Optional<ExternalEDCConnection> findUsableStudyConnection(StudyId studyId) {
		if (studyConnectionRepository.isEnabled(studyId, StudyConnectionType.READ_LABNAMES)) {
			return Optional.of(studyConnectionRepository.getReadLabnamesStudyConnectionById(studyId));
		}
		return Optional.empty();
	}

}
