package eu.ehr4cr.workbench.local.usecases.schedulemanualfiltering;

import org.apache.commons.lang3.StringUtils;

class ManuallyFilteredPatientsResponse {
	private String message;
	private Long patientCount;

	public ManuallyFilteredPatientsResponse(String message, Long patientCount) {
		this.message = message;
		this.patientCount = patientCount;
	}

	public boolean isSucceeded() {
		return patientCount != null;
	}

	public boolean isFailed() {
		return StringUtils.isNotBlank(message);
	}

	public String getMessage() {
		return message;
	}

	public Long getPatientCount() {
		return patientCount;
	}
}
