package com.custodix.insite.local.ehr2edc.query.fhir.vitalsign.observation.mapping;

import static java.util.Optional.ofNullable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;

class VitalSignObservationEffectiveProcessor {
	static void process(IDatatype effective, Consumer<LocalDateTime> consumer) {
		Stream.of(fromDateTime(effective), fromPeriod(effective))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst()
				.map(VitalSignObservationEffectiveProcessor::toLocalDateTime)
				.ifPresent(consumer);
	}

	private static Optional<Date> fromDateTime(IDatatype effective) {
		return from(effective, DateTimeDt.class).map(DateTimeDt::getValue);
	}

	private static Optional<Date> fromPeriod(IDatatype effective) {
		return from(effective, PeriodDt.class).map(p -> ofNullable(p.getStart()).orElse(p.getEnd()));
	}

	private static <T extends IDatatype> Optional<T> from(IDatatype effective, Class<T> targetClass) {
		return ofNullable(effective).filter(targetClass::isInstance)
				.map(targetClass::cast);
	}

	private static LocalDateTime toLocalDateTime(Date date) {
		return date.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}
}
