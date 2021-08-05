package com.custodix.insite.local.ehr2edc.query.mongo.criteria;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.query.executor.common.domain.Interval;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.ConceptCriterion;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.ExcludeConceptsCriterion;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.FreshnessCriterion;
import com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion;

@Component
public class MongoCriteriaFactory {
	private static final String FIELD_SUBJECT_ID = "subjectId";

	public Criteria createSubjectCriterion(SubjectCriterion subjectCriterion) {
		return where(FIELD_SUBJECT_ID).is(subjectCriterion.getSubjectId());
	}

	public Criteria createAll(com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria criteria,
			LocalDate referenceDate, String conceptField, String dateField) {
		List<Criteria> criteriaList = new ArrayList<>();
		criteriaList.add(this.createSubjectCriterion(criteria.subject()));
		this.createConceptCriterion(criteria, conceptField)
				.ifPresent(criteriaList::add);
		this.createExcludeConceptsCriterion(criteria, conceptField)
				.ifPresent(criteriaList::add);
		this.createFreshnessCriterion(criteria, referenceDate, dateField)
				.ifPresent(criteriaList::add);
		return new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
	}

	public Optional<Criteria> createConceptCriterion(
			com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria criteria,
			String conceptField) {
		return criteria.concepts()
				.map(crit -> doCreateConceptsCriterion(crit, conceptField));
	}

	public Optional<Criteria> createExcludeConceptsCriterion(
			com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria criteria,
			String conceptField) {
		return criteria.excludeConcepts()
				.map(crit -> doCreateExcludeConceptsCriterion(crit, conceptField));
	}

	public Optional<Criteria> createFreshnessCriterion(
			com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria criteria,
			LocalDate referenceDate, String dateField) {
		return criteria.freshness()
				.map(crit -> doCreateFreshnessCriterion(crit, referenceDate, dateField));
	}

	public Optional<Criteria> createFreshnessCriterion(
			com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.Criteria criteria,
			LocalDate referenceDate, String startDateField, String endDateField) {
		return criteria.freshness()
				.map(crit -> doCreateFreshnessCriterion(crit, referenceDate, startDateField, endDateField));
	}

	private Criteria doCreateConceptsCriterion(ConceptCriterion conceptCriterion, String conceptField) {
		return where(conceptField).in(conceptCriterion.getConcepts());
	}

	private Criteria doCreateExcludeConceptsCriterion(ExcludeConceptsCriterion excludeConceptsCriterion,
			String conceptField) {
		return where(conceptField).nin(excludeConceptsCriterion.getConcepts());
	}

	private Criteria doCreateFreshnessCriterion(FreshnessCriterion freshnessCriterion, LocalDate referenceDate,
			String dateField) {
		Interval interval = freshnessCriterion.getInterval(referenceDate);
		return createDateInIntervalCriterion(interval, dateField);
	}

	private Criteria doCreateFreshnessCriterion(FreshnessCriterion freshnessCriterion, LocalDate referenceDate,
			String startDateField, String endDateField) {
		Interval interval = freshnessCriterion.getInterval(referenceDate);
		Criteria withoutEndDateInInterval = createWithoutEndDateInIntervalCriterion(interval, startDateField,
				endDateField);
		Criteria withEndDateInInterval = createWithEndDateInIntervalCriterion(interval, startDateField, endDateField);
		return new Criteria().orOperator(withoutEndDateInInterval, withEndDateInInterval);
	}

	private Criteria createWithoutEndDateInIntervalCriterion(Interval interval, String startDateField,
			String endDateField) {
		Criteria startDateInPeriod = createDateInIntervalCriterion(interval, startDateField);
		Criteria endDateUnknown = new Criteria().orOperator(where(endDateField).exists(false),
				where(endDateField).is(null));
		return new Criteria().andOperator(startDateInPeriod, endDateUnknown);
	}

	private Criteria createWithEndDateInIntervalCriterion(Interval interval, String startDateField,
			String endDateField) {
		Criteria startDateBeforeEndOfInterval = where(startDateField).lt(interval.getEndDate());
		Criteria endDateAfterStartOfInterval = where(endDateField).gte(interval.getStartDate());
		return new Criteria().andOperator(startDateBeforeEndOfInterval, endDateAfterStartOfInterval);
	}

	private Criteria createDateInIntervalCriterion(Interval interval, String dateField) {
		Criteria dateAfterStartOfInterval = where(dateField).gte(interval.getStartDate());
		Criteria dateBeforeEndOfInterval = where(dateField).lt(interval.getEndDate());
		return new Criteria().andOperator(dateAfterStartOfInterval, dateBeforeEndOfInterval);
	}
}
