package com.custodix.insite.local.ehr2edc.usecase.impl.getsubmitteditemprovenance;

import java.util.ArrayList;
import java.util.List;

import com.custodix.insite.local.ehr2edc.provenance.model.*;
import com.custodix.insite.local.ehr2edc.query.GetSubmittedItemProvenance;

class MedicationResponseMapper implements ResponseMapper {
	private static final String LABEL_GROUP_CONCEPT = "concept";
	private static final String LABEL_GROUP_DOSAGE = "dosage";
	private static final String LABEL_ITEM_START_DATE = "start date";
	private static final String LABEL_ITEM_END_DATE = "end date";
	private static final String LABEL_ITEM_ADMINISTRATION_ROUTE = "administration route";
	private static final String LABEL_ITEM_DOSE_FORM = "dose form";
	private static final String LABEL_ITEM_DOSING_FREQUENCY = "dosing frequency";
	private static final String LABEL_ITEM_CONCEPT_NAME = "name";
	private static final String LABEL_ITEM_CONCEPT_CODE = "code";
	private static final String LABEL_ITEM_DOSAGE_VALUE = "value";
	private static final String LABEL_ITEM_DOSAGE_UNIT = "unit";
	private static final String LABEL_EVENT_TYPE = "event type";

	@Override
	public boolean supports(ProvenanceDataPoint submittedProvenanceDataPoint) {
		return submittedProvenanceDataPoint instanceof ProvenanceMedication;
	}

	@Override
	public GetSubmittedItemProvenance.Response map(ProvenanceDataPoint submittedProvenanceDataPoint) {
		ProvenanceMedication medication = (ProvenanceMedication) submittedProvenanceDataPoint;
		List<GetSubmittedItemProvenance.ProvenanceGroup> groups = new ArrayList<>();
		addConceptGroup(groups, medication);
		addDosageGroup(groups, medication);
		List<GetSubmittedItemProvenance.ProvenanceItem> items = new ArrayList<>();
		addItem(items, LABEL_ITEM_START_DATE, medication.getStartDate());
		addItem(items, LABEL_ITEM_END_DATE, medication.getEndDate());
		addItem(items, LABEL_ITEM_ADMINISTRATION_ROUTE, medication.getAdministrationRoute());
		addItem(items, LABEL_ITEM_DOSE_FORM, medication.getDoseForm());
		addItem(items, LABEL_ITEM_DOSING_FREQUENCY, medication.getDosingFrequency());
		addItem(items, LABEL_EVENT_TYPE, medication.getEventType());
		return GetSubmittedItemProvenance.Response.newBuilder()
				.withGroups(groups)
				.withItems(items)
				.build();
	}

	private void addConceptGroup(List<GetSubmittedItemProvenance.ProvenanceGroup> groups,
			ProvenanceMedication medication) {
		MedicationConcept concept = medication.getConcept();
		if (concept != null) {
			List<GetSubmittedItemProvenance.ProvenanceItem> items = new ArrayList<>();
			addItem(items, LABEL_ITEM_CONCEPT_NAME, concept.getName());
			ConceptCode conceptCode = concept.getConcept();
			if (conceptCode != null) {
				addItem(items, LABEL_ITEM_CONCEPT_CODE, conceptCode.getCode());
			}
			addGroup(groups, LABEL_GROUP_CONCEPT, items);
		}
	}

	private void addDosageGroup(List<GetSubmittedItemProvenance.ProvenanceGroup> groups,
			ProvenanceMedication medication) {
		Dosage dosage = medication.getDosage();
		if (dosage != null) {
			List<GetSubmittedItemProvenance.ProvenanceItem> items = new ArrayList<>();
			addItem(items, LABEL_ITEM_DOSAGE_VALUE, dosage.getValue());
			addItem(items, LABEL_ITEM_DOSAGE_UNIT, dosage.getUnit());
			addGroup(groups, LABEL_GROUP_DOSAGE, items);
		}
	}
}
