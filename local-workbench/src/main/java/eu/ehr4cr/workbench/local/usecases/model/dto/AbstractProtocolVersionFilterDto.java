package eu.ehr4cr.workbench.local.usecases.model.dto;

import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

import eu.ehr4cr.workbench.local.model.clinicalStudy.ExternalUser;
import eu.ehr4cr.workbench.local.model.security.User;

interface AbstractProtocolVersionFilterDto {
	Long getId();

	String getVersion();

	String getJsonContent();

	Boolean isArchived();

	Boolean isFromSponsor();

	Date getCreationDate();

	User getCreator();

	ExternalUser getSponsor();

	JsonNode getFormattedJsonContent();
}