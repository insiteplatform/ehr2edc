package com.custodix.insite.local.ehr2edc.query.fhir.medication.order;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Dosage;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.MedicationConcept;
import com.custodix.insite.local.ehr2edc.query.fhir.medication.*;

import ca.uhn.fhir.model.api.BasePrimitive;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.TimingDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;

class FhirMedicationOrderMapper {
	private final MedicationConceptCodeProcessor medicationConceptCodeProcessor;
	private final MedicationOrder medicationOrder;
	private final MedicationOrder.DosageInstruction dosageInstruction;

	FhirMedicationOrderMapper(final MedicationConceptCodeProcessor medicationConceptCodeProcessor,
			final MedicationOrder medicationOrder,
			final MedicationOrder.DosageInstruction dosageInstruction) {
		this.medicationConceptCodeProcessor = medicationConceptCodeProcessor;
		this.medicationOrder = medicationOrder;
		this.dosageInstruction = dosageInstruction;
	}

	Dosage getDosage() {
		final IDatatype dosage = dosageInstruction.getDose();
		final Dosage.Builder dosageBuilder = Dosage.newBuilder();
		new DosageValueProcessor().execute(dosage, dosageBuilder::withValue);
		new DosageUnitProcessor().execute(dosage, dosageBuilder::withUnit);
		return dosageBuilder.build();
	}

	Optional<String> getRoute() {
		return Optional.ofNullable(dosageInstruction.getRoute())
				.map(CodeableConceptDt::getCodingFirstRep)
				.map(CodingDt::getCode);
	}

	Optional<String> getDosageForm() {
		return new MedicationDoseFormProcessor().getDosageFormCode(medicationOrder.getMedication());
	}

	Optional<String> getDosageFrequency() {
		return Optional.ofNullable(dosageInstruction.getTiming())
				.map(TimingDt::getRepeat)
				.map(FhirDstu2FrequencyConverter::new)
				.map(FhirDstu2FrequencyConverter::getFrequency);
	}

	Optional<LocalDateTime> getStartDate() {
		return getDate(Comparator.naturalOrder(), getDateWritten());
	}

	Optional<LocalDateTime> getEndDate() {
		return getDate(Comparator.reverseOrder(), getDateEnded());
	}

	private Optional<LocalDateTime> getDateWritten() {
		return Optional.ofNullable(medicationOrder.getDateWritten())
				.map(this::dateToLocalDateTime);
	}

	private Optional<LocalDateTime> getDateEnded() {
		return Optional.ofNullable(medicationOrder.getDateEnded())
				.map(this::dateToLocalDateTime);
	}

	private Optional<LocalDateTime> getDate(final Comparator<? super LocalDateTime> comparator,
			final Optional<LocalDateTime> fallback) {
		final Supplier<Optional<LocalDateTime>> getEvent = () -> getEvent(comparator);
		final Supplier<Optional<LocalDateTime>> getBoundary = () -> getRepeatBoundary(comparator);
		final Supplier<Optional<LocalDateTime>> getFallback = () -> fallback;
		return Stream.of(getEvent, getBoundary, getFallback)
				.map(Supplier::get)
				.filter(Optional::isPresent)
				.findFirst()
				.flatMap(Function.identity());
	}

	private Optional<LocalDateTime> getEvent(final Comparator<? super LocalDateTime> comparator) {
		return Optional.ofNullable(dosageInstruction.getTiming())
				.map(TimingDt::getEvent)
				.flatMap(events -> events.stream()
						.map(BasePrimitive::getValue)
						.map(this::dateToLocalDateTime)
						.min(comparator));
	}

	private Optional<LocalDateTime> getRepeatBoundary(final Comparator<? super LocalDateTime> comparator) {
		return Optional.ofNullable(dosageInstruction.getTiming())
				.map(TimingDt::getRepeat)
				.map(repeat -> (PeriodDt) repeat.getBounds())
				.flatMap(bounds -> Stream.of(bounds.getStart(), bounds.getEnd())
						.filter(Objects::nonNull)
						.map(this::dateToLocalDateTime)
						.min(comparator));
	}

	private LocalDateTime dateToLocalDateTime(final Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	Optional<MedicationConcept> getConcept() {
		final IDatatype medication = medicationOrder.getMedication();
		return medicationConceptCodeProcessor.createMedicationConceptCode(medication);
	}
}
