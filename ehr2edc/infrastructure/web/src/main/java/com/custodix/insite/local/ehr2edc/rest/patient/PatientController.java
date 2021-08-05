package com.custodix.insite.local.ehr2edc.rest.patient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.custodix.insite.local.ehr2edc.query.ListAvailableStudies;
import com.custodix.insite.local.ehr2edc.query.ListRegisteredStudies;
import com.custodix.insite.local.ehr2edc.vocabulary.PatientCDWReference;

@Deprecated
@RestController
@RequestMapping("/ehr2edc/patient")
public class PatientController {

	private final ListRegisteredStudies listRegisteredStudies;
	private final ListAvailableStudies listAvailableStudies;

	public PatientController(ListRegisteredStudies listRegisteredStudies, ListAvailableStudies listAvailableStudies) {
		this.listRegisteredStudies = listRegisteredStudies;
		this.listAvailableStudies = listAvailableStudies;
	}

	@Deprecated
	@GetMapping(value = "/{source}/{id}/studies",
				produces = "application/json")
	public GetStudiesForPatientResponse getStudiesForPatient(@PathVariable("source") String source,
			@PathVariable("id") String id) {
		PatientCDWReference patientCDWReference = PatientCDWReference.newBuilder()
				.withSource(source)
				.withId(id)
				.build();

		ListRegisteredStudies.Request requestRegistered = ListRegisteredStudies.Request.newBuilder()
				.withPatientCDWReference(patientCDWReference)
				.build();

		ListAvailableStudies.Request requestAvailable = ListAvailableStudies.Request.newBuilder()
				.withPatientCDWReference(patientCDWReference)
				.build();

		return GetStudiesForPatientResponse.newBuilder()
				.withRegisteredStudies(mapRegisteredStudies(listRegisteredStudies.registeredStudies(requestRegistered)
						.getRegisteredStudies()))
				.withAvailableStudies(mapAvailableStudies(listAvailableStudies.availableStudies(requestAvailable)
						.getAvailableStudies()))
				.build();
	}

	private List<GetStudiesForPatientResponse.Study> mapRegisteredStudies(List<ListRegisteredStudies.Study> studies) {
		if (studies == null) {
			return Collections.emptyList();
		}
		return studies.stream()
				.map(this::mapRegisteredStudy)
				.collect(Collectors.toList());
	}

	private GetStudiesForPatientResponse.Study mapRegisteredStudy(ListRegisteredStudies.Study study) {
		return GetStudiesForPatientResponse.Study.newBuilder()
				.withStudyId(study.getStudyId())
				.withName(study.getName())
				.withDescription(study.getDescription())
				.withSubject(mapRegisteredSubject(study.getSubject()))
				.build();
	}

	private GetStudiesForPatientResponse.Subject mapRegisteredSubject(ListRegisteredStudies.Subject subject) {
		return GetStudiesForPatientResponse.Subject.newBuilder()
				.withSubjectId(subject.getSubjectId())
				.withEdcSubjectReference(subject.getEdcSubjectReference())
				.withPatientId(subject.getPatientCDWReference())
				.withDateOfConsent(subject.getDateOfConsent())
				.build();
	}

	private List<GetStudiesForPatientResponse.Study> mapAvailableStudies(List<ListAvailableStudies.Study> studies) {
		if (studies == null) {
			return Collections.emptyList();
		}
		return studies.stream()
				.map(this::mapAvailableStudy)
				.collect(Collectors.toList());
	}

	private GetStudiesForPatientResponse.Study mapAvailableStudy(ListAvailableStudies.Study study) {
		return GetStudiesForPatientResponse.Study.newBuilder()
				.withStudyId(study.getStudyId())
				.withName(study.getName())
				.withDescription(study.getDescription())
				.build();
	}

}
