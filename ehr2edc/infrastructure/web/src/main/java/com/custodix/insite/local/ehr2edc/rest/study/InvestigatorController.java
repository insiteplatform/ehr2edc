package com.custodix.insite.local.ehr2edc.rest.study;

import org.springframework.web.bind.annotation.*;

import com.custodix.insite.local.ehr2edc.command.AssignInvestigator;
import com.custodix.insite.local.ehr2edc.command.UnassignInvestigator;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

@RestController
@RequestMapping("/ehr2edc/studies/{studyId}/investigators")
public final class InvestigatorController {

	private final AssignInvestigator assignInvestigator;
	private final UnassignInvestigator unassignInvestigator;

	InvestigatorController(final AssignInvestigator assignInvestigator,
										 final UnassignInvestigator unassignInvestigator) {
		this.assignInvestigator = assignInvestigator;
		this.unassignInvestigator = unassignInvestigator;
	}

	@PutMapping
	public void assignInvestigator(@PathVariable("studyId") String studyId,
			@RequestBody AssignInvestigator.Request jsonRequest) {
		AssignInvestigator.Request request = AssignInvestigator.Request.newBuilder(jsonRequest)
				.withStudyId(StudyId.of(studyId))
				.build();
		assignInvestigator.assign(request);
	}

	@DeleteMapping("/{investigatorId}")
	public void unassignInvestigator(@PathVariable("studyId") String studyId,
			@PathVariable("investigatorId") String investigatorId) {
		unassignInvestigator.unassign(UnassignInvestigator.Request.newBuilder()
				.withStudyId(StudyId.of(studyId))
				.withInvestigatorId(UserIdentifier.of(investigatorId))
				.build());
	}
}