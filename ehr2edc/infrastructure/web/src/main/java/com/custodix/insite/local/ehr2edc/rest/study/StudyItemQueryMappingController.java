package com.custodix.insite.local.ehr2edc.rest.study;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;

import com.custodix.insite.local.ehr2edc.command.ClearItemQueryMappings;
import com.custodix.insite.local.ehr2edc.command.DeleteItemQueryMapping;
import com.custodix.insite.local.ehr2edc.command.SaveItemQueryMapping;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

/**
 * @deprecated use {@link com.custodix.insite.local.ehr2edc.rest.actuator.ActuatorManagementController} instead
 */
@Deprecated
@RestController
@RequestMapping("/ehr2edc")
public class StudyItemQueryMappingController {
	private final SaveItemQueryMapping saveItemQueryMapping;
	private final DeleteItemQueryMapping deleteItemQueryMapping;
	private final ClearItemQueryMappings clearItemQueryMappings;

	public StudyItemQueryMappingController(final SaveItemQueryMapping saveItemQueryMapping,
			DeleteItemQueryMapping deleteItemQueryMapping, ClearItemQueryMappings clearItemQueryMappings) {
		this.saveItemQueryMapping = saveItemQueryMapping;
		this.deleteItemQueryMapping = deleteItemQueryMapping;
		this.clearItemQueryMappings = clearItemQueryMappings;
	}

	@PostMapping("/studies/{studyId}/item-query-mappings")
	public void createItemMappingBackwardsCompatibility(@PathVariable("studyId") String studyId,
			@RequestBody SaveItemQueryMapping.Request requestBody, HttpServletResponse response) {
		SaveItemQueryMapping.Request request = SaveItemQueryMapping.Request.newBuilder(requestBody)
				.withStudyId(StudyId.of(studyId))
				.build();
		saveItemQueryMapping.save(request);
		response.setStatus(HttpServletResponse.SC_CREATED);
	}

	@DeleteMapping("/studies/{studyId}/item-query-mappings")
	public void deleteAllMappings(@PathVariable("studyId") String studyId, HttpServletResponse response) {
		ClearItemQueryMappings.Request request = ClearItemQueryMappings.Request.newBuilder()
				.withStudyId(StudyId.of(studyId))
				.build();
		clearItemQueryMappings.clear(request);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@DeleteMapping("/studies/{studyId}/item-query-mappings/{itemId}")
	public void deleteItemMapping(@PathVariable("studyId") String studyId, @PathVariable("itemId") String item,
			HttpServletResponse response) {
		DeleteItemQueryMapping.Request request = DeleteItemQueryMapping.Request.newBuilder()
				.withStudyId(StudyId.of(studyId))
				.withItemId(ItemDefinitionId.of(item))
				.build();
		deleteItemQueryMapping.delete(request);
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}
}