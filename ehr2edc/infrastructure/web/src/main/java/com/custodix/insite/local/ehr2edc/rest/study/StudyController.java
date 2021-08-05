package com.custodix.insite.local.ehr2edc.rest.study;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.custodix.insite.local.ehr2edc.command.CreateStudy;
import com.custodix.insite.local.ehr2edc.command.UpdateStudyMetadata;
import com.custodix.insite.local.ehr2edc.query.GetStudy;
import com.custodix.insite.local.ehr2edc.query.ListAllStudies;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyODM;

@RestController
@RequestMapping("/ehr2edc/studies")
public final class StudyController {

	private final ListAllStudies listAllStudies;
	private final GetStudy getStudy;
	private final CreateStudy createStudy;
	private final UpdateStudyMetadata updateStudyMetadata;

	StudyController(ListAllStudies listAllStudies, GetStudy getStudy, CreateStudy createStudy,
			UpdateStudyMetadata updateStudyMetadata) {
		this.listAllStudies = listAllStudies;
		this.getStudy = getStudy;
		this.createStudy = createStudy;
		this.updateStudyMetadata = updateStudyMetadata;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ListAllStudies.Response listAllStudies() {
		return listAllStudies.allStudies();
	}

	@GetMapping(value = "/{studyId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public GetStudy.Study getStudy(@PathVariable String studyId) {
		return getStudy.getStudy(GetStudy.Request.newBuilder()
				.withStudyId(StudyId.of(studyId))
				.build())
				.getStudy();
	}

	/**
	 * @deprecated use {@link com.custodix.insite.local.ehr2edc.rest.actuator.ActuatorManagementController} instead
	 */
	@Deprecated
	@PostMapping
	public ResponseEntity createStudy(final @RequestParam("file") MultipartFile file)
			throws IOException, URISyntaxException {
		final CreateStudy.Request request = CreateStudy.Request.newBuilder()
				.withStudyODM(StudyODM.of(new String(file.getBytes())))
				.build();
		final CreateStudy.Response response = createStudy.create(request);
		final URI location = new URI("/ehr2edc/studies/" + response.getStudyId().getId());
		return ResponseEntity.created(location).build();
	}

	/**
	 * @deprecated use {@link com.custodix.insite.local.ehr2edc.rest.actuator.ActuatorManagementController} instead
	 */
	@Deprecated
	@PutMapping("/{studyId}/metadata")
	public void updateStudyMetadata(@PathVariable("studyId") String studyId,
			final @RequestParam("file") MultipartFile file) throws IOException {
		final UpdateStudyMetadata.Request request = UpdateStudyMetadata.Request.newBuilder()
				.withStudyId(StudyId.of(studyId))
				.withStudyODM(StudyODM.of(new String(file.getBytes())))
				.build();
		updateStudyMetadata.update(request);
	}
}
