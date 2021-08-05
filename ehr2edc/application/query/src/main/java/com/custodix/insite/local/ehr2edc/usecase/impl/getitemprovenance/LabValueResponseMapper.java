package com.custodix.insite.local.ehr2edc.usecase.impl.getitemprovenance;

import java.util.ArrayList;
import java.util.List;

import com.custodix.insite.local.ehr2edc.provenance.model.*;
import com.custodix.insite.local.ehr2edc.query.GetItemProvenance;

class LabValueResponseMapper implements ResponseMapper {
	private static final String LABEL_GROUP_CONCEPT = "concept";
	private static final String LABEL_GROUP_MEASUREMENT = "measurement";
	private static final String LABEL_ITEM_START_DATE = "start date";
	private static final String LABEL_ITEM_END_DATE = "end date";
	private static final String LABEL_ITEM_VENDOR = "vendor";
	private static final String LABEL_ITEM_CONCEPT_CODE = "code";
	private static final String LABEL_ITEM_CONCEPT_COMPONENT = "component";
	private static final String LABEL_ITEM_CONCEPT_METHOD = "method";
	private static final String LABEL_ITEM_CONCEPT_FASTING_STATUS = "fasting status";
	private static final String LABEL_ITEM_CONCEPT_SPECIMEN = "specimen";
	private static final String LABEL_ITEM_MEASUREMENT_VALUE = "value";
	private static final String LABEL_ITEM_MEASUREMENT_UNIT = "unit";
	private static final String LABEL_ITEM_MEASUREMENT_LLN = "normal range lower limit";
	private static final String LABEL_ITEM_MEASUREMENT_ULN = "normal range upper limit";

	@Override
	public boolean supports(ProvenanceDataPoint provenanceDataPoint) {
		return provenanceDataPoint instanceof ProvenanceLabValue;
	}

	@Override
	public GetItemProvenance.Response map(ProvenanceDataPoint provenanceDataPoint) {
		ProvenanceLabValue labValue = (ProvenanceLabValue) provenanceDataPoint;
		List<GetItemProvenance.ProvenanceGroup> groups = new ArrayList<>();
		addConceptGroup(groups, labValue);
		addMeasurementGroup(groups, labValue);
		List<GetItemProvenance.ProvenanceItem> items = new ArrayList<>();
		addItem(items, LABEL_ITEM_START_DATE, labValue.getStartDate());
		addItem(items, LABEL_ITEM_END_DATE, labValue.getEndDate());
		addItem(items, LABEL_ITEM_VENDOR, labValue.getVendor());
		return GetItemProvenance.Response.newBuilder()
				.withGroups(groups)
				.withItems(items)
				.build();
	}

	private void addConceptGroup(List<GetItemProvenance.ProvenanceGroup> groups, ProvenanceLabValue labValue) {
		LabConcept concept = labValue.getLabConcept();
		if (concept != null) {
			List<GetItemProvenance.ProvenanceItem> items = new ArrayList<>();
			ConceptCode conceptCode = concept.getConcept();
			if (conceptCode != null) {
				addItem(items, LABEL_ITEM_CONCEPT_CODE, conceptCode.getCode());
			}
			addItem(items, LABEL_ITEM_CONCEPT_COMPONENT, concept.getComponent());
			addItem(items, LABEL_ITEM_CONCEPT_METHOD, concept.getMethod());
			addItem(items, LABEL_ITEM_CONCEPT_FASTING_STATUS, concept.getFastingStatus());
			addItem(items, LABEL_ITEM_CONCEPT_SPECIMEN, concept.getSpecimen());
			addGroup(groups, LABEL_GROUP_CONCEPT, items);
		}
	}

	private void addMeasurementGroup(List<GetItemProvenance.ProvenanceGroup> groups, ProvenanceLabValue labValue) {
		Measurement measurement = labValue.getQuantitativeResult();
		if (measurement != null) {
			List<GetItemProvenance.ProvenanceItem> items = new ArrayList<>();
			addItem(items, LABEL_ITEM_MEASUREMENT_VALUE, measurement.getValue());
			addItem(items, LABEL_ITEM_MEASUREMENT_UNIT, measurement.getUnit());
			addItem(items, LABEL_ITEM_MEASUREMENT_LLN, measurement.getLowerLimit());
			addItem(items, LABEL_ITEM_MEASUREMENT_ULN, measurement.getUpperLimit());
			addGroup(groups, LABEL_GROUP_MEASUREMENT, items);
		}
	}
}
