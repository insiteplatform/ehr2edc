package eu.ehr4cr.workbench.local.usecases.userrecovery;

import java.util.Locale;

import eu.ehr4cr.workbench.local.model.security.User;

interface AbstractRetrieveUserSecurityQuestionRequest {
	User getUser();

	Locale getLocale();
}