package eu.ehr4cr.workbench.local.usecases.clinical.study.create;

import java.util.Date;

import eu.ehr4cr.workbench.local.annotation.Nullable;

public interface CreateClinicalStudyInfo {
	String getPublicId();

	String getName();

	@Nullable
	String getDescription();

	String getAuthor();

	String getSponsor();

	Date getCreationDate();

	Date getModificationDate();

	long getGoal();

	@Nullable
	Date getDeadline();
}
