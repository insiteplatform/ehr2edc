package com.custodix.insite.local.ehr2edc.query.executor.medication.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Dosage;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.MedicationConcept;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public enum MedicationField {
	SUBJECT {
		@Override
		public Optional<Object> getValue(Medication med) {
			return Optional.ofNullable(med.getSubjectId())
					.map(SubjectId::getId);
		}
	},
	START_DATE {
		@Override
		public Optional<Object> getValue(Medication med) {
			return Optional.ofNullable(med.getStartDate());
		}
	},
	END_DATE {
		@Override
		public Optional<Object> getValue(Medication med) {
			return Optional.ofNullable(med.getEndDate());
		}
	},
	CODE {
		@Override
		public Optional<Object> getValue(Medication med) {
			return Optional.ofNullable(med.getMedicationConcept())
					.map(MedicationConcept::getConcept)
					.map(ConceptCode::getCode);
		}
	},
	NAME {
		@Override
		public Optional<Object> getValue(Medication med) {
			return Optional.ofNullable(med.getMedicationConcept())
					.map(MedicationConcept::getName);
		}
	},
	DOSAGE_VALUE {
		@Override
		public Optional<Object> getValue(Medication med) {
			return Optional.ofNullable(med.getDosage())
					.map(Dosage::getValue);
		}
	},
	DOSAGE_UNIT {
		@Override
		public Optional<Object> getValue(Medication med) {
			return Optional.ofNullable(med.getDosage())
					.map(Dosage::getUnit);
		}
	},
	ADMINISTRATION_ROUTE {
		@Override
		public Optional<Object> getValue(Medication med) {
			return Optional.ofNullable(med.getAdministrationRoute());
		}
	},
	DOSAGE_FORM {
		@Override
		public Optional<Object> getValue(Medication med) {
			return Optional.ofNullable(med.getDoseForm());
		}
	},
	DOSAGE_FREQUENCY {
		@Override
		public Optional<Object> getValue(Medication med) {
			return Optional.ofNullable(med.getDosingFrequency());
		}
	},
	EVENT_TYPE {
		@Override
		public Optional<Object> getValue(Medication med) {
			return Optional.ofNullable(med.getEventType());
		}
	};

	public abstract Optional<Object> getValue(Medication med);
}
