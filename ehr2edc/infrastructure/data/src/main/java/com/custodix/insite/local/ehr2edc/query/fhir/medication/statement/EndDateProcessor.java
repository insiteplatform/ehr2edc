package com.custodix.insite.local.ehr2edc.query.fhir.medication.statement;

import static java.util.Optional.empty;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import ca.uhn.fhir.model.primitive.DateTimeDt;

class EndDateProcessor {


	void execute(MedicationStatement medicationStatement, Consumer<LocalDateTime> endDateConsumer) {
		Optional<LocalDateTime> startDateTime = Stream.<Supplier<Optional<LocalDateTime>>>of(
				() -> this.getEndDateTimeByEffectiveDateTime(medicationStatement),
				() -> this.getEndDateTimeByEffectiveEndPeriod(medicationStatement))
				.map(Supplier::get)
				.filter(Optional::isPresent)
				.findFirst()
				.orElseGet(Optional::empty);

		startDateTime.ifPresent(endDateConsumer);
	}

	private Optional<LocalDateTime> getEndDateTimeByEffectiveDateTime(MedicationStatement medicationStatement) {
		if(hasNoEffectiveDateTime(medicationStatement)) {
			return empty();
		}
		return Optional.of(getEffectiveDateTime(medicationStatement));
	}

	private Optional<LocalDateTime> getEndDateTimeByEffectiveEndPeriod(MedicationStatement medicationStatement) {
		if(hasNoEffectiveEndPeriod(medicationStatement)) {
			return empty();
		}
		return Optional.of(getEffectiveEndPeriodDateTime(medicationStatement));
	}

	private LocalDateTime getEffectiveEndPeriodDateTime(MedicationStatement medicationStatement) {
		return convertToLocalDateTime(getEffectiveEndPeriodDateTimeDt(medicationStatement));
	}

	private boolean hasNoEffectiveEndPeriod(MedicationStatement medicationStatement) {
		return medicationStatement.getEffective() == null ||
				!(medicationStatement.getEffective() instanceof PeriodDt) ||
				getEffectiveEndPeriodDateTimeDt(medicationStatement) == null;
	}

	private Date getEffectiveEndPeriodDateTimeDt(MedicationStatement medicationStatement) {
		return ((PeriodDt) medicationStatement.getEffective()).getEnd();
	}

	private LocalDateTime getEffectiveDateTime(MedicationStatement medicationStatement) {
		return convertToLocalDateTime(getEffectiveDateTimeDt(medicationStatement));
	}

	private Date getEffectiveDateTimeDt(MedicationStatement medicationStatement) {
		return ((DateTimeDt)medicationStatement.getEffective()).getValue();
	}

	private boolean hasNoEffectiveDateTime(MedicationStatement medicationStatement) {
		return medicationStatement.getEffective() == null ||
				!(medicationStatement.getEffective() instanceof DateTimeDt) ||
				this.getEffectiveDateTime(medicationStatement) == null;
	}

	private LocalDateTime convertToLocalDateTime(Date date) {
		return date.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}

}
