import axios from "axios";

export const STUDY_ENDPOINT = '/ehr2edc/studies';

export const getAll = function() {
    return axios.get(STUDY_ENDPOINT);
};