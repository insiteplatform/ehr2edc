package eu.ehr4cr.workbench.local.security.annotation;

import eu.ehr4cr.workbench.local.vocabulary.clinical.StudyIdentifier;

public interface HasClinicalStudyIdentifier {
	StudyIdentifier getStudyIdentifier();
}
