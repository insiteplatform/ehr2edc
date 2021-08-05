package com.custodix.insite.local.ehr2edc.rest.study;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;

import com.custodix.insite.local.ehr2edc.command.DeregisterSubject;
import com.custodix.insite.local.ehr2edc.command.RegisterSubject;
import com.custodix.insite.local.ehr2edc.query.GetEventPopulationReadiness;
import com.custodix.insite.local.ehr2edc.query.GetSubject;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@RestController
@RequestMapping("/ehr2edc/studies/{studyId}/subjects")
public final class StudySubjectController {

	private final RegisterSubject registerSubject;
	private final DeregisterSubject deregisterSubject;
	private final GetSubject getSubject;
	private final GetEventPopulationReadiness getEventPopulationReadiness;

	StudySubjectController(RegisterSubject registerSubject,
			DeregisterSubject deregisterSubject,
			GetSubject getSubject,
			GetEventPopulationReadiness getEventPopulationReadiness) {
		this.registerSubject = registerSubject;
		this.deregisterSubject = deregisterSubject;
		this.getSubject = getSubject;
		this.getEventPopulationReadiness = getEventPopulationReadiness;
	}

	@PutMapping
	public void registerSubject(@PathVariable("studyId") String studyId, @RequestBody RegisterSubject.Request request,
			HttpServletResponse httpResponse) {
		final RegisterSubject.Request newRequest = RegisterSubject.Request.newBuilder(request)
				.withStudyId(StudyId.of(studyId))
				.withEdcSubjectReference(request.getEdcSubjectReference())
				.build();
		final RegisterSubject.Response response = registerSubject.register(newRequest);
		httpResponse.setStatus(HttpServletResponse.SC_CREATED);
		httpResponse.setHeader("Location", response.getSubjectId().getId());
	}

	@DeleteMapping("/{subjectId}")
	public void deregisterSubject(@PathVariable("studyId") String studyId, @PathVariable("subjectId") String subjectId,
			@RequestBody DeregisterSubject.Request request, HttpServletResponse response) {
		StudyId studyIdParam = StudyId.of(studyId);
		SubjectId subjectIdParam = SubjectId.of(subjectId);
		DeregisterSubject.Request newRequest = DeregisterSubject.Request.newBuilder(request)
				.withStudyId(studyIdParam)
				.withSubjectId(subjectIdParam)
				.build();
		deregisterSubject.deregister(newRequest);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@GetMapping("/{subjectId}")
	public GetSubject.Subject getSubjectInStudy(@PathVariable("studyId") String studyId,
			@PathVariable("subjectId") String subjectId) {
		GetSubject.Request request = GetSubject.Request.newBuilder()
				.withStudyId(StudyId.of(studyId))
				.withSubjectId(SubjectId.of(subjectId))
				.build();

		return getSubject.getSubject(request)
				.getSubject();
	}

	@GetMapping("/{subjectId}/event-population-readiness")
	public GetEventPopulationReadiness.Response getEventPopulationReadiness(@PathVariable("studyId") String studyId,
	@PathVariable("subjectId") String subjectId) {
		GetEventPopulationReadiness.Request request = GetEventPopulationReadiness.Request.newBuilder()
				.withSubjectId(SubjectId.of(subjectId))
				.withStudyId(StudyId.of(studyId))
				.build();
		return getEventPopulationReadiness.getEventPopulationReadiness(request);
	}
}
