package com.custodix.insite.local.ehr2edc.rest.actuator;

import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.custodix.insite.local.ehr2edc.command.*;
import com.custodix.insite.local.ehr2edc.vocabulary.EDCSystem;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyODM;

@RestControllerEndpoint(id = "management")
@Component
public class ActuatorManagementController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActuatorManagementController.class);

	private final DatawarehouseUpdated datawarehouseUpdated;
	private final CreateStudy createStudy;
	private final UpdateStudyMetadata updateStudyMetadata;
	private final LinkConnection linkConnection;
	private final LinkEHRConnection linkEHRConnection;
	private final SaveItemQueryMapping saveItemQueryMapping;
	private final DeleteItemQueryMapping deleteItemQueryMapping;
	private final ClearItemQueryMappings clearItemQueryMappings;
	private final DeleteStudy deleteStudy;

	//CHECKSTYLE:OFF
	ActuatorManagementController(DatawarehouseUpdated datawarehouseUpdated, CreateStudy createStudy,
			UpdateStudyMetadata updateStudyMetadata, LinkConnection linkConnection, LinkEHRConnection linkEHRConnection,
			SaveItemQueryMapping saveItemQueryMapping, DeleteItemQueryMapping deleteItemQueryMapping,
			ClearItemQueryMappings clearItemQueryMappings, DeleteStudy deleteStudy) {
		this.datawarehouseUpdated = datawarehouseUpdated;
		this.createStudy = createStudy;
		this.updateStudyMetadata = updateStudyMetadata;
		this.linkConnection = linkConnection;
		this.linkEHRConnection = linkEHRConnection;
		this.saveItemQueryMapping = saveItemQueryMapping;
		this.deleteItemQueryMapping = deleteItemQueryMapping;
		this.clearItemQueryMappings = clearItemQueryMappings;
		this.deleteStudy = deleteStudy;
	}
	//CHECKSTYLE:ON

	@PostMapping("datawarehouse")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void datawarehouseUpdated(@RequestParam(value = "timestamp",
												   required = false) String timestamp) {
		LOGGER.info("Datawarehouse updated at " + timestamp);
		datawarehouseUpdated.update();
	}

	@PostMapping("studies")
	public ResponseEntity createStudy(final @RequestParam("file") MultipartFile file)
			throws IOException, URISyntaxException {
		final CreateStudy.Request request = CreateStudy.Request.newBuilder()
				.withStudyODM(StudyODM.of(new String(file.getBytes())))
				.build();
		final CreateStudy.Response response = createStudy.create(request);
		final URI location = new URI("/ehr2edc/studies/" + response.getStudyId()
				.getId());
		return ResponseEntity.created(location)
				.build();
	}

	@PutMapping("studies/{studyId}/metadata")
	public void updateStudyMetadata(@PathVariable("studyId") String studyId,
			final @RequestParam("file") MultipartFile file) throws IOException {
		final UpdateStudyMetadata.Request request = UpdateStudyMetadata.Request.newBuilder()
				.withStudyId(StudyId.of(studyId))
				.withStudyODM(StudyODM.of(new String(file.getBytes())))
				.build();
		updateStudyMetadata.update(request);
	}

	@PostMapping("edc/connection")
	public void linkConnection(@RequestBody LinkConnection.Request request) {
		final LinkConnection.Request newRequest = LinkConnection.Request.newBuilder(request)
				.withEdcSystem(ofNullable(request.getEdcSystem()).orElse(EDCSystem.RAVE))
				.build();
		linkConnection.link(newRequest);
	}

	@PostMapping("ehr/connection")
	public void linkEHRConnection(@RequestBody LinkEHRConnection.Request request) {
		linkEHRConnection.link(request);
	}

	@PostMapping("studies/{studyId}/item-query-mappings")
	@ResponseStatus(HttpStatus.CREATED)
	public void createItemMappingBackwardsCompatibility(@PathVariable("studyId") String studyId,
			@RequestBody SaveItemQueryMapping.Request requestBody) {
		SaveItemQueryMapping.Request request = SaveItemQueryMapping.Request.newBuilder(requestBody)
				.withStudyId(StudyId.of(studyId))
				.build();
		saveItemQueryMapping.save(request);
	}

	@DeleteMapping("studies/{studyId}/item-query-mappings")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAllMappings(@PathVariable("studyId") String studyId) {
		ClearItemQueryMappings.Request request = ClearItemQueryMappings.Request.newBuilder()
				.withStudyId(StudyId.of(studyId))
				.build();
		clearItemQueryMappings.clear(request);
	}

	@DeleteMapping("studies/{studyId}/item-query-mappings/{itemId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteItemMapping(@PathVariable("studyId") String studyId, @PathVariable("itemId") String item) {
		DeleteItemQueryMapping.Request request = DeleteItemQueryMapping.Request.newBuilder()
				.withStudyId(StudyId.of(studyId))
				.withItemId(ItemDefinitionId.of(item))
				.build();
		deleteItemQueryMapping.delete(request);
	}

	@DeleteMapping("studies/{studyId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteStudy(@PathVariable("studyId") String studyId) {
		DeleteStudy.Request request = DeleteStudy.Request.newBuilder()
				.withStudyId(StudyId.of(studyId))
				.build();
		deleteStudy.delete(request);
	}
}
