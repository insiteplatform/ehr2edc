package eu.ehr4cr.workbench.local.usecases.clinical.study.create;

import java.util.Map;

public interface CreateClinicalStudyVersionInfo {
	String getName();

	String getQueryContent();

	Map<String, byte[]> getDocuments();
}
