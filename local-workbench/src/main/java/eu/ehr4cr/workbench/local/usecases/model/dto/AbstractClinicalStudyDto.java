package eu.ehr4cr.workbench.local.usecases.model.dto;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import eu.ehr4cr.workbench.local.model.clinicalStudy.ClinicalStudyState;
import eu.ehr4cr.workbench.local.model.clinicalStudy.ExternalUser;

interface AbstractClinicalStudyDto {
	Long getId();

	String getName();

	String getDescription();

	Boolean getArchived();

	ClinicalStudyState getState();

	ExternalUser getSponsor();

	Long getGoal();

	Date getDeadline();

	Date getLastUpdated();

	Long getReached();

	Date getCreationDate();

	String getExternalId();

	boolean isDetailsViewPossible();

	Optional<Long> getDaysUntilDeadline();

	default boolean isInState(ClinicalStudyState... states) {
		return Arrays.stream(states)
				.anyMatch(getState()::equals);
	}
}