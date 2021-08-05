import mockAxios from 'jest-mock-axios';
import {getAll} from "../../src/api/study-api";

describe("Study API", () => {
    it("Should get all studies from the appropriate endpoint", () => {
        getAll();

        expect(mockAxios.get).toHaveBeenCalledWith("/ehr2edc/studies");
    });
});