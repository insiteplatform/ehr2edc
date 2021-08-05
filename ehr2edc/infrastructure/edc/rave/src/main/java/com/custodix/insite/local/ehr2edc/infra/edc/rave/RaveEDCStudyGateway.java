package com.custodix.insite.local.ehr2edc.infra.edc.rave;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.infra.edc.api.SpecificEDCStudyGateway;
import com.custodix.insite.local.ehr2edc.infra.edc.rave.model.response.RaveErrorResponse;
import com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.RaveODMSerializer;
import com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.mapper.ClinicalDataODMMapper;
import com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.ClinicalData;
import com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.ODM;
import com.custodix.insite.local.ehr2edc.infra.edc.rave.odm.model.SubjectData;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

class RaveEDCStudyGateway implements SpecificEDCStudyGateway {

	private static final Logger LOGGER = getLogger(RaveEDCStudyGateway.class);

	private final RaveODMSerializer odmSerializer;
	private final SubmitReviewedEventErrorResponseHandler submitReviewedEventErrorResponseHandler;
	private final RaveRestClient restClient;

	RaveEDCStudyGateway(RaveODMSerializer odmSerializer,
			SubmitReviewedEventErrorResponseHandler submitReviewedEventErrorResponseHandler,
			RaveRestClient restClient) {
		this.odmSerializer = odmSerializer;
		this.submitReviewedEventErrorResponseHandler = submitReviewedEventErrorResponseHandler;
		this.restClient = restClient;
	}

	@Override
	public boolean supports(EDCSystem edcSystem) {
		return edcSystem == EDCSystem.RAVE;
	}

	@Override
	public List<EDCSubjectReference> findRegisteredSubjectIds(ExternalEDCConnection connection) {
		List<ClinicalData> clinicalDatas = getClinicalDataFromEDC(connection);
		return clinicalDatas.stream()
				.flatMap(clinicalData -> {
					if (hasStudyId(connection.getStudyIdOverride()
							.orElse(connection.getStudyId())).test(clinicalData)) {
						return clinicalData.getSubjectData()
								.stream()
								.filter(hasSiteId(connection.getExternalSiteId()))
								.map(SubjectData::getSubjectKey)
								.map(EDCSubjectReference::of);
					}
					return Stream.empty();
				})
				.collect(Collectors.toList());
	}

	@Override
	public boolean isRegisteredSubject(ExternalEDCConnection connection, EDCSubjectReference reference) {
		return findRegisteredSubjectIds(connection).contains(reference);
	}

	@Override
	public void createSubject(ExternalEDCConnection connection, Study study, EDCSubjectReference reference) {
		createSubject(study, reference, connection);
	}

	@Override
	public String submitReviewedEvent(ExternalEDCConnection connection, SubmittedEvent reviewedEvent, Study study) {
		return this.submitReviewedEvent(reviewedEvent, study, connection);
	}

	private void createSubject(Study study, EDCSubjectReference reference, ExternalEDCConnection connection) {
		if (connection.isEnabled()) {
			ODM subjectODM = ClinicalDataODMMapper.mapSubjectODMFor(study, reference, connection);

			try {
				restClient.postXml(connection.getClinicalDataURI(), odmSerializer.serialize(subjectODM),
						connection.getUsername(), connection.getPassword());
			} catch (HttpClientErrorException hcee) {
				RaveErrorResponse raveErrorResponse = submitReviewedEventErrorResponseHandler.convertToRaveErrorResponse(
						hcee);

				if (subjectAlreadyExists(raveErrorResponse)) {
					LOGGER.warn("Subject already exists, edc reference: {} connection: {}", reference.getId(),
							connection.getClinicalDataURI(), hcee);
				} else {
					throw new SystemException("Error during rave communication", hcee);
				}

			} catch (Exception e) {
				LOGGER.error("Could not register subject in edc, with "
								+ "connection URI: {}, siteId: {}, edcSubjectReference: {}, studyId: {}",
						connection.getClinicalDataURI(), connection.getExternalSiteId(), reference.getId(),
						study.getStudyId()
								.getId(), e);
				throw new SystemException("Error during rave communication", e);
			}
		}
	}

	private String submitReviewedEvent(SubmittedEvent reviewedEvent, Study study, ExternalEDCConnection connection) {
		if (connection.isEnabled()) {
			ODM subjectODM = ClinicalDataODMMapper.mapSubmittedEventODMFor(reviewedEvent, study, connection);
			String serializedOdmXml = serializeToOdmXML(subjectODM);

			submit(reviewedEvent, serializedOdmXml, connection);

			return serializedOdmXml;
		}
		return null;
	}

	private boolean subjectAlreadyExists(RaveErrorResponse raveErrorResponse) {
		return RaveErrorResponse.KnownRaveReasons.SUBJECT_ALREADY_EXISTS.getReasonCode()
				.equalsIgnoreCase(raveErrorResponse.getReasonCode());
	}

	private void submit(SubmittedEvent reviewedEvent, String serializedOdmXml, ExternalEDCConnection connection) {
		try {
			restClient.postXml(connection.getClinicalDataURI(), serializedOdmXml, connection.getUsername(),
					connection.getPassword());
		} catch (HttpClientErrorException hcee) {
			submitReviewedEventErrorResponseHandler.convertToConstraintViolation(reviewedEvent, hcee);
		}
	}

	private String serializeToOdmXML(ODM subjectODM) {
		return odmSerializer.serialize(subjectODM);
	}

	private List<ClinicalData> getClinicalDataFromEDC(ExternalEDCConnection externalEDCConnection) {
		if (externalEDCConnection.isEnabled()) {
			try {
				String odmXML = restClient.get(externalEDCConnection.getClinicalDataURI(),
						externalEDCConnection.getUsername(), externalEDCConnection.getPassword());
				ODM odm = odmSerializer.deserialize(odmXML);
				return odm.getClinicalData();
			} catch (RestClientException ex) {
				LOGGER.error("Unable to connect to external EDC @ {} ", externalEDCConnection.getClinicalDataURI()
						.toASCIIString(), ex);
			}
		}
		return emptyList();
	}

	private static Predicate<ClinicalData> hasStudyId(StudyId studyId) {
		return clinicalData -> nonNull(studyId) && nonNull(clinicalData) && Objects.equals(clinicalData.getStudyOID(),
				studyId.getId());
	}

	private static Predicate<SubjectData> hasSiteId(ExternalSiteId siteId) {
		return subjectData -> nonNull(siteId) && nonNull(subjectData) && nonNull(subjectData.getSiteRef())
				&& Objects.equals(subjectData.getSiteRef()
				.getLocationOID(), siteId.getId());
	}
}
