package com.custodix.insite.local.ehr2edc.query.fhir;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.BaseResource;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class ReferencedResourceLoader<T extends BaseResource> {
	private final FhirResourceRepository<T> resourceRepository;

	public ReferencedResourceLoader(IGenericClient client, Class<T> entryClass) {
		this.resourceRepository = new FhirResourceRepository<>(client, entryClass);
	}

	public void load(ResourceReferenceDt resourceReference) {
		if (resourceReference.getResource() == null && !resourceReference.isEmpty()) {
			T resource = resourceRepository.get(resourceReference.getReference());
			resourceReference.setResource(resource);
		}
	}
}
