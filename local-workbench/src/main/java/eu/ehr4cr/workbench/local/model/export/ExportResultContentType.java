package eu.ehr4cr.workbench.local.model.export;

public enum ExportResultContentType {
	PATIENTS("Patients"),
	PATIENTFACTS("Patients with clinical facts");

	private String value;

	ExportResultContentType(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
