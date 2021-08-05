package com.custodix.insite.local.ehr2edc.query.fhir.medication.dispense;

import static java.util.Optional.empty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.TimingDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationDispense;
import ca.uhn.fhir.model.primitive.DateTimeDt;

class StartDateProcessor {

	void execute(TimingDt timing,
			MedicationDispense medicationDispense, Consumer<LocalDateTime> startDateConsumer) {
		Optional<LocalDateTime> startDateTime = Stream.<Supplier<Optional<LocalDateTime>>>of(
				() -> this.getStartDateTimeByEventTime(timing),
				() -> this.getStartDateTimeByBoundPeriod(timing),
				() -> this.getStartDateTimeByHandedOverTime(medicationDispense),
				() -> this.getStartDateTimeByPreparedTime(medicationDispense))
			.map(Supplier::get)
			.filter(Optional::isPresent)
			.findFirst()
			.orElseGet(Optional::empty);

		startDateTime.ifPresent(startDateConsumer);
	}

	private Optional<LocalDateTime> getStartDateTimeByEventTime(TimingDt timing) {
		if(hasNoEventTime(timing)) {
			return empty();
		}
		List<DateTimeDt> eventTimesOrderedAsc = getOrderedAscendingEventTimes(timing);
		return Optional.of(convertToLocalDateTime(eventTimesOrderedAsc.get(0)));
	}

	private Optional<LocalDateTime> getStartDateTimeByBoundPeriod(TimingDt timing) {
		if(hasNoStartBoundPeriod(timing)) {
			return empty();
		}
		return Optional.of(convertToLocalDateTime(getStartBound(timing)));
	}

	private Optional<LocalDateTime> getStartDateTimeByHandedOverTime(MedicationDispense medicationDispense) {
		if(hasNoHandedOverTime(medicationDispense)) {
			return empty();
		}
		return Optional.of(getHandedOverTime(medicationDispense));
	}

	private Optional<LocalDateTime> getStartDateTimeByPreparedTime(MedicationDispense medicationDispense) {
		if(hasNoPreparedTime(medicationDispense)) {
			return empty();
		}
		return Optional.of(getPreparedTime(medicationDispense));
	}
	private LocalDateTime getHandedOverTime(MedicationDispense medicationDispense) {
		return convertToLocalDateTime(medicationDispense.getWhenHandedOver());
	}

	private LocalDateTime getPreparedTime(MedicationDispense medicationDispense) {
		return convertToLocalDateTime(medicationDispense.getWhenPrepared());
	}

	private LocalDateTime convertToLocalDateTime(Date date) {
		return date.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}

	private boolean hasNoHandedOverTime(MedicationDispense medicationDispense) {
		return medicationDispense.getWhenHandedOver() == null;
	}

	private boolean hasNoPreparedTime(MedicationDispense medicationDispense) {
		return medicationDispense.getWhenPrepared() == null;
	}

	private Date getStartBound(TimingDt timing) {
		return ((PeriodDt) timing.getRepeat().getBounds()).getStart();
	}

	private boolean hasNoStartBoundPeriod(TimingDt timing) {
		return timing.getRepeat() == null ||
				timing.getRepeat().getBounds() == null ||
				!(timing.getRepeat().getBounds() instanceof PeriodDt) ||
				getStartBound(timing) ==null;
	}

	private boolean hasNoEventTime(TimingDt timing) {
		return timing.getEvent() == null || timing.getEvent().isEmpty();
	}

	private List<DateTimeDt> getOrderedAscendingEventTimes(TimingDt timing) {
		final List<DateTimeDt> eventTimes;
		eventTimes = timing.getEvent();
		eventTimes.sort(new DateTimeDtAscendingComparator());
		return eventTimes;
	}

	private LocalDateTime convertToLocalDateTime(DateTimeDt dateTimeDt) {
		return dateTimeDt.getValue().toInstant()
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
