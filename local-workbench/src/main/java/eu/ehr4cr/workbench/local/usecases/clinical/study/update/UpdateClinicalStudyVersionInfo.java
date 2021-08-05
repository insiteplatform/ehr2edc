package eu.ehr4cr.workbench.local.usecases.clinical.study.update;

import java.util.Map;

public interface UpdateClinicalStudyVersionInfo {
	String getName();

	String getQueryContent();

	Map<String, byte[]> getDocuments();
}
