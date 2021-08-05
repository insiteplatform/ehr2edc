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

class EndDateProcessor {

	void execute(TimingDt timing,
			MedicationDispense medicationDispense, Consumer<LocalDateTime> endDateConsumer) {
		Optional<LocalDateTime> endDate = Stream.<Supplier<Optional<LocalDateTime>>>of(
				() -> getEndDateTimeByEventTime(timing),
				() -> getEndDateTimeByBoundPeriod(timing),
				() -> getEndDateTimeByHandedOverTime(medicationDispense),
				() -> getEndDateTimeByPreparedTime(medicationDispense))
			.map(Supplier::get)
			.filter(Optional::isPresent)
			.findFirst()
			.orElseGet(Optional::empty);

		endDate.ifPresent(endDateConsumer);
	}

	private Optional<LocalDateTime> getEndDateTimeByEventTime(TimingDt timing) {
		if(hasNoEventTime(timing)) {
			return empty();
		}
		List<DateTimeDt> eventTimesOrderedDesc = getOrderedDescendingEventTimes(timing);
		return Optional.of(convertToLocalDateTime(eventTimesOrderedDesc.get(0)));
	}

	private Optional<LocalDateTime> getEndDateTimeByBoundPeriod(TimingDt timing) {
		if(hasNoOpenPeriod(timing)) {
			return empty();
		}
		return Optional.of(convertToLocalDateTime(getEndBound(timing)));
	}

	private Optional<LocalDateTime> getEndDateTimeByHandedOverTime(MedicationDispense medicationDispense) {
		if(hasNoHandedOverTime(medicationDispense)) {
			return empty();
		}
		return Optional.of(getHandedOverTime(medicationDispense));
	}

	private Optional<LocalDateTime> getEndDateTimeByPreparedTime(MedicationDispense medicationDispense) {
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

	private boolean hasNoHandedOverTime(MedicationDispense medicationDispense) {
		return medicationDispense.getWhenHandedOver() == null;
	}

	private boolean hasNoPreparedTime(MedicationDispense medicationDispense) {
		return medicationDispense.getWhenPrepared() == null;
	}

	private Date getEndBound(TimingDt timing) {
		return ((PeriodDt) timing.getRepeat().getBounds()).getEnd();
	}

	private boolean hasNoOpenPeriod(TimingDt timing) {
		return timing.getRepeat() == null ||
				timing.getRepeat().getBounds() == null ||
				!(timing.getRepeat().getBounds() instanceof PeriodDt) ||
				getEndBound(timing) ==null;
	}

	private LocalDateTime convertToLocalDateTime(Date date) {
		return date.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}

	private boolean hasNoEventTime(TimingDt timing) {
		return timing.getEvent() == null || timing.getEvent().isEmpty();
	}

	private List<DateTimeDt> getOrderedDescendingEventTimes(TimingDt timing) {
		final List<DateTimeDt> eventTimes;
		eventTimes = timing.getEvent();
		eventTimes.sort(new DateTimeDtDescendingComparator());
		return eventTimes;
	}

	private LocalDateTime convertToLocalDateTime(DateTimeDt dateTimeDt) {
		return dateTimeDt.getValue().toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}

	private static class DateTimeDtDescendingComparator implements Comparator<DateTimeDt>, Serializable {
		@Override
		public int compare(DateTimeDt dateTime1, DateTimeDt dateTime2) {
			return dateTime2.getValue().compareTo(dateTime1.getValue());
		}
	}

}
