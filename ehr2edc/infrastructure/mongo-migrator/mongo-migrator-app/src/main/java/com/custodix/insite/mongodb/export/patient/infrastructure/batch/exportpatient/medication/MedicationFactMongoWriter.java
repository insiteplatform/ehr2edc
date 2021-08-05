package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.medication;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;

import com.custodix.insite.mongodb.export.patient.domain.model.common.Modifier;
import com.custodix.insite.mongodb.export.patient.domain.model.medication.MedicationFact;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.concept.ConceptCode;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.medication.Dosage;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.medication.MedicationConcept;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.model.medication.MedicationDocument;
import com.custodix.insite.mongodb.export.patient.infrastructure.mongo.repository.MedicationDocumentRepository;
import com.custodix.insite.mongodb.vocabulary.SubjectId;

@StepScope
public final class MedicationFactMongoWriter implements ItemWriter<MedicationFact> {

	private final MedicationDocumentRepository medicationDocumentRepository;

	MedicationFactMongoWriter(MedicationDocumentRepository medicationDocumentRepository) {
		this.medicationDocumentRepository = medicationDocumentRepository;
	}

	@Override
	public void write(List<? extends MedicationFact> list) throws Exception {
		list.stream()
				.filter(Objects::nonNull)
				.map(new ToMedicationDocument())
				.forEach(medicationDocumentRepository::save);
	}

	private static class ToMedicationDocument implements Function<MedicationFact, MedicationDocument> {

		@Override
		public MedicationDocument apply(final MedicationFact medicationFact) {
			MedicationDocument.Builder builder = MedicationDocument.newBuilder()
					.withEndDate(toLocalDateTime(medicationFact.getEndDate()))
					.withStartDate(toLocalDateTime(medicationFact.getStartDate()))
					.withConcept(getMedicationConcept(medicationFact))
					.withSubjectId(SubjectId.of(medicationFact.getSubjectIdentifier()));

			addOptionalFields(medicationFact, builder);

			return builder.build();
		}

		private void addOptionalFields(MedicationFact fact, MedicationDocument.Builder builder) {
			fact.getDosage()
					.map(dosage -> new Dosage(dosage.getValue(), dosage.getUnit()))
					.ifPresent(builder::withDosage);
			fact.getEventType()
					.ifPresent(builder::withEventType);

			WithModifiers.from(fact)
					.into(builder);
		}

		private MedicationConcept getMedicationConcept(final MedicationFact medicationFact) {
			ConceptCode concept = ConceptCode.conceptFor(medicationFact.getReferenceConcept()
					.getCode());
			return MedicationConcept.newBuilder()
					.withConcept(concept)
					.withName(medicationFact.getConceptName())
					.build();
		}

		private LocalDateTime toLocalDateTime(Instant instant) {
			return instant.atZone(ZoneId.systemDefault())
					.toLocalDateTime();
		}

		private static final class WithModifiers {

			private final MedicationFact fact;

			private WithModifiers(MedicationFact fact) {
				this.fact = fact;
			}

			static WithModifiers from(MedicationFact fact) {
				return new WithModifiers(fact);
			}

			void into(MedicationDocument.Builder builder) {
				withModifier(fact::getRoute, builder::withRoute);
				withModifier(fact::getFrequency, builder::withFrequency);
				withModifier(fact::getDoseFormat, builder::withDoseFormat);
			}

			private void withModifier(Supplier<Optional<Modifier>> getter, Consumer<String> consumer) {
				getter.get()
						.map(Modifier::getReferenceCode)
						.ifPresent(consumer);
			}
		}
	}
}
