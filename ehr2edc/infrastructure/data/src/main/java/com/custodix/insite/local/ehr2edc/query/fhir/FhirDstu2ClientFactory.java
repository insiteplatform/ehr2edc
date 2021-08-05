package com.custodix.insite.local.ehr2edc.query.fhir;

import java.net.URI;

import com.custodix.insite.local.ehr2edc.EHRConnectionRepository;
import com.custodix.insite.local.ehr2edc.query.fhir.demographic.OneUpHealthBearerTokenInterceptor;
import com.custodix.insite.local.ehr2edc.vocabulary.EHRConnection;
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class FhirDstu2ClientFactory {
    private final EHRConnectionRepository ehrConnectionRepository;
    private FhirContext fhirDstu2Context;

    public FhirDstu2ClientFactory(EHRConnectionRepository ehrConnectionRepository) {
        this.ehrConnectionRepository = ehrConnectionRepository;
    }

    private FhirContext getFhirDstu2Context() {
        if(fhirDstu2Context == null) {
            fhirDstu2Context = FhirContext.forDstu2();
        }
        return fhirDstu2Context;
    }

    public IGenericClient getFhirDstu2Client(StudyId studyId) {
        FhirContext fhirDstu2Context = getFhirDstu2Context();

        URI fhirURI = getFhirURI(studyId);
        IGenericClient fhirDstu2Client = fhirDstu2Context.newRestfulGenericClient(fhirURI.toString());

        fhirDstu2Client.registerInterceptor(new OneUpHealthBearerTokenInterceptor());
        return fhirDstu2Client;
    }

    private URI getFhirURI(StudyId studyId) {
        EHRConnection ehrConnection = ehrConnectionRepository.getByStudyId(studyId);
        return ehrConnection.getUri();
    }

}
