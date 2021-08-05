package com.custodix.insite.local.ehr2edc.jackson;

import com.custodix.insite.local.ehr2edc.jackson.mixin.projector.*;
import com.custodix.insite.local.ehr2edc.jackson.mixin.projector.common.*;
import com.custodix.insite.local.ehr2edc.jackson.mixin.projector.model.LabelMixin;
import com.custodix.insite.local.ehr2edc.jackson.mixin.projector.model.LabeledValueMixin;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.*;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.formitem.ProjectedValueToFormItem;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurement.MeasurementToValueInUnit;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.measurementnew.*;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.Label;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.model.LabeledValue;
import com.custodix.insite.local.ehr2edc.query.executor.common.projector.number.FormatNumber;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Projector;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.birthdate.DateOfBirthToAgeProjector;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.gender.GenderToStringProjector;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.LastLabValueProjector;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector.ProjectLabValue;
import com.custodix.insite.local.ehr2edc.query.executor.medication.projector.ProjectMedication;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector.ProjectVitalSignValue;
import com.fasterxml.jackson.databind.module.SimpleModule;

public final class ProjectionModule {
	private ProjectionModule() {
	}

	public static SimpleModule create() {
		SimpleModule module = new SimpleModule();
		module.setMixInAnnotation(Projector.class, ProjectorMixin.class);
		addModelMixins(module);
		addCommonProjectorMixins(module);
		addDemographicMixins(module);
		addLaboratoryProjectorMixins(module);
		addMedicationProjectorMixins(module);
		addVitalSignProjectorMixins(module);
		return module;
	}

	private static void addModelMixins(SimpleModule module) {
		module.setMixInAnnotation(LabeledValue.class, LabeledValueMixin.class);
		module.setMixInAnnotation(Label.class, LabelMixin.class);
	}

	private static void addCommonProjectorMixins(SimpleModule module) {
		module.setMixInAnnotation(DateToStringProjector.class, DateToStringProjectorMixin.class);
		module.setMixInAnnotation(MapToBooleanProjector.class, MapToBooleanProjectorMixin.class);
		module.setMixInAnnotation(MapToStringProjector.class, MapToStringProjectorMixin.class);
		module.setMixInAnnotation(MapToLabeledValue.class, MapToLabeledValueMixin.class);
		module.setMixInAnnotation(FormatNumber.class, FormatNumberMixin.class);
		module.setMixInAnnotation(MeasurementToValueInUnit.class, MeasurementToValueInUnitMixin.class);
		module.setMixInAnnotation(ConvertProjectedValue.class, ConvertProjectedValueMixin.class);
		module.setMixInAnnotation(ConvertProjectedValueComposite.class, ConvertProjectedValueCompositeMixin.class);
		module.setMixInAnnotation(ComposeProjectedValueProjection.class, ComposeProjectedValueProjectionMixin.class);
		module.setMixInAnnotation(SetProjectedValueUnit.class, SetProjectedValueUnitMixin.class);
		module.setMixInAnnotation(FormatProjectedValueNumber.class, FormatProjectedValueNumberMixin.class);
		module.setMixInAnnotation(MapProjectedValue.class, MapProjectedValueMixin.class);
		module.setMixInAnnotation(ProjectedValueToFormItem.class, ProjectedValueToFormItemMixin.class);
		module.setMixInAnnotation(MapUnitToCommonModel.class, MapUnitToCommonModelMixin.class);
		module.setMixInAnnotation(FixedStringProjector.class, FixedStringProjectorMixin.class);
	}

	private static void addDemographicMixins(SimpleModule module) {
		module.setMixInAnnotation(DateOfBirthToAgeProjector.class, DateOfBirthToAgeProjectorMixin.class);
		module.setMixInAnnotation(GenderToStringProjector.class, GenderToStringProjectorMixin.class);
	}

	private static void addLaboratoryProjectorMixins(SimpleModule module) {
		module.setMixInAnnotation(LastLabValueProjector.class, LastLabValueProjectorMixin.class);
		module.setMixInAnnotation(ProjectLabValue.class, ProjectLabValueMixin.class);
	}

	private static void addMedicationProjectorMixins(SimpleModule module) {
		module.setMixInAnnotation(ProjectMedication.class, ProjectMedicationMixin.class);
	}

	private static void addVitalSignProjectorMixins(SimpleModule module) {
		module.setMixInAnnotation(ProjectVitalSignValue.class, ProjectVitalSignValueMixin.class);
	}
}