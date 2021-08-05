package com.custodix.insite.local.ehr2edc.submitted;

import static com.custodix.insite.local.ehr2edc.submitted.SubmittedItemGroupObjectMother.aDefaultSubmittedItemGroupBuilder;

import java.util.Collections;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.populator.PopulatedForm;
import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.FormId;

public class SubmittedFormObjectMother {
	public static SubmittedForm.Builder aDefaultSubmittedFormBuilder() {
		return SubmittedForm.newBuilder()
				.withName("reviewedForm.name")
				.withFormDefinitionId(FormDefinitionId.of("formDefinitionId"))
				.withSubmittedItemGroups(Collections.singletonList(aDefaultSubmittedItemGroupBuilder().build()))
				.withPopulatedFormId(FormId.of("populatedFormId"));
	}

	public static SubmittedForm aSubmittedForm(PopulatedForm populatedForm) {
		return SubmittedForm.newBuilder()
				.withName(populatedForm.getName())
				.withFormDefinitionId(populatedForm.getFormDefinitionId())
				.withPopulatedFormId(populatedForm.getInstanceId())
				.withLocalLab(populatedForm.getLocalLab())
				.withSubmittedItemGroups(populatedForm.getItemGroups()
						.stream()
						.map(SubmittedItemGroupObjectMother::aSubmittedItemGroup)
						.collect(Collectors.toList()))
				.build();
	}
}
