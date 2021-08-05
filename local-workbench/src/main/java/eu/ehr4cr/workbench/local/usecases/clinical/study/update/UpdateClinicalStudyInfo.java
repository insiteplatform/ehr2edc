package eu.ehr4cr.workbench.local.usecases.clinical.study.update;

import java.util.Date;

import eu.ehr4cr.workbench.local.annotation.Nullable;

public interface UpdateClinicalStudyInfo {
	String getPublicId();

	String getName();

	@Nullable
	String getDescription();

	String getAuthor();

	String getSponsor();

	Date getModificationDate();

	long getGoal();
}
