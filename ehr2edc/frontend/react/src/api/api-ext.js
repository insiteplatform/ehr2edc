import axios from "axios";

export const subjectApi = function (url) {
    return {
        getByStudyIdAndSubjectId: (studyId, subjectId) => axios.get(url
            .replace("${studyId}", studyId)
            .replace("${subjectId}", subjectId)),
    }
};

export const investigatorApi = function (studyId) {
    const url = `/ehr2edc/studies/${studyId}/investigators`;
    return {
        create: (body) => axios.put(url, body),
        delete: ({id}) => axios.delete(`${url}/${id}`)
    };
};

export const patientIdentifierSourcesApi = function (studyId) {
    return {
        getAll: () => axios.get(`/ehr2edc/patients/domains?studyId=${studyId}`)
    };
};

export const subjectObservationSummaryApi = function (studyId) {
    return {
        getOne: (subjectId) => axios.get(`/ehr2edc/studies/${studyId}/subjects/${subjectId}/observations`)
    };
};

export const eventDefinitionsApi = function (studyId) {
    return {
        getOne: (subjectId) => axios.get(`/ehr2edc/studies/${studyId}/subjects/${subjectId}/events`)
    };
};

export const getEventPopulationReadiness = function (studyId, subjectId) {
    if (typeof subjectId !== "string") {
        throw new Error("subjectId expected.");
    }
    if (typeof studyId !== "string") {
        throw new Error("studyId expected.");
    }

    return axios.get(`/ehr2edc/studies/${studyId}/subjects/${subjectId}/event-population-readiness`)
};

export const patientRegistrationApiForRegisterSubjectComponent = function (url) {
    return {
        put: (studyId, postData) => axios.put(url.replace("${studyId}", studyId), postData)
    }
};

export const patientRegistrationApiForStudyDetailsComponent = function (url) {
    return {
        put: (postData) => axios.put(url, postData),
    }
};

export function listAvailableSubjectFor(study) {
    if (typeof study === "string") {
        return axios.get(`/ehr2edc/edc/subjects?studyId=${study}`);
    }
    throw new Error("StudyId expected.");
}

export function listAvailablePatientFor(study, domain, filter) {
    if (typeof study === "string" && typeof domain === "string") {
        let actualFilter = typeof filter === "string" ? filter : "";
        return axios.get(`/ehr2edc/ehr/patients?limit=10&patientDomain=${domain}&studyId=${study}&filter=${actualFilter}`);
    }
    throw new Error("StudyId and domain are required.");
}

export function getEventPopulationHistory(studyId, subjectId, eventDefinitionId) {
    if (typeof studyId === "string" && typeof subjectId === "string" && typeof eventDefinitionId === "string") {
        return axios.get(`/ehr2edc/studies/${studyId}/subjects/${subjectId}/events/populated/history?eventDefinitionId=${eventDefinitionId}`);
    }
    throw new Error("studyId, subjectId and eventDefinitionId expected");
}

export function getEvent(studyId, subjectId, eventId) {
    if (typeof studyId === "string" && typeof subjectId === "string" && typeof eventId === "string") {
        return axios.get(`/ehr2edc/studies/${studyId}/subjects/${subjectId}/events/populated/${eventId}`);
    }
    throw new Error("studyId, subjectId and eventId expected");
}

export function getItemProvenance(studyId, subjectId, eventId, itemId) {
    if (typeof studyId === "string" && typeof subjectId === "string" && typeof eventId === "string" && typeof itemId === "string") {
        return axios.get(`/ehr2edc/studies/${studyId}/subjects/${subjectId}/events/populated/${eventId}/items/${itemId}/provenance`);
    }
    throw new Error("studyId, subjectId, eventId and itemId expected")
}

export function getSubmittedItemProvenance(studyId, subjectId, submittedEventId, submittedItemId) {
    if (typeof studyId === "string" && typeof subjectId === "string" && typeof submittedEventId === "string" && typeof submittedItemId === "string") {
        return axios.get(`/ehr2edc/studies/${studyId}/subjects/${subjectId}/events/submitted/${submittedEventId}/items/${submittedItemId}/provenance`);
    }
    throw new Error("studyId, subjectId, submittedEventId and submittedItemId expected")
}

export function submitReviewedForms(studyId, subjectId, reviewedEventRequestBody) {
    if (typeof studyId === "string" && typeof subjectId === "string") {
        return axios.post(`/ehr2edc/studies/${studyId}/subjects/${subjectId}/events/submitted`, reviewedEventRequestBody);
    }
    throw new Error("studyId, subjectId, submittedEventId and submittedItemId expected")
}

export function getSubject(studyId, subjectId) {
    if (typeof studyId === "string" && typeof subjectId === "string") {
        return axios.get(`/ehr2edc/studies/${studyId}/subjects/${subjectId}`);
    }
    throw new Error("studyId and subjectId expected");
}

export function getAllSubmittedEvents(studyId, subjectId, eventId) {
    if (typeof studyId === "string" && typeof subjectId === "string" && typeof eventId === "string") {
        return axios.get(`/ehr2edc/studies/${studyId}/subjects/${subjectId}/events/submitted/${eventId}/history`);
    }
    throw new Error("studyId, subjectId and eventId expected");
}
export function getSubmittedEvent(studyId, subjectId, eventId) {
    if (typeof studyId === "string" && typeof subjectId === "string" && typeof eventId === "string") {
        return axios.get(`/ehr2edc/studies/${studyId}/subjects/${subjectId}/events/submitted/${eventId}`);
    }
    throw new Error("studyId, subjectId and eventId expected");
}
export function delayedPromise() {
    return new Promise(resolve => {
        setTimeout(() => resolve(), 6000);
    });
}