package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.observationsummary;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;

import com.custodix.insite.mongodb.export.patient.domain.model.SummarizableObservationFact;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.observationsummary.ObservationSummaryDocument;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

public final class ObservationSummaryCounter {

	private final Map<ObservationMappingKey, Integer> observationCounts;

	public ObservationSummaryCounter() {
		this.observationCounts = new HashMap<>();
	}

	public void incrementFor(SummarizableObservationFact fact) {
		LocalDate date = fact.getObservationInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate();
		ObservationMappingKey key = new ObservationMappingKey(fact.getSubjectIdentifier(), date, fact.getCategory());
		observationCounts.put(key, observationCounts.getOrDefault(key, 0) + 1);
	}

	public List<ObservationSummaryDocument> getSummaryItemsDocumentsFor(String subjectId) {
		return observationCounts.entrySet()
				.stream()
				.filter(entry -> subjectId.equals(entry.getKey()
						.getSubjectId()))
				.map(this::makeSummaryItem)
				.collect(toList());
	}

	void resetCounter(String subjectId) {
		this.observationCounts.keySet()
				.removeIf(isKeyForSubject(subjectId));
	}

	private Predicate<ObservationMappingKey> isKeyForSubject(String subjectId) {
		return key -> key.subjectId.equals(subjectId);
	}

	private ObservationSummaryDocument makeSummaryItem(Entry<ObservationMappingKey, Integer> entry) {
		ObservationMappingKey key = entry.getKey();
		return ObservationSummaryDocument.newBuilder()
				.withDate(key.getDate())
				.withCategory(key.getCategory())
				.withAmountOfObservations(entry.getValue())
				.withSubjectId(SubjectId.of(key.getSubjectId()))
				.build();
	}

	private static final class ObservationMappingKey {
		private final String subjectId;
		private final LocalDate date;
		private final String category;

		ObservationMappingKey(String subjectId, LocalDate date, String category) {
			this.subjectId = subjectId;
			this.date = date;
			this.category = category;
		}

		public String getSubjectId() {
			return subjectId;
		}

		public LocalDate getDate() {
			return date;
		}

		public String getCategory() {
			return category;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			final ObservationMappingKey that = (ObservationMappingKey) o;
			return subjectId.equals(that.subjectId) && date.equals(that.date) && category.equals(that.category);
		}

		@Override
		public int hashCode() {
			return Objects.hash(subjectId, date, category);
		}
	}
}