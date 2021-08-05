package com.custodix.insite.local.ehr2edc.query.fhir.medication;

import java.time.LocalDateTime;
import java.util.function.Predicate;

import com.custodix.insite.local.ehr2edc.query.executor.common.domain.Interval;
import com.custodix.insite.local.ehr2edc.query.executor.medication.domain.Medication;

public final class MedicationFreshnessMatcher implements Predicate<Medication> {

	private final Interval freshnessInterval;

	public MedicationFreshnessMatcher(Interval freshnessCriterion) {
		this.freshnessInterval = freshnessCriterion;
	}

	@Override
	public boolean test(Medication medication) {
		LocalDateTime startDateMedication = medication.getStartDate();
		LocalDateTime endDateMedication = medication.getEndDate();
		return isBetweenFreshnessPeriod(startDateMedication) ||
				isBetweenFreshnessPeriod(endDateMedication) ||
				isEqualsToStartOfFreshnessPeriod(endDateMedication) ||
				isEqualsToStartOfFreshnessPeriod(startDateMedication) ||
				isBeforeStartOfFreshnessPeriod(startDateMedication) && isAfterEndOfFreshnessPeriod(endDateMedication);
	}

	private boolean isAfterEndOfFreshnessPeriod(LocalDateTime dateMedication) {
		return dateMedication.isAfter(freshnessInterval.getEndDate().atStartOfDay());
	}

	private boolean isBeforeStartOfFreshnessPeriod(LocalDateTime dateMedication) {
		return dateMedication.isBefore(freshnessInterval.getStartDate().atStartOfDay());
	}

	private boolean isEqualsToStartOfFreshnessPeriod(LocalDateTime dateMedication) {
		return dateMedication.isEqual(freshnessInterval.getStartDate().atStartOfDay());
	}

	private boolean isBetweenFreshnessPeriod(LocalDateTime dateMedication) {
		return dateMedication.isAfter(freshnessInterval.getStartDate().atStartOfDay()) &&
				dateMedication.isBefore(freshnessInterval.getEndDate().atStartOfDay());
	}
}
