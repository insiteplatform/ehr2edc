package com.custodix.insite.local.ehr2edc.query.executor.laboratory.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public enum LabValueField {
	SUBJECT {
		@Override
		public Optional<Object> getValue(LabValue labValue) {
			return Optional.ofNullable(labValue.getSubjectId())
					.map(SubjectId::getId);
		}
	},
	START_DATE {
		@Override
		public Optional<Object> getValue(LabValue labValue) {
			return Optional.ofNullable(labValue.getStartDate());
		}
	},
	END_DATE {
		@Override
		public Optional<Object> getValue(LabValue labValue) {
			return Optional.ofNullable(labValue.getEndDate());
		}
	},
	CODE {
		@Override
		public Optional<Object> getValue(LabValue labValue) {
			return Optional.ofNullable(labValue.getLabConcept())
					.map(LabConcept::getConcept)
					.map(ConceptCode::getCode);
		}
	},
	VALUE {
		@Override
		public Optional<Object> getValue(LabValue labValue) {
			return Optional.ofNullable(labValue.getQuantitativeResult())
					.map(Measurement::getValue);
		}
	},
	UNIT {
		@Override
		public Optional<Object> getValue(LabValue labValue) {
			return Optional.ofNullable(labValue.getQuantitativeResult())
					.map(Measurement::getUnit);
		}
	},
	LOWER_LIMIT {
		@Override
		public Optional<Object> getValue(LabValue labValue) {
			return Optional.ofNullable(labValue.getQuantitativeResult())
					.map(Measurement::getLowerLimit);
		}
	},
	UPPER_LIMIT {
		@Override
		public Optional<Object> getValue(LabValue labValue) {
			return Optional.ofNullable(labValue.getQuantitativeResult())
					.map(Measurement::getUpperLimit);
		}
	},
	METHOD {
		@Override
		public Optional<Object> getValue(LabValue labValue) {
			return Optional.ofNullable(labValue.getLabConcept())
					.map(LabConcept::getMethod);
		}
	},
	FASTING_STATUS {
		@Override
		public Optional<Object> getValue(LabValue labValue) {
			return Optional.ofNullable(labValue.getLabConcept())
					.map(LabConcept::getFastingStatus);
		}
	},
	SPECIMEN {
		@Override
		public Optional<Object> getValue(LabValue labValue) {
			return Optional.ofNullable(labValue.getLabConcept())
					.map(LabConcept::getSpecimen);
		}
	},
	VENDOR {
		@Override
		public Optional<Object> getValue(LabValue labValue) {
			return Optional.ofNullable(labValue.getVendor());
		}
	};

	public abstract Optional<Object> getValue(LabValue labValue);
}
