package com.custodix.insite.local.ehr2edc.query.fhir;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.query.executor.common.concept.ConceptCode;
import com.custodix.insite.local.ehr2edc.query.executor.common.domain.Interval;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.Query;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.ConceptCriterion;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.FreshnessCriterion;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.OrdinalCriterion;
import com.custodix.insite.local.ehr2edc.query.fhir.vocabulary.FhirResourceId;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.gclient.DateClientParam;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

public class FhirQueryFactory {
	public FhirQuery create(Query query,
			FhirResourceId patientResourceId,
			LocalDate referenceDate,
			ResourceFields resourceFields,
			List<Include> includes) {
		FhirQuery.Builder builder = FhirQuery.newBuilder();
		addCriteria(builder, query, patientResourceId, referenceDate, resourceFields);
		addSort(builder, query, resourceFields.getDateField());
		addLimit(builder, query);
		addInclude(builder, includes);
		return builder.build();
	}

	private void addInclude(FhirQuery.Builder builder, List<Include> includes) {
		builder.withIncludes(includes);
	}

	private void addCriteria(FhirQuery.Builder builder, Query query, FhirResourceId patientResourceId,
			LocalDate referenceDate, ResourceFields resourceFields) {
		List<ICriterion> criteria = new ArrayList<>();
		buildPatientCriterion(resourceFields.getPatientField(), patientResourceId).ifPresent(criteria::add);
		buildFreshnessCriteria(query, referenceDate, resourceFields.getDateField()).ifPresent(criteria::addAll);
		createConceptCriterion(query, resourceFields.getCodeField()).ifPresent(criteria::add);
		builder.withCriteria(criteria);
	}

	private void addSort(FhirQuery.Builder builder, Query query, ResourceField dateField) {
		query.getCriteria()
				.ordinal()
				.ifPresent(
						ordinal -> builder.withSortSpec(new SortSpec(dateField.getField(), map(ordinal.getOrdinal()))));
	}

	private void addLimit(FhirQuery.Builder builder, Query query) {
		query.getCriteria()
				.ordinal()
				.ifPresent(ordinal -> builder.withLimit(1));
	}

	private Optional<List<ICriterion<DateClientParam>>> buildFreshnessCriteria(Query query, LocalDate referenceDate, ResourceField dateField) {
		return query.getCriteria().freshness().map(
				freshnessCriterion -> getFreshnessIntervalCriteria(freshnessCriterion, referenceDate, dateField));
	}

	private Optional<ICriterion> buildPatientCriterion(ResourceField patientField, FhirResourceId patientResourceId) {
		return Optional.of(createCriterion(patientField, patientResourceId.getId()));
	}

	private ICriterion createCriterion(ResourceField field, String value) {
		return new StringClientParam(field.getField()).matches()
				.value(value);
	}

	private List<ICriterion<DateClientParam>> getFreshnessIntervalCriteria(
			FreshnessCriterion freshnessCriterion,
			LocalDate referenceDate,
			ResourceField dateField) {

		Interval interval = freshnessCriterion.getInterval(referenceDate);
		ICriterion<DateClientParam> startCriterion =  getStartDateCriterion(dateField, interval.getStartDate());
		ICriterion<DateClientParam> endCriterion =  getEndDateCriterion(dateField, interval.getEndDate());
		return Arrays.asList(startCriterion, endCriterion);
	}

	private ICriterion<DateClientParam> getEndDateCriterion(ResourceField dateField, LocalDate endDate) {
		return new DateClientParam(dateField.getField()).before().second(getDate(endDate));
	}

	private Date getDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault())
				.toInstant());
	}

	private ICriterion<DateClientParam> getStartDateCriterion(ResourceField dateField, LocalDate startDate) {
		return new DateClientParam(dateField.getField()).afterOrEquals().second(getDate(startDate));
	}

	private Optional<ICriterion> createConceptCriterion(Query query, ResourceField codeField) {
		return query.getCriteria()
				.concepts()
				.map(conceptCriterion -> createConceptCriterion(conceptCriterion, codeField));
	}

	private ICriterion createConceptCriterion(ConceptCriterion conceptCriterion, ResourceField codeField) {
		List<String> codes = conceptCriterion.getConcepts()
				.stream()
				.map(ConceptCode::getCode)
				.collect(Collectors.toList());
		return new TokenClientParam(codeField.getField()).exactly()
				.codes(codes);
	}

	private SortOrderEnum map(OrdinalCriterion.Ordinal ordinal) {
		return ordinal.equals(OrdinalCriterion.Ordinal.FIRST) ? SortOrderEnum.ASC : SortOrderEnum.DESC;
	}

	public static final class ResourceFields {
		private final ResourceField patientField;
		private final ResourceField dateField;
		private final ResourceField codeField;

		private ResourceFields(Builder builder) {
			patientField = builder.patientField;
			dateField = builder.dateField;
			codeField = builder.codeField;
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public ResourceField getPatientField() {
			return patientField;
		}

		public ResourceField getDateField() {
			return dateField;
		}

		public ResourceField getCodeField() {
			return codeField;
		}

		public static final class Builder {
			private ResourceField patientField;
			private ResourceField dateField;
			private ResourceField codeField;

			private Builder() {
			}

			public Builder withPatientField(ResourceField patientField) {
				this.patientField = patientField;
				return this;
			}

			public Builder withDateField(ResourceField dateField) {
				this.dateField = dateField;
				return this;
			}

			public Builder withCodeField(ResourceField codeField) {
				this.codeField = codeField;
				return this;
			}

			public ResourceFields build() {
				return new ResourceFields(this);
			}
		}
	}

	public static final class ResourceField {
		private final String field;

		private ResourceField(String field) {
			this.field = field;
		}

		public static ResourceField of(String field) {
			return new ResourceField(field);
		}

		public String getField() {
			return field;
		}
	}
}
