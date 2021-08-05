package com.custodix.insite.local.ehr2edc.query.populator;

import com.custodix.insite.local.ehr2edc.metadata.model.*;
import com.custodix.insite.local.ehr2edc.vocabulary.FormDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.ItemGroupDefinitionId;
import com.custodix.insite.local.ehr2edc.vocabulary.LabName;

import java.util.Arrays;

public class FormFactory {
	public FormDefinition aFormWithDemographicsItemGroup() {
		return aFormDefinition("DM_DOB", aDemographicsItemGroup());
	}

	public FormDefinition aFormWithGlucoseItemGroup() {
		return aFormDefinition("FORM_GLUC", aGlucoseItemGroup());
	}

	public FormDefinition aFormWithHeightItemGroup() {
		return aFormDefinition("FORM_HEIGHT", aHeightItemGroup());
	}

	public FormDefinition aFormWithDemographicsItemGroupAndGlucoseItemGroup() {
		return aFormDefinition("FORM_DM_GLUC", aDemographicsItemGroup(), aGlucoseItemGroup());
	}

	public FormDefinition aFormWithRepeatingItemGroup(ItemGroupDefinition o) {
		return aFormDefinition("FORM_LOG_LINE", o);
	}

	public FormDefinition aFormWithDemographicsItemGroupAndRepeatingItemGroup() {
		return aFormDefinition("FORM_DM_LOG_LINE", aDemographicsItemGroup(), aRepeatingLabItemGroup());
	}

	public FormDefinition aFormWithLocalLabReference() {
		return aFormDefinitionWithLocalLab("FORM_LOCAL_LAB", LabName.of("LAB_1"), aDemographicsItemGroup());
	}

	private static ItemGroupDefinition aDemographicsItemGroup() {
		ItemDefinition age = anItem("DM_DOB.AGE");
		ItemDefinition ageUnits = anItem("DM_DOB.AGEU");
		ItemDefinition ethnicity = anItem("DM_DOB.ETHNIC");
		ItemDefinition sex = anItem("DM_DOB.SEX");
		return aNonRepeatingItemGroup("DM_DOB", age, ageUnits, ethnicity, sex);
	}

	private static ItemGroupDefinition aGlucoseItemGroup() {
		ItemDefinition loinc = anItem("LB_GLUC.LBLOINC");
		ItemDefinition value = anItem("LB_GLUC.LBORRES");
		ItemDefinition unit = anItem("LB_GLUC.LBORRESU");
		ItemDefinition fasting = anItem("LB_GLUC.LBFAST");
		return aNonRepeatingItemGroup("LB_GLUC", loinc, value, unit, fasting);
	}

	private static ItemGroupDefinition aHeightItemGroup() {
		ItemDefinition value = anItem("VS_HEIGHT.HEIGHT");
		ItemDefinition unit = anItem("VS_HEIGHT.HEIGHTU");
		return aNonRepeatingItemGroup("VS_HEIGHT", value, unit);
	}

	public static ItemGroupDefinition aRepeatingVitalSignItemGroup() {
		ItemDefinition medName = anItem("LOG_LINE.VTS");
		return aRepeatingItemGroup("LOG_LINE", medName);
	}

	public static ItemGroupDefinition aRepeatingMedicationItemGroup() {
		ItemDefinition medName = anItem("LOG_LINE.CMTRT");
		return aRepeatingItemGroup("LOG_LINE", medName);
	}

	public static ItemGroupDefinition aRepeatingLabItemGroup() {
		ItemDefinition loinc = anItem("LOG_LINE.LBLOINC");
		ItemDefinition value = anItem("LOG_LINE.LBORRES");
		ItemDefinition unit = anItem("LOG_LINE.LBORRESU");
		return aRepeatingItemGroup("LOG_LINE", loinc, value, unit);
	}

	private static ItemGroupDefinition aNonRepeatingItemGroup(String itemGroupDefinitionId,
			ItemDefinition... itemDefinitions) {
		return ItemGroupDefinitionObjectMother.aDefaultItemGroupDefinitionBuilder()
				.withId(ItemGroupDefinitionId.of(itemGroupDefinitionId))
				.withItemDefinitions(Arrays.asList(itemDefinitions))
				.build();
	}

	private static ItemGroupDefinition aRepeatingItemGroup(String itemGroupDefinitionId,
			ItemDefinition... itemDefinitions) {
		return ItemGroupDefinitionObjectMother.aRepeatingtItemGroupDefinitionBuilder()
				.withId(ItemGroupDefinitionId.of(itemGroupDefinitionId))
				.withItemDefinitions(Arrays.asList(itemDefinitions))
				.build();
	}

	private FormDefinition aFormDefinition(String formDefinitionId, ItemGroupDefinition... itemGroupDefinitions) {
		return FormDefinitionObjectMother.aDefaultFormDefinitionBuilder()
				.withId(FormDefinitionId.of(formDefinitionId))
				.withItemGroupDefinitions(Arrays.asList(itemGroupDefinitions))
				.build();
	}

	private FormDefinition aFormDefinitionWithLocalLab(String formDefinitionId, LabName localLab, ItemGroupDefinition... itemGroupDefinitions) {
		return FormDefinitionObjectMother.aDefaultFormDefinitionBuilder()
				.withId(FormDefinitionId.of(formDefinitionId))
				.withItemGroupDefinitions(Arrays.asList(itemGroupDefinitions))
				.withLocalLab(localLab)
				.build();
	}

	private static ItemDefinition anItem(String id) {
		return ItemDefinitionObjectMother.aDefaultItemBuilder()
				.withId(ItemDefinitionId.of(id))
				.build();
	}
}
