package com.custodix.insite.local.ehr2edc.infrastructure.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.function.Predicate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<DateRange, LocalDate> {

	private static final Predicate<String> IS_TODAY = "today"::equals;
	private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private DateRange constraintAnnotation;

	@Override
	public void initialize(DateRange constraintAnnotation) {
		this.constraintAnnotation = constraintAnnotation;
	}

	@Override
	public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
		final LocalDate min = parseOrDefault(constraintAnnotation.min());
		final LocalDate max = parseOrDefault(constraintAnnotation.max());
		return value == null || isDateInRange(value, min, max);
	}

	private boolean isDateInRange(LocalDate value, LocalDate min, LocalDate max) {
		return isDateInRangeExcludingBoundaries(value, min, max) || value.isEqual(min) || value.isEqual(max);
	}

	private boolean isDateInRangeExcludingBoundaries(LocalDate value, LocalDate min, LocalDate max) {
		return value.isAfter(min) && value.isBefore(max);
	}

	private LocalDate parseOrDefault(String max) {
		return Optional.of(max)
				.filter(IS_TODAY.negate())
				.map(this::parse)
				.orElse(LocalDate.now());
	}

	private LocalDate parse(String date) {
		try {
			return LocalDate.parse(date, FORMAT);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("Could not parse date: " + date, e);
		}
	}
}