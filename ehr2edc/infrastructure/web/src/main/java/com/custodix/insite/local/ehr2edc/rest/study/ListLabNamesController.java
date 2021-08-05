package com.custodix.insite.local.ehr2edc.rest.study;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.custodix.insite.local.ehr2edc.query.ListLocalLabs;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

@RestController
@RequestMapping("/ehr2edc/studies")
public class ListLabNamesController {
	private final ListLocalLabs listLabNames;

	ListLabNamesController(final ListLocalLabs listLabNames) {
		this.listLabNames = listLabNames;
	}

	@GetMapping(value = "{studyId}/lab-names",
				produces = MediaType.APPLICATION_JSON_VALUE)
	public ListLocalLabs.Response getLabNames(@PathVariable String studyId) {
		ListLocalLabs.Request request = ListLocalLabs.Request.newBuilder().withStudyId(StudyId.of(studyId)).build();
		return  listLabNames.list(request);
	}
}
