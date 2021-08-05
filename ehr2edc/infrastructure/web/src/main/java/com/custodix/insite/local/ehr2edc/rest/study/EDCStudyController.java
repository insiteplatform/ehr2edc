package com.custodix.insite.local.ehr2edc.rest.study;

import static java.util.Optional.ofNullable;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;

import com.custodix.insite.local.ehr2edc.command.LinkConnection;
import com.custodix.insite.local.ehr2edc.query.ListAvailableSubjectIds;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

@RestController
@RequestMapping("/ehr2edc/edc")
public final class EDCStudyController {

	private final LinkConnection linkConnection;
	private final ListAvailableSubjectIds listAvailableSubjectIds;

	EDCStudyController(LinkConnection linkConnection, ListAvailableSubjectIds listAvailableSubjectIds) {
		this.linkConnection = linkConnection;
		this.listAvailableSubjectIds = listAvailableSubjectIds;
	}

	/**
	 * @deprecated use {@link com.custodix.insite.local.ehr2edc.rest.actuator.ActuatorManagementController} instead
	 */
	@Deprecated
	@PostMapping("/connection")
	public void linkConnection(@RequestBody LinkConnection.Request request) {
		final LinkConnection.Request newRequest = LinkConnection.Request.newBuilder(request)
				.withEdcSystem(ofNullable(request.getEdcSystem()).orElse(EDCSystem.RAVE))
				.build();
		linkConnection.link(newRequest);
	}

	@GetMapping("/subjects")
	public ListAvailableSubjectIds.Response listAvailableSubjectIds(@RequestParam String studyId,
			HttpServletResponse httpServletResponse) {
		final StudyId id = StudyId.of(studyId);
		final ListAvailableSubjectIds.Request request = ListAvailableSubjectIds.Request.newBuilder()
				.withStudyId(id)
				.build();
		final ListAvailableSubjectIds.Response response = listAvailableSubjectIds.list(request);
		if (!response.isFromEDC()) {
			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		return response;
	}
}
