package com.custodix.insite.local.ehr2edc.query.fhir.demographic;

import static com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType.VITAL_STATUS;

import java.util.Optional;
import java.util.function.Function;

import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.vitalstatus.VitalStatus;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.BooleanDt;

class FhirDstu2DemographicFactoryForVitalStatus implements FhirDstu2DemographicFactory {

	private static final boolean DECEASED = true;

	@Override
	public DemographicType supports() {
		return VITAL_STATUS;
	}

	@Override
	public Optional<Demographic> findDemographic(Patient patient, SubjectId subjectId) {
		return Optional.ofNullable(patient.getDeceased())
				.map(this::isDeceased)
				.map(this::toVitalStatus)
				.map(toDemographicFor(subjectId));
	}

	private boolean isDeceased(IDatatype deceased) {
		return deceased instanceof BooleanDt ? ((BooleanDt) deceased).getValue() : DECEASED;
	}

	private VitalStatus toVitalStatus(boolean deceased) {
		return deceased ? VitalStatus.DECEASED : VitalStatus.ALIVE;
	}

	private Function<VitalStatus, Demographic> toDemographicFor(SubjectId subjectId) {
		return status -> Demographic.newBuilder()
				.withSubjectId(subjectId)
				.withDemographicType(VITAL_STATUS)
				.withValue(status.toString())
				.build();
	}
}
