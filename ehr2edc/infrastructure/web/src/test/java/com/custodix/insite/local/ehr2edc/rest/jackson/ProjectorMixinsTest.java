package com.custodix.insite.local.ehr2edc.rest.jackson;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import org.junit.Before;
import org.junit.Test;

import com.custodix.insite.local.ehr2edc.query.executor.common.projector.*;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.formitem.ProjectedValueToFormItem;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement.*;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.*;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.Label;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.number.FormatNumber;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.birthdate.*;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.deathdate.DateOfDeathProjector;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.Gender;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.GenderProjector;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.GenderToSDTMCodeProjector;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.GenderToStringProjector;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.vitalstatus.VitalStatusProjector;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.*;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.concept.*;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.interpretation.InterpretationToIsPositiveBooleanProjector;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.interpretation.InterpretationToOriginalProjector;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.interpretation.LabValueToInterpretationProjector;
import com.custodix.insite.local.ehr2edc.query.executor.medication.projector.*;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.*;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.concept.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProjectorMixinsTest {

	private ObjectMapper objectMapper;

	@Before
	public void setUp() {
		objectMapper = new ObjectMapper();
		new JacksonWebConfiguration(objectMapper);
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
	}

	@Test
	public void testMappingOfDateOfBirthToAgeProjector() throws Exception {
		testProjector(new DateOfBirthToAgeProjector(AgeUnit.YEARS), "dateOfBirthToAge");
	}

	@Test
	public void testMappingOfDateOfBirthProjector() throws Exception {
		testProjector(new DateOfBirthProjector(), "dateOfBirth");
	}

	@Test
	public void testMappingOfDateOfDeathProjector() throws Exception {
		testProjector(new DateOfDeathProjector(), "dateOfDeath");
	}

	@Test
	public void testMappingOfGenderProjector() throws Exception {
		testProjector(new GenderProjector(), "gender");
	}

	@Test
	public void testMappingOfGenderToStringProjector() throws Exception {
		Map<Gender, String> genderMapping = new HashMap<>();
		genderMapping.put(Gender.MALE, "M");
		genderMapping.put(Gender.FEMALE, "V");
		genderMapping.put(Gender.UNKNOWN, "X");
		testProjector(new GenderToStringProjector(genderMapping), "genderToString");
	}

	@Test
	public void testMappingOfVitalStatusProjector() throws Exception {
		testProjector(new VitalStatusProjector(), "vitalStatus");
	}

	@Test
	public void testMappingOfDateTimeISO8601Projector() throws Exception {
		testProjector(new DateTimeISO8601Projector(), "dateTimeISO8601");
	}

	@Test
	public void testMappingOfDateToStringProjector() throws Exception {
		testProjector(new DateToStringProjector("dd-MM-yyyy"), "dateToString");
	}

	@Test
	public void testMappingOfMapToBooleanProjector() throws Exception {
		testProjector(new MapToBooleanProjector(emptyMap()), "mapToBoolean");
	}

	@Test
	public void testMappingOfMapToStringProjector() throws Exception {
		testProjector(new MapToStringProjector(emptyMap()), "mapToString");
	}

	@Test
	public void mapToLabeledValue() throws Exception {
		Map<String, List<Label>> mapping = new HashMap<>();
		mapping.put("C25301", Arrays.asList(new Label(Locale.ENGLISH, "DAYS"), new Label(Locale.FRENCH, "JOURS")));
		testProjector(new MapToLabeledValue(mapping), "mapToLabeledValue");
	}

	@Test
	public void testMappingOfBooleanToNYProjector() throws Exception {
		testProjector(new BooleanToNYProjector(), "booleanToNY");
	}

	@Test
	public void testMappingOfRoundProjector() throws Exception {
		testProjector(new RoundingProjector(), "round");
	}

	@Test
	public void testMappingOfAgeToNumericalProjector() throws Exception {
		testProjector(new AgeToNumericalProjector(), "ageToNumerical");
	}

	@Test
	public void testMappingOfAgeToUnitProjector() throws Exception {
		testProjector(new AgeToUnitProjector(), "ageToUnit");
	}

	@Test
	public void testMappingOfGenderToSDTMCodeProjector() throws Exception {
		testProjector(new GenderToSDTMCodeProjector(), "genderToSDTMCode");
	}

	@Test
	public void testMappingOfConceptProjector() throws Exception {
		testProjector(new LabValueToLabConceptProjector(), "labValueToLabConcept");
	}

	@Test
	public void testMappingOfConceptCodeProjector() throws Exception {
		testProjector(new LabConceptToCodeProjector(), "labConceptToCode");
	}

	@Test
	public void testMappingOfConceptComponentProjector() throws Exception {
		testProjector(new LabConceptToComponentProjector(), "labConceptToComponent");
	}

	@Test
	public void testMappingOfConceptFastingStatusProjector() throws Exception {
		testProjector(new LabConceptToFastingBooleanProjector(), "labConceptToFastingBoolean");
	}

	@Test
	public void testMappingOfConceptMethodProjector() throws Exception {
		testProjector(new LabConceptToMethodProjector(), "labConceptToMethod");
	}

	@Test
	public void testMappingOfConceptSpecimenProjector() throws Exception {
		testProjector(new LabConceptToSpecimenProjector(), "labConceptToSpecimen");
	}

	@Test
	public void testMappingOfMeasurementProjector() throws Exception {
		testProjector(new LabValueToMeasurementProjector(), "labValueToMeasurement");
	}

	@Test
	public void labValueToProjectedValue() throws Exception {
		testProjector(new LabValueToProjectedValue(), "labValueToProjectedValue");
	}

	@Test
	public void projectLabValue() throws Exception {
		ProjectLabValue projector = testProjector(new ProjectLabValue(LabValueField.CODE), "projectLabValue");
		assertThat(getField(projector)).isEqualTo(LabValueField.CODE);
	}

	@Test
	public void projectLabValueFromJson() throws Exception {
		ProjectLabValue projector = objectMapper.readValue("{\"type\":\"projectLabValue\", \"field\": \"CODE\"}",
				ProjectLabValue.class);
		assertThat(getField(projector)).isEqualTo(LabValueField.CODE);
	}

	@Test
	public void projectMedication() throws Exception {
		ProjectMedication projector = testProjector(new ProjectMedication(MedicationField.CODE), "projectMedication");
		assertThat(getField(projector)).isEqualTo(MedicationField.CODE);
	}

	@Test
	public void projectMedicationFromJson() throws Exception {
		ProjectMedication projector = objectMapper.readValue("{\"type\":\"projectMedication\", \"field\": \"CODE\"}",
				ProjectMedication.class);
		assertThat(getField(projector)).isEqualTo(MedicationField.CODE);
	}

	@Test
	public void testMappingOfMeasurementValueProjector() throws Exception {
		testProjector(new MeasurementToValueProjector(), "measurementToValue");
	}

	@Test
	public void testMappingOfMeasurementValueAndUnitProjector() throws Exception {
		testProjector(new GetMeasurementValueAndUnit(), "measurementToValueAndUnit");
	}

	@Test
	public void convertProjectedValue() throws Exception {
		testProjector(new ConvertProjectedValue("mg"), "convertProjectedValue");
	}

	@Test
	public void setProjectedValueUnit() throws Exception {
		testProjector(new SetProjectedValueUnit("13899.CM/IN.cm"), "setProjectedValueUnit");
	}

	@Test
	public void formatProjectedValueNumber() throws Exception {
		testProjector(new FormatProjectedValueNumber(2, null), "formatProjectedValueNumber");
	}

	@Test
	public void mapProjectedValue() throws Exception {
		Map<Object, String> mapping = new HashMap<>();
		mapping.put("code1", "code-1");
		mapping.put("code2", "code-2");
		testProjector(new MapProjectedValue(mapping, ProjectedValueField.CODE, false), "map");
	}

	@Test
	public void loadMapProjectedValueFromJson() throws Exception {
		MapProjectedValue projector = objectMapper.readValue(
				"{\"type\":\"mapProjectedValue\", \"field\": \"UNIT\", \"projectMissing\": true, \"mapping\": {\n"
						+ "        \"46418-0\": \"INR\",\n" + "        \"52129-4\": \"INR\",\n"
						+ "        \"34714-6\": \"INR\",\n" + "        \"38875-1\": \"INR\",\n"
						+ "        \"6301-6\": \"INR\"\n" + "      }}", MapProjectedValue.class);
		ProjectedValueField field = (ProjectedValueField)getField(projector);
		boolean projectMissing = isProjectMissing(projector);
		Map<String, String> mapping = getMapping(projector);
		assertThat(field).isEqualTo(ProjectedValueField.UNIT);
		assertThat(projectMissing).isTrue();
		assertThat(mapping).isEqualTo(new HashMap<String, String>() {{
			put("46418-0", "INR");
			put("52129-4", "INR");
			put("34714-6", "INR");
			put("38875-1", "INR");
			put("6301-6", "INR");
		}});
	}

	@Test
	public void loadMapUnitToCommonModel() throws Exception {
		MapUnitToCommonModel projector = testProjector(new MapUnitToCommonModel(ProjectedValueField.CODE, true), "unitToCommonModel");
		ProjectedValueField field = (ProjectedValueField)getField(projector);
		boolean projectMissing = isProjectMissing(projector);
		assertThat(field).isEqualTo(ProjectedValueField.CODE);
		assertThat(projectMissing).isTrue();
	}

	@Test
	public void testMapUnitToCommonModel() throws Exception {
		MapUnitToCommonModel projector = objectMapper.readValue(
				"{\"type\":\"unitToCommonModel\", \"field\": \"UNIT\", \"projectMissing\": true}", MapUnitToCommonModel.class);
		ProjectedValueField field = (ProjectedValueField)getField(projector);
		boolean projectMissing = isProjectMissing(projector);
		assertThat(field).isEqualTo(ProjectedValueField.UNIT);
		assertThat(projectMissing).isTrue();
	}

	@Test
	public void convertProjectedValueComposite() throws Exception {
		Map<List<String>, NumericalProjectedValueProjector> projectors = new HashMap<>();
		projectors.put(Arrays.asList("123", "456"), new ConvertProjectedValue("cm"));
		projectors.put(Collections.singletonList("789"), new ConvertProjectedValue("kg"));
		testProjector(new ConvertProjectedValueComposite(projectors), "convertProjectedValueComposite");
	}

	@Test
	public void composeProjectedValueProjection() throws Exception {
		Projector projector = new DateToStringProjector("dd-MM-yyyy");
		testProjector(new ComposeProjectedValueProjection(projector), "composeProjectedValueProjection");
	}

	@Test
	public void testMappingOfMeasurementValueLowerLimitProjector() throws Exception {
		testProjector(new MeasurementToLowerLimitProjector(), "measurementToLowerLimit");
	}

	@Test
	public void testMappingOfMeasurementValueUpperLimitProjector() throws Exception {
		testProjector(new MeasurementToUpperLimitProjector(), "measurementToUpperLimit");
	}

	@Test
	public void testMappingOfMeasurementUnitProjector() throws Exception {
		testProjector(new MeasurementToUnitProjector(), "measurementToUnit");
	}

	@Test
	public void testMappingOfInterpretationProjector() throws Exception {
		testProjector(new LabValueToInterpretationProjector(), "labValueToInterpretation");
	}

	@Test
	public void testMappingOfInterpretationIsPositiveProjector() throws Exception {
		testProjector(new InterpretationToIsPositiveBooleanProjector(), "interpretationToIsPositiveBoolean");
	}

	@Test
	public void testMappingOfInterpretationOriginalProjector() throws Exception {
		testProjector(new InterpretationToOriginalProjector(), "interpretationToOriginal");
	}

	@Test
	public void testMappingOfSubjectIdProjector() throws Exception {
		testProjector(new SubjectIdProjector(), "subjectId");
	}

	@Test
	public void testMappingOfGetConsentDate() throws Exception {
		testProjector(new GetConsentDate(), "getConsentDate");
	}

	@Test
	public void testMappingOfLastLabValueProjector() throws Exception {
		testProjector(new LastLabValueProjector(), "lastLabValue");
	}

	@Test
	public void testMappingOfStartDateProjector() throws Exception {
		testProjector(new LabValueToStartDateProjector(), "labValueToStartDate");
	}

	@Test
	public void testMappingOfEndDateProjector() throws Exception {
		testProjector(new LabValueToEndDateProjector(), "labValueToEndDate");
	}

	@Test
	public void lastVitalSignProjector() throws Exception {
		testProjector(new LastVitalSignProjector(), "lastVitalSign");
	}

	@Test
	public void vitalSignToProjectedValue() throws Exception {
		testProjector(new VitalSignToProjectedValue(), "vitalSignToProjectedValue");
	}

	@Test
	public void projectVitalSignValue() throws Exception {
		testProjector(new ProjectVitalSignValue(VitalSignField.CODE), "projectVitalSignValue");
	}

	@Test
	public void vitalSignToVitalSignConceptProjector() throws Exception {
		testProjector(new VitalSignToVitalSignConceptProjector(), "vitalSignToVitalSignConcept");
	}

	@Test
	public void vitalSignConceptToCodeProjector() throws Exception {
		testProjector(new VitalSignConceptToCodeProjector(), "vitalSignConceptToCode");
	}

	@Test
	public void vitalSignConceptToPositionProjector() throws Exception {
		testProjector(new VitalSignConceptToPositionProjector(), "vitalSignConceptToPosition");
	}

	@Test
	public void vitalSignConceptToLocationProjector() throws Exception {
		testProjector(new VitalSignConceptToLocationProjector(), "vitalSignConceptToLocation");
	}

	@Test
	public void vitalSignConceptToLateralityProjector() throws Exception {
		testProjector(new VitalSignConceptToLateralityProjector(), "vitalSignConceptToLaterality");
	}

	@Test
	public void vitalSignToMeasurementProjector() throws Exception {
		testProjector(new VitalSignToMeasurementProjector(), "vitalSignToMeasurement");
	}

	@Test
	public void vitalSignToDateProjector() throws Exception {
		testProjector(new VitalSignToDateProjector(), "vitalSignToDate");
	}

	@Test
	public void doNotOuputBooleanIfFalse() throws Exception {
		testProjector(new DoNotOutputBooleanIfFalse(), "doNotOutputBooleanIfFalse");
	}

	@Test
	public void booleanToInteger() throws Exception {
		testProjector(new BooleanToIntegerProjector(), "booleanToInteger");
	}

	@Test
	public void negateBoolean() throws Exception {
		testProjector(new NegateBoolean(), "negateBoolean");
	}

	@Test
	public void isVitalSignAvailable() throws Exception {
		testProjector(new IsVitalSignAvailable(), "isVitalSignAvailable");
	}

	@Test
	public void lastMedicationProjector() throws Exception {
		testProjector(new LastMedicationProjector(), "lastMedication");
	}

	@Test
	public void testMappingOfMedicationStartDateProjector() throws Exception {
		testProjector(new MedicationToStartDateProjector(), "medicationToStartDate");
	}

	@Test
	public void testMappingOfMedicationEndDateProjector() throws Exception {
		testProjector(new MedicationToEndDateProjector(), "medicationToEndDate");
	}

	@Test
	public void testMappingOfMedicationNameProjector() throws Exception {
		testProjector(new MedicationToNameProjector(), "medicationToName");
	}

	@Test
	public void testMappingOfExtractDosageFromMedication() throws Exception {
		testProjector(new ExtractDosageFromMedication(), "extractDosageFromMedication");
	}

	@Test
	public void testMappingOfExtractUnitFromDosage() throws Exception {
		testProjector(new ExtractUnitFromDosage(), "extractUnitFromDosage");
	}

	@Test
	public void testMappingOfExtractValueFromDosage() throws Exception {
		testProjector(new ExtractValueFromDosage(), "extractValueFromDosage");
	}

	@Test
	public void medicationToAdministrationRoute() throws Exception {
		testProjector(new MedicationToAdministrationRoute(), "medicationToAdministrationRoute");
	}

	@Test
	public void medicationToDoseForm() throws Exception {
		testProjector(new MedicationToDoseForm(), "medicationToDoseForm");
	}

	@Test
	public void medicationToDosingFrequency() throws Exception {
		testProjector(new MedicationToDosingFrequency(), "medicationToDosingFrequency");
	}

	@Test
	public void testFormatNumber() throws Exception {
		int maximumFractionDigits = 2;
		Character decimalSeparator = null;
		FormatNumber result = testProjector(new FormatNumber(maximumFractionDigits, decimalSeparator), "formatNumber");
		assertThat(getMaximumFractionDigits(result)).isEqualTo(maximumFractionDigits);
		assertThat(getDecimalSeparator(result)).isEqualTo(FormatNumber.DEFAULT_DECIMAL_SEPARATOR);
	}

	private Integer getMaximumFractionDigits(FormatNumber result) throws Exception {
		Field f = FormatNumber.class.getDeclaredField("maximumFractionDigits");
		f.setAccessible(true);
		return (Integer) f.get(result);
	}

	private Character getDecimalSeparator(FormatNumber result) throws Exception {
		Field f = FormatNumber.class.getDeclaredField("decimalSeparator");
		f.setAccessible(true);
		return (Character) f.get(result);
	}

	@Test
	public void projectedValueToFormItem() throws Exception {
		Map<Object, String> indexMapping = new HashMap<>();
		indexMapping.put("A", "1");
		indexMapping.put("B", "2");
		indexMapping.put("C", "3");
		ProjectedValueToFormItem result = testProjector(
				new ProjectedValueToFormItem(indexMapping, ProjectedValueField.VALUE,
						ProjectedValueToFormItem.UnitMapping.EXPORT, false, true), "projectedValueToFormItem");
		assertThat(getIndexMapping(result)).isEqualTo(indexMapping);
		assertThat(getKey(result)).isEqualTo(true);
	}

	@Test
	public void testFixedStringProjector() throws Exception {
		String string = "a fixed string";

		testProjector(new FixedStringProjector(string), "fixedString");
	}

	private Map<String, String> getIndexMapping(Object result) throws Exception {
		Field f = result.getClass().getDeclaredField("indexMapping");
		f.setAccessible(true);
		return (Map<String, String>) f.get(result);
	}

	private Map<String, String> getMapping(Object result) throws Exception {
		Field f = result.getClass().getDeclaredField("mapping");
		f.setAccessible(true);
		return (Map<String, String>) f.get(result);
	}

	private Object getField(Object projector) throws Exception {
		Field f = projector.getClass().getDeclaredField("field");
		f.setAccessible(true);
		return f.get(projector);
	}

	private boolean isProjectMissing(Object result) throws Exception {
		Field f = result.getClass().getDeclaredField("projectMissing");
		f.setAccessible(true);
		return (Boolean) f.get(result);
	}

	private boolean getKey(ProjectedValueToFormItem result) throws Exception {
		Field f = ProjectedValueToFormItem.class.getDeclaredField("key");
		f.setAccessible(true);
		return (boolean) f.get(result);
	}

	private <T> T testProjector(T projector, String type) throws Exception {
		String jsonTypeFormat = String.format("\"type\":\"%s\"", type);
		final String serialisedProjector = objectMapper.writeValueAsString(projector);
		assertThat(serialisedProjector
				.contains(jsonTypeFormat)).isTrue();
		return assertSerializationBothWays(projector);
	}

	private <T> T assertSerializationBothWays(T o) {
		try {
			final T result = (T) objectMapper.readValue(objectMapper.writeValueAsString(o), o.getClass());
			assertThat(result).isEqualToComparingFieldByFieldRecursively(o);
			return result;
		} catch (IOException e) {
			throw new AssertionError(e.getMessage());
		}
	}
}