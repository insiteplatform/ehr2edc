package com.custodix.insite.local.ehr2edc.query.executor.demographic.projector.birthdate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DateOfBirth {
	private LocalDate birthDate;

	public DateOfBirth(final LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public Age getAge(LocalDate referenceDate, AgeUnit ageUnit) {
		return Optional.ofNullable(referenceDate)
				.map(until -> new Age(birthDate, until, ageUnit))
				.orElseGet(() -> new Age(birthDate, LocalDate.now(), ageUnit));
	}

	public String toString(String format) {
		return birthDate.format(DateTimeFormatter.ofPattern(format));
	}

	@Override
	public String toString() {
		return birthDate.format(DateTimeFormatter.ISO_DATE);
	}
}
