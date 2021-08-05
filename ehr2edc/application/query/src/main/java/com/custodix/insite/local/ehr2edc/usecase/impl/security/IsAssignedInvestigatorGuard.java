package com.custodix.insite.local.ehr2edc.usecase.impl.security;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEvent;
import com.custodix.insite.local.ehr2edc.populator.PopulatedEventRepository;
import com.custodix.insite.local.ehr2edc.query.security.IsAssignedInvestigator;
import com.custodix.insite.local.ehr2edc.shared.annotations.AuthorizationCorrelator;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.shared.annotations.security.GuardCheck;
import com.custodix.insite.local.ehr2edc.shared.annotations.util.CorrelatorLookup;
import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEvent;
import com.custodix.insite.local.ehr2edc.submitted.SubmittedEventRepository;
import com.custodix.insite.local.ehr2edc.user.GetCurrentUser;
import com.custodix.insite.local.ehr2edc.vocabulary.*;

@GuardCheck
@Query
class IsAssignedInvestigatorGuard implements IsAssignedInvestigator {

	private final GetCurrentUser getCurrentUser;
	private final StudyRepository studyRepository;
	private final PopulatedEventRepository populatedEventRepository;
	private final SubmittedEventRepository submittedEventRepository;

	private final CorrelatorLookup correlatorLookup;

	IsAssignedInvestigatorGuard(GetCurrentUser getCurrentUser, StudyRepository studyRepository,
			PopulatedEventRepository populatedEventRepository, SubmittedEventRepository submittedEventRepository) {
		this.getCurrentUser = getCurrentUser;
		this.studyRepository = studyRepository;
		this.populatedEventRepository = populatedEventRepository;
		this.submittedEventRepository = submittedEventRepository;
		this.correlatorLookup = CorrelatorLookup.ofType(AuthorizationCorrelator.class);
	}

	@Override
	public void checkPermission(final List<Object> correlatorAncestors) {
		UserIdentifier currentUser = getCurrentUser.getUserId();
		if (currentUser != null) {
			correlatorLookup.findAllRecursively(correlatorAncestors)
					.forEach(correlator -> verifyForCorrelator(correlator, currentUser));
		}
	}

	private void verifyForCorrelator(Object correlator, UserIdentifier user) {
		if(correlator==null) {
			accessDenied();
			return;
		}
		try {
			this.getClass()
					.getDeclaredMethod("verifyAuthorizedInvestigator", correlator.getClass(), user.getClass())
					.invoke(this, correlator, user);
		} catch (IllegalAccessException | NoSuchMethodException e) {
			throw new SystemException("Unable to verify if user is assigned investigator", e);
		} catch (InvocationTargetException e) {
			handleInvocationException(e);
		}
	}

	private void accessDenied() {
		throw new AccessDeniedException("User is not an assigned Investigator");
	}

	private void handleInvocationException(InvocationTargetException e) {
		if (e.getTargetException() instanceof AccessDeniedException) {
			throw (AccessDeniedException) e.getTargetException();
		}
		accessDenied();
	}

	private void verifyAuthorizedInvestigator(StudyId studyId, UserIdentifier userIdentifier) {
		Study study = studyRepository.getStudyById(studyId);
		if (study.hasNoInvestigatorFor(userIdentifier)) {
			accessDenied();
		}
	}

	private void verifyAuthorizedInvestigator(SubjectId subjectId, UserIdentifier userIdentifier) {
		Study study = studyRepository.getBySubjectId(subjectId);
		if (study.hasNoInvestigatorFor(userIdentifier)) {
			accessDenied();
		}
	}

	private void verifyAuthorizedInvestigator(EventId eventId, UserIdentifier userIdentifier) {
		PopulatedEvent event = populatedEventRepository.getEvent(eventId);
		StudyId studyId = event.getStudyId();
		verifyAuthorizedInvestigator(studyId, userIdentifier);
	}

	private void verifyAuthorizedInvestigator(SubmittedEventId eventId, UserIdentifier userIdentifier) {
		SubmittedEvent event = submittedEventRepository.get(eventId);
		StudyId studyId = event.getStudyId();
		verifyAuthorizedInvestigator(studyId, userIdentifier);
	}
}
