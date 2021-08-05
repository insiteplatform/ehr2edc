package com.custodix.insite.local.ehr2edc.query.mongo.laboratory.gateway;

import static com.custodix.insite.local.ehr2edc.query.executor.common.query.criteria.SubjectCriterion.subjectIs;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;

import org.bson.Document;
import org.springframework.stereotype.Component;

import com.custodix.insite.local.ehr2edc.populator.EHRGateway;
import com.custodix.insite.local.ehr2edc.populator.provenance.DataPointEHRGateway;
import com.custodix.insite.local.ehr2edc.query.executor.domain.Measurement;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.FastingStatus;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabConcept;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValue;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.domain.LabValueInterpretation;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LabValues;
import com.custodix.insite.local.ehr2edc.query.executor.laboratory.query.LaboratoryQuery;
import com.custodix.insite.local.ehr2edc.query.mongo.criteria.MongoCriteriaFactory;
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabConceptField;
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabMeasurementField;
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabValueDocument;
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.model.LabValueInterpretationField;
import com.custodix.insite.local.ehr2edc.query.mongo.laboratory.repository.LabValueRepository;
import com.custodix.insite.local.ehr2edc.vocabulary.SubjectId;

@Component
class LabValueDocumentGateway implements EHRGateway<LabValues, LaboratoryQuery>, DataPointEHRGateway {
	private final LabValueRepository labValueRepository;
	private final LabMongoQueryFactory labMongoQueryFactory;
	private final MongoCriteriaFactory mongoCriteriaFactory;

	public LabValueDocumentGateway(LabValueRepository labValueRepository, MongoCriteriaFactory mongoCriteriaFactory,
			LabMongoQueryFactory labMongoQueryFactory) {
		this.labValueRepository = labValueRepository;
		this.mongoCriteriaFactory = mongoCriteriaFactory;
		this.labMongoQueryFactory = labMongoQueryFactory;
	}

	@Override
	public List<String> findAllForSubject(SubjectId subjectId) {
		org.springframework.data.mongodb.core.query.Criteria criteria = mongoCriteriaFactory.createSubjectCriterion(
				subjectIs(subjectId));
		return labValueRepository.findAllDocuments(criteria)
				.stream()
				.map(Document::toJson)
				.collect(toList());
	}

	@Override
	public boolean canHandle(com.custodix.insite.local.ehr2edc.query.executor.common.query.Query<?> query) {
		return query instanceof LaboratoryQuery;
	}

	@Override
	public LabValues execute(LaboratoryQuery query, LocalDate referenceDate) {
		List<LabValueDocument> documents = labValueRepository.findAll(
				labMongoQueryFactory.createQuery(query.getCriteria(), referenceDate));
		return map(documents);
	}

	private LabValues map(List<LabValueDocument> labValueDocuments) {
		return labValueDocuments.stream()
				.map(this::map)
				.collect(collectingAndThen(toList(), LabValues::new));
	}

	private LabValue map(LabValueDocument document) {
		LabValue.Builder builder = LabValue.newBuilder()
				.forSubject(document.getSubjectId())
				.withStartDate(document.getStartDate())
				.withEndDate(document.getEndDate())
				.withVendor(document.getVendor());
		ofNullable(document.getLabConcept()).map(this::map)
				.ifPresent(builder::withLabConcept);
		ofNullable(document.getQuantitativeResult()).map(this::map)
				.ifPresent(builder::withQuantitativeResult);
		ofNullable(document.getQualitativeResult()).map(this::map)
				.ifPresent(builder::withQualitativeResult);
		return builder.build();
	}

	private LabConcept map(LabConceptField labConceptField) {
		return LabConcept.newBuilder()
				.withConcept(labConceptField.getConcept())
				.withComponent(labConceptField.getComponent())
				.withFastingStatus(FastingStatus.fromValue(labConceptField.getFastingStatus()))
				.withMethod(labConceptField.getMethod())
				.withSpecimen(labConceptField.getSpecimen())
				.build();
	}

	private Measurement map(LabMeasurementField measurementField) {
		return Measurement.newBuilder()
				.withLowerLimit(measurementField.getLowerLimit())
				.withUpperLimit(measurementField.getUpperLimit())
				.withValue(measurementField.getValue())
				.withUnit(measurementField.getUnit())
				.build();
	}

	private LabValueInterpretation map(LabValueInterpretationField labValueInterpretationField) {
		return LabValueInterpretation.newBuilder()
				.withParsedInterpretation(labValueInterpretationField.getParsedInterpretation())
				.withOriginalInterpretation(labValueInterpretationField.getOriginalInterpretation())
				.build();
	}
}
