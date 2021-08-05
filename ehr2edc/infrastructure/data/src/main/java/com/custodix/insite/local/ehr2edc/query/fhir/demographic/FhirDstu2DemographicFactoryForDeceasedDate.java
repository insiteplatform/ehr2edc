package com.custodix.insite.local.ehr2edc.query.fhir.demographic;

import static com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType.DEATH_DATE;
import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.Optional;

import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.Demographic.Builder;
import com.custodix.insite.local.ehr2edc.query.executor.demographic.domain.DemographicType;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.DateTimeDt;

class FhirDstu2DemographicFactoryForDeceasedDate implements FhirDstu2DemographicFactory {

	private static final DateTimeFormatter DECEASE_DATE_FORMATTER = ofPattern("yyyy-MM-dd");

	@Override
	public DemographicType supports() {
		return DEATH_DATE;
	}

	@Override
	public Optional<Demographic> findDemographic(Patient patient, SubjectId subjectId) {
		Builder builder = Demographic.newBuilder().withSubjectId(subjectId).withDemographicType(DEATH_DATE);
		return Optional.ofNullable(patient.getDeceased())
				.filter(dataType -> dataType instanceof DateTimeDt)
				.map(DateTimeDt.class::cast)
				.map(DateTimeDt::getValueAsCalendar)
				.map(GregorianCalendar::toZonedDateTime)
				.map(ZonedDateTime::toLocalDate)
				.map(date -> date.format(DECEASE_DATE_FORMATTER))
				.map(builder::withValue)
				.map(Builder::build);
	}

}
