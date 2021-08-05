package com.custodix.insite.local.ehr2edc.rest.study;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.custodix.insite.local.ehr2edc.query.ListAvailablePatientIds;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

@RestController
@RequestMapping("/ehr2edc/ehr/patients")
public final class EHRPatientsController {

	private final ListAvailablePatientIds listAvailablePatientIds;

	EHRPatientsController(ListAvailablePatientIds listAvailablePatientIds) {
		this.listAvailablePatientIds = listAvailablePatientIds;
	}

	@GetMapping
	public ListAvailablePatientIds.Response listAvailablePatientIds(
			@RequestParam(value = "patientDomain") String patientDomain,
			@RequestParam(value = "studyId") String studyId,
			@RequestParam(value = "filter", required = false) String filter,
			@RequestParam(value = "limit", required = false) Long limit) {
		final StudyId id = StudyId.of(studyId);
		final ListAvailablePatientIds.Request fullRequest = ListAvailablePatientIds.Request.newBuilder()
				.withStudyId(id)
				.withPatientDomain(patientDomain)
				.withFilter(filter)
				.withLimit(limit)
				.build();
		return listAvailablePatientIds.list(fullRequest);
	}
}
