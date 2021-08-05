package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.observationsummary;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.custodix.insite.mongodb.export.patient.domain.model.SummarizableObservationFact;

public class ObservationSummarizer implements ItemWriter<SummarizableObservationFact> {
	private final ObservationSummaryCounter observationSummaryCounter;

	public ObservationSummarizer(ObservationSummaryCounter observationSummaryCounter) {
		this.observationSummaryCounter = observationSummaryCounter;
	}

	@Override
	public void write(List<? extends SummarizableObservationFact> laboratoryFacts) {
		laboratoryFacts.forEach(observationSummaryCounter::incrementFor);
	}

}