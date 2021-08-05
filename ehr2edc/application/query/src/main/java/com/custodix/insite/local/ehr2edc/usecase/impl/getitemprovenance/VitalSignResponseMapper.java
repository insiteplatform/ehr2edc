package com.custodix.insite.local.ehr2edc.usecase.impl.getitemprovenance;

import java.util.ArrayList;
import java.util.List;

import com.custodix.insite.local.ehr2edc.provenance.model.*;
import com.custodix.insite.local.ehr2edc.query.GetItemProvenance;

class VitalSignResponseMapper implements ResponseMapper {
	private static final String LABEL_GROUP_CONCEPT = "concept";
	private static final String LABEL_GROUP_MEASUREMENT = "measurement";
	private static final String LABEL_ITEM_DATE = "date";
	private static final String LABEL_ITEM_CONCEPT_CODE = "code";
	private static final String LABEL_ITEM_CONCEPT_COMPONENT = "component";
	private static final String LABEL_ITEM_CONCEPT_LOCATION = "location code";
	private static final String LABEL_ITEM_CONCEPT_LATERALITY = "laterality code";
	private static final String LABEL_ITEM_CONCEPT_POSITION = "position code";
	private static final String LABEL_ITEM_MEASUREMENT_VALUE = "value";
	private static final String LABEL_ITEM_MEASUREMENT_UNIT = "unit";
	private static final String LABEL_ITEM_MEASUREMENT_LLN = "normal range lower limit";
	private static final String LABEL_ITEM_MEASUREMENT_ULN = "normal range upper limit";

	@Override
	public boolean supports(ProvenanceDataPoint provenanceDataPoint) {
		return provenanceDataPoint instanceof ProvenanceVitalSign;
	}

	@Override
	public GetItemProvenance.Response map(ProvenanceDataPoint provenanceDataPoint) {
		ProvenanceVitalSign vitalSign = (ProvenanceVitalSign) provenanceDataPoint;
		List<GetItemProvenance.ProvenanceGroup> groups = new ArrayList<>();
		addConceptGroup(groups, vitalSign);
		addMeasurementGroup(groups, vitalSign);
		List<GetItemProvenance.ProvenanceItem> items = new ArrayList<>();
		addItem(items, LABEL_ITEM_DATE, vitalSign.getEffectiveDateTime());
		return GetItemProvenance.Response.newBuilder()
				.withGroups(groups)
				.withItems(items)
				.build();
	}

	private void addConceptGroup(List<GetItemProvenance.ProvenanceGroup> groups, ProvenanceVitalSign vitalSign) {
		VitalSignConcept concept = vitalSign.getConcept();
		if (concept != null) {
			List<GetItemProvenance.ProvenanceItem> items = new ArrayList<>();
			ConceptCode conceptCode = concept.getConcept();
			if (conceptCode != null) {
				addItem(items, LABEL_ITEM_CONCEPT_CODE, conceptCode.getCode());
			}
			addItem(items, LABEL_ITEM_CONCEPT_COMPONENT, concept.getComponent());
			addItem(items, LABEL_ITEM_CONCEPT_LOCATION, concept.getLocation());
			addItem(items, LABEL_ITEM_CONCEPT_LATERALITY, concept.getLaterality());
			addItem(items, LABEL_ITEM_CONCEPT_POSITION, concept.getPosition());
			addGroup(groups, LABEL_GROUP_CONCEPT, items);
		}
	}

	private void addMeasurementGroup(List<GetItemProvenance.ProvenanceGroup> groups, ProvenanceVitalSign vitalSign) {
		Measurement measurement = vitalSign.getMeasurement();
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
