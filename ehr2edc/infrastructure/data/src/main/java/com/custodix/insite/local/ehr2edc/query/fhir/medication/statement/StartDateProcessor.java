package com.custodix.insite.local.ehr2edc.query.fhir.medication.statement;

import static java.util.Optional.empty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import ca.uhn.fhir.model.primitive.DateTimeDt;

class StartDateProcessor {

	void execute(MedicationStatement medicationStatement, Consumer<LocalDateTime> startDateConsumer) {
		Optional<LocalDateTime> startDateTime = Stream.<Supplier<Optional<LocalDateTime>>>of(
				() -> this.getStartDateTimeByEffectiveDateTime(medicationStatement),
				() -> this.getStartDateTimeByEffectiveStartPeriod(medicationStatement))
				.map(Supplier::get)
				.filter(Optional::isPresent)
				.findFirst()
				.orElseGet(Optional::empty);

		startDateTime.ifPresent(startDateConsumer);
	}

	private Optional<LocalDateTime> getStartDateTimeByEffectiveDateTime(MedicationStatement medicationStatement) {
		if(hasNoEffectiveDateTime(medicationStatement)) {
			return empty();
		}
		return Optional.of(getEffectiveDateTime(medicationStatement));
	}

	private Optional<LocalDateTime> getStartDateTimeByEffectiveStartPeriod(MedicationStatement medicationStatement) {
		if(hasNoEffectiveStartPeriod(medicationStatement)) {
			return empty();
		}
		return Optional.of(getEffectiveStartPeriodDateTime(medicationStatement));
	}

	private LocalDateTime getEffectiveStartPeriodDateTime(MedicationStatement medicationStatement) {
		return convertToLocalDateTime(getEffectiveStartPeriodDateTimeDt(medicationStatement));
	}

	private boolean hasNoEffectiveStartPeriod(MedicationStatement medicationStatement) {
		return medicationStatement.getEffective() == null ||
				!(medicationStatement.getEffective() instanceof PeriodDt) ||
				getEffectiveStartPeriodDateTimeDt(medicationStatement) == null;
	}

	private Date getEffectiveStartPeriodDateTimeDt(MedicationStatement medicationStatement) {
		return ((PeriodDt) medicationStatement.getEffective()).getStart();
	}

	private LocalDateTime getEffectiveDateTime(MedicationStatement medicationStatement) {
		return convertToLocalDateTime(getEffectiveDateTimeDt(medicationStatement));
	}

	private boolean hasNoEffectiveDateTime(MedicationStatement medicationStatement) {
		return medicationStatement.getEffective() == null ||
				!(medicationStatement.getEffective() instanceof DateTimeDt) ||
				this.getEffectiveDateTime(medicationStatement) == null;
	}

	private Date getEffectiveDateTimeDt(MedicationStatement medicationStatement) {
		return ((DateTimeDt) medicationStatement.getEffective()).getValue();
	}

	private LocalDateTime convertToLocalDateTime(Date date) {
		return date.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}

	private static class DateTimeDtAscendingComparator implements Comparator<DateTimeDt>, Serializable {
		@Override
		public int compare(DateTimeDt dateTime1, DateTimeDt dateTime2) {
			return dateTime1.getValue().compareTo(dateTime2.getValue());
		}
	}
}
