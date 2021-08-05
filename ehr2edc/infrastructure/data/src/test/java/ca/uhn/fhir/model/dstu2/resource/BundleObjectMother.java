package ca.uhn.fhir.model.dstu2.resource;

public class BundleObjectMother {

	public static Bundle aDefaultBundle() {
		return new Bundle();
	}

	public static Bundle.Entry aDefaultBundleEntry() {
		return new Bundle.Entry();
	}
}
