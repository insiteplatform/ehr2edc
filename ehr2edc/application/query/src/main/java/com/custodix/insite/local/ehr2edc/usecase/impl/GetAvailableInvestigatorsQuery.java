package com.custodix.insite.local.ehr2edc.usecase.impl;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.query.GetAvailableInvestigators;
import com.custodix.insite.local.ehr2edc.shared.annotations.Query;
import com.custodix.insite.local.ehr2edc.user.User;
import com.custodix.insite.local.ehr2edc.user.UserRepository;

@Query
class GetAvailableInvestigatorsQuery implements GetAvailableInvestigators {

	private final UserRepository userRepository;
	private final StudyRepository studyRepository;

	GetAvailableInvestigatorsQuery(UserRepository userRepository, StudyRepository studyRepository) {
		this.userRepository = userRepository;
		this.studyRepository = studyRepository;
	}

	@Override
	public Response get(Request request) {
		Study study = studyRepository.getStudyById(request.getStudyId());

		List<User> potentialInvestigators = getPotentialInvestigators(study);
		return Response.newBuilder()
				.withPotentialInvestigators(mapUsersToPotentialInvestigators(potentialInvestigators))
				.build();
	}

	private List<User> getPotentialInvestigators(Study study) {
		return getUsersThatAreNotAlreadyAnInvestigatorFor(study);
	}

	private List<User> getUsersThatAreNotAlreadyAnInvestigatorFor(Study study) {
		return userRepository.getUsers()
				.stream()
				.filter(isUserNotAnInvestigatorFor(study))
				.collect(Collectors.toList());
	}

	private Predicate<User> isUserNotAnInvestigatorFor(Study study) {
		return user -> study.hasNoInvestigatorFor(user.getUserIdentifier());
	}

	private List<PotentialInvestigator> mapUsersToPotentialInvestigators(List<User> users) {
		return users.stream()
				.map(this::mapUserToPotentialInvestigator)
				.collect(Collectors.toList());
	}

	private PotentialInvestigator mapUserToPotentialInvestigator(User u) {
		return PotentialInvestigator.newBuilder()
				.withName(u.getName())
				.withUserId(u.getUserIdentifier())
				.build();
	}
}
