package com.custodix.insite.local.ehr2edc.rest;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.custodix.insite.local.ehr2edc.query.GetAvailableInvestigators;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.UserIdentifier;

@RestController
@RequestMapping("/ehr2edc/users")
public class UsersController {

	private final GetAvailableInvestigators getAvailableInvestigators;

	UsersController(final GetAvailableInvestigators getAvailableInvestigators) {
		this.getAvailableInvestigators = getAvailableInvestigators;
	}

	@GetMapping(params = { "status=unassigned" })
	public PotentialInvestigatorResponse potentialInvestigators(@RequestParam("studyId") StudyId studyId) {
		GetAvailableInvestigators.Request request = GetAvailableInvestigators.Request.newBuilder()
				.withStudyId(studyId)
				.build();
		GetAvailableInvestigators.Response response = getAvailableInvestigators.get(request);
		return mapToControllerResponse(response);
	}

	@GetMapping(params = { "status!=unassigned" })
	public ResponseEntity notFound() {
		return ResponseEntity.notFound()
				.build();
	}

	private PotentialInvestigatorResponse mapToControllerResponse(GetAvailableInvestigators.Response response) {
		List<PotentialInvestigator> potentialInvestigators = response
				.getPotentialInvestigators()
				.stream()
				.map(i -> new PotentialInvestigator(i.getUserIdentifier(), i.getName()))
				.collect(toList());
		return new PotentialInvestigatorResponse(potentialInvestigators);
	}

	static class PotentialInvestigatorResponse {
		private final List<PotentialInvestigator> potentialInvestigators;

		public PotentialInvestigatorResponse(List<PotentialInvestigator> potentialInvestigators) {
			this.potentialInvestigators = potentialInvestigators;
		}

		public List<PotentialInvestigator> getPotentialInvestigators() {
			return potentialInvestigators;
		}
	}

	static class PotentialInvestigator {
		private UserIdentifier id;
		private String name;

		public PotentialInvestigator() {
		}

		public PotentialInvestigator(UserIdentifier userIdentifier, String name) {
			this.id = userIdentifier;
			this.name = name;
		}

		public UserIdentifier getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setId(UserIdentifier id) {
			this.id = id;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
