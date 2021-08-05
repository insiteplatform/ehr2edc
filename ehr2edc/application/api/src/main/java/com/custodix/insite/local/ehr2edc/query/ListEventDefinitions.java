package com.custodix.insite.local.ehr2edc.query;

import static com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow.Rule.ASSIGNED_INVESTIGATOR;

import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.Allow;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

public interface ListEventDefinitions {

	@Allow(ASSIGNED_INVESTIGATOR)
	Response list(@Valid @NotNull Request request);

	final class Request {
		@AuthorizationCorrelator
		@NotNull
		@Valid
		private final StudyId studyId;

		@NotNull
		@Valid
		private final SubjectId subjectId;

		private Request(Builder builder) {
			studyId = builder.studyId;
			subjectId = builder.subjectId;
		}

		public StudyId getStudyId() {
			return studyId;
		}

		public SubjectId getSubjectId() {
			return subjectId;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Request copy) {
			Builder builder = new Builder();
			builder.studyId = copy.studyId;
			builder.subjectId = copy.subjectId;
			return builder;
		}

		public static final class Builder {
			private StudyId studyId;
			private SubjectId subjectId;

			private Builder() {
			}

			public Builder withStudyId(StudyId studyId) {
				this.studyId = studyId;
				return this;
			}

			public Builder withSubjectId(SubjectId val) {
				subjectId = val;
				return this;
			}

			public Request build() {
				return new Request(this);
			}
		}
	}

	final class Response {
		private final List<EventDefinition> eventDefinitionsInStudy;

		private Response(Builder builder) {
			eventDefinitionsInStudy = builder.eventDefinitionsInStudy;
		}

		public List<EventDefinition> getEventDefinitionsInStudy() {
			return eventDefinitionsInStudy;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(Response copy) {
			Builder builder = new Builder();
			builder.eventDefinitionsInStudy = copy.getEventDefinitionsInStudy();
			return builder;
		}

		public static final class Builder {
			private List<EventDefinition> eventDefinitionsInStudy;

			private Builder() {
			}

			public Builder withEventDefinitionsInStudy(List<EventDefinition> eventDefinitionsInStudy) {
				this.eventDefinitionsInStudy = eventDefinitionsInStudy;
				return this;
			}

			public Response build() {
				return new Response(this);
			}
		}
	}

	final class EventDefinition {
		private final EventDefinitionId eventDefinitionId;
		private final String name;
		private final List<FormDefinition> formDefinitions;
		private final long queryCount;
		private final LocalDate lastReferenceDate;
		private final Instant lastPopulationTime;
		private final EventId eventId;
		private final boolean historyAvailable;

		private EventDefinition(Builder builder) {
			eventDefinitionId = builder.eventDefinitionId;
			name = builder.name;
			formDefinitions = builder.formDefinitions;
			queryCount = builder.queryCount;
			lastReferenceDate = builder.lastReferenceDate;
			lastPopulationTime = builder.lastPopulationTime;
			eventId = builder.eventId;
			historyAvailable = builder.historyAvailable;
		}

		public EventDefinitionId getEventDefinitionId() {
			return eventDefinitionId;
		}

		public String getName() {
			return name;
		}

		public List<FormDefinition> getFormDefinitions() {
			return formDefinitions;
		}

		public long getQueryCount() {
			return queryCount;
		}

		public LocalDate getLastReferenceDate() {
			return lastReferenceDate;
		}

		public Instant getLastPopulationTime() {
			return lastPopulationTime;
		}

		public EventId getEventId() {
			return eventId;
		}

		public boolean isHistoryAvailable() {
			return historyAvailable;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static Builder newBuilder(EventDefinition copy) {
			Builder builder = new Builder();
			builder.eventDefinitionId = copy.getEventDefinitionId();
			builder.name = copy.getName();
			builder.formDefinitions = copy.getFormDefinitions();
			builder.queryCount = copy.getQueryCount();
			builder.lastPopulationTime = copy.getLastPopulationTime();
			return builder;
		}

		public static final class Builder {
			private String name;
			private List<FormDefinition> formDefinitions;
			private long queryCount;
			private LocalDate lastReferenceDate;
			private Instant lastPopulationTime;
			private EventId eventId;
			private boolean historyAvailable;
			private EventDefinitionId eventDefinitionId;

			private Builder() {
			}

			public Builder withEventDefinitionId(EventDefinitionId id) {
				this.eventDefinitionId = id;
				return this;
			}

			public Builder withName(String name) {
				this.name = name;
				return this;
			}

			public Builder withFormDefinitions(List<FormDefinition> val) {
				formDefinitions = val;
				return this;
			}

			public Builder withQueryCount(long val) {
				queryCount = val;
				return this;
			}

			public Builder withLastReferenceDate(final LocalDate val) {
				lastReferenceDate = val;
				return this;
			}

			public Builder withLastPopulationTime(final Instant val) {
				lastPopulationTime = val;
				return this;
			}

			public Builder withEventId(EventId eventId) {
				this.eventId = eventId;
				return this;
			}

			public Builder withHistoryAvailable(boolean val) {
				historyAvailable = val;
				return this;
			}

			public EventDefinition build() {
				return new EventDefinition(this);
			}
		}
	}

	final class FormDefinition {
		private final FormDefinitionId formDefinitionId;
		private final String name;
		private final long queryCount;

		private FormDefinition(Builder builder) {
			formDefinitionId = builder.formDefinitionId;
			name = builder.name;
			queryCount = builder.queryCount;
		}

		public FormDefinitionId getFormDefinitionId() {
			return formDefinitionId;
		}

		public String getName() {
			return name;
		}

		public long getQueryCount() {
			return queryCount;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public static final class Builder {
			private FormDefinitionId formDefinitionId;
			private String name;
			private long queryCount;

			private Builder() {
			}

			public Builder withFormDefinitionId(FormDefinitionId formDefinitionId) {
				this.formDefinitionId = formDefinitionId;
				return this;
			}

			public Builder withName(String name) {
				this.name = name;
				return this;
			}

			public Builder withQueryCount(long val) {
				queryCount = val;
				return this;
			}

			public FormDefinition build() {
				return new FormDefinition(this);
			}
		}
	}
}