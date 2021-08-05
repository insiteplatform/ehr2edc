package com.custodix.insite.local.ehr2edc.query.fhir.medication;

import static java.util.Optional.empty;

import java.util.Optional;

import ca.uhn.fhir.model.dstu2.composite.TimingDt;

public class FhirDstu2FrequencyConverter {

	private final TimingDt.Repeat repeat;

	public FhirDstu2FrequencyConverter(TimingDt.Repeat repeat) {
		this.repeat = repeat;
	}

	public String getFrequency() {
		if(hadFrequency()) {
			return createFrequency();
		}
		return null;
	}

	private String createFrequency() {
		StringBuilder frequencyBuilder = new StringBuilder();
		frequencyBuilder.append(getFrequencyValue().orElse(""));
		frequencyBuilder.append("/");
		frequencyBuilder.append(getFrequencyPeriod().orElse(""));
		frequencyBuilder.append(getFrequencyPeriodUnit().orElse(""));
		return  frequencyBuilder.toString();
	}

	private boolean hadFrequency() {
		return hasRepeat() && (getFrequencyValue().isPresent() || getFrequencyPeriod().isPresent() || getFrequencyPeriodUnit().isPresent());
	}

	private boolean hasRepeat() {
		return repeat != null;
	}

	private Optional<Object> getFrequencyPeriodUnit() {
		if(repeat.getPeriodUnits() != null) {
			return Optional.of(repeat.getPeriodUnits());
		}
		return empty();
	}

	private Optional<String> getFrequencyPeriod() {
		Optional<String> period = empty();
		if(getFrequencyStartPeriod().isPresent() && getFrequencyMaxPeriod().isPresent()) {
			period = Optional.of(getFrequencyStartPeriod().get() + ".." + getFrequencyMaxPeriod().get());
		} else if(getFrequencyStartPeriod().isPresent()) {
			period = getFrequencyStartPeriod();
		} else if(getFrequencyMaxPeriod().isPresent()) {
			period = Optional.of(".." + getFrequencyMaxPeriod().get());
		}
		return period;
	}

	private Optional<String> getFrequencyStartPeriod() {
		if(repeat.getPeriod() != null) {
			return Optional.of(String.valueOf(repeat.getPeriod()));
		}
		return empty();
	}

	private Optional<String> getFrequencyMaxPeriod() {
		if(repeat.getPeriodMax() != null) {
			return Optional.of(String.valueOf(repeat.getPeriodMax()));
		}
		return empty();
	}

	private Optional<String> getFrequencyValue() {
		Optional<String> frequencyValue = empty();
		if(getFrequencyStartPeriod().isPresent() && getFrequencyMaxValue().isPresent()) {
			frequencyValue =  Optional.of(getFrequencyStartValue().get() + ".." + getFrequencyMaxValue().get());
		} else if(getFrequencyStartValue().isPresent()) {
			frequencyValue = getFrequencyStartValue();
		} else if(getFrequencyMaxValue().isPresent()) {
			frequencyValue = Optional.of(".." + getFrequencyMaxValue().get());
		}
		return frequencyValue;
	}

	private Optional<String> getFrequencyStartValue() {
		if(repeat.getFrequency() != null) {
			return Optional.of((String.valueOf(repeat.getFrequency())));
		}
		return empty();
	}

	private Optional<String> getFrequencyMaxValue() {
		if(repeat.getFrequencyMax() != null) {
			return Optional.of(String.valueOf(repeat.getFrequencyMax()));
		}
		return empty();
	}
}
