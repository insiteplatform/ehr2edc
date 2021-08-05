package com.custodix.insite.local.ehr2edc.query.executor.vitalsign.projector;

import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSign;
import com.custodix.insite.local.ehr2edc.query.executor.vitalsign.domain.VitalSignConcept;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

public enum VitalSignField {
	SUBJECT {
		@Override
		public Optional<Object> getValue(VitalSign vitalSign) {
			return Optional.ofNullable(vitalSign.getSubjectId())
					.map(SubjectId::getId);
		}
	},
	DATE {
		@Override
		public Optional<Object> getValue(VitalSign vitalSign) {
			return Optional.ofNullable(vitalSign.getEffectiveDateTime());
		}
	},
	CODE {
		@Override
		public Optional<Object> getValue(VitalSign vitalSign) {
			return Optional.ofNullable(vitalSign.getVitalSignConcept())
					.map(VitalSignConcept::getConcept)
					.map(ConceptCode::getCode);
		}
	},
	VALUE {
		@Override
		public Optional<Object> getValue(VitalSign vitalSign) {
			return Optional.ofNullable(vitalSign.getMeasurement())
					.map(Measurement::getValue);
		}
	},
	UNIT {
		@Override
		public Optional<Object> getValue(VitalSign vitalSign) {
			return Optional.ofNullable(vitalSign.getMeasurement())
					.map(Measurement::getUnit);
		}
	},
	LOWER_LIMIT {
		@Override
		public Optional<Object> getValue(VitalSign vitalSign) {
			return Optional.ofNullable(vitalSign.getMeasurement())
					.map(Measurement::getLowerLimit);
		}
	},
	UPPER_LIMIT {
		@Override
		public Optional<Object> getValue(VitalSign vitalSign) {
			return Optional.ofNullable(vitalSign.getMeasurement())
					.map(Measurement::getUpperLimit);
		}
	},
	LOCATION {
		@Override
		public Optional<Object> getValue(VitalSign vitalSign) {
			return Optional.ofNullable(vitalSign.getVitalSignConcept())
					.map(VitalSignConcept::getLocation);
		}
	},
	LATERALITY {
		@Override
		public Optional<Object> getValue(VitalSign vitalSign) {
			return Optional.ofNullable(vitalSign.getVitalSignConcept())
					.map(VitalSignConcept::getLaterality);
		}
	},
	POSITION {
		@Override
		public Optional<Object> getValue(VitalSign vitalSign) {
			return Optional.ofNullable(vitalSign.getVitalSignConcept())
					.map(VitalSignConcept::getPosition);
		}
	};

	public abstract Optional<Object> getValue(VitalSign vitalSign);
}
