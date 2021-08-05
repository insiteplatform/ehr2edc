package com.custodix.insite.local.ehr2edc.rest.patient;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.custodix.insite.local.ehr2edc.query.GetPatientDomains;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

@RestController
@RequestMapping("/ehr2edc/patients/domains")
public class PatientDomainController {
	private final GetPatientDomains getPatientDomains;

	public PatientDomainController(GetPatientDomains getPatientDomains) {
		this.getPatientDomains = getPatientDomains;
	}

	@GetMapping(produces = "application/json")
	public List<String> getAllNames(@RequestParam(value = "studyId") String studyId) {
		final GetPatientDomains.Request request = GetPatientDomains.Request.newBuilder()
				.withStudyId(StudyId.of(studyId))
				.build();
		return getPatientDomains.getAll(request)
				.getPatientDomains()
				.stream()
				.map(GetPatientDomains.PatientDomain::getName)
				.collect(Collectors.toList());
	}
}