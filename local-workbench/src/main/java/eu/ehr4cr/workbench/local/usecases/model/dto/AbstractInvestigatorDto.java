package eu.ehr4cr.workbench.local.usecases.model.dto;

import eu.ehr4cr.workbench.local.model.security.User;

interface AbstractInvestigatorDto {
	Long getId();

	User getUser();

	boolean isOccupied();
}
