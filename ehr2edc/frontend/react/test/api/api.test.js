import React from 'react'
import mockAxios from 'jest-mock-axios';
import api from "../../src/api/api";

const base_url = "/api/test";
const testApi = api.posts(base_url);

afterEach(() => {
   mockAxios.reset();
});

test("getAll calls the correct url", async () => {
    const testData = generateTestObjects();
    letWebMethodReturnData(testData, mockAxios.get);

    testApi.getAll().then(resp => {
        expect(resp.data).toBe(testData);

    });

    await Promise.resolve();
    assertUrlMatches(base_url, mockAxios.get);
});

test("getOne calls the correct url", async () => {
    const testData = generateTestObject(1, "Test item nr. 1");
    letWebMethodReturnData(testData, mockAxios.get);

    testApi.getOne({id: 1}).then(resp => {
        expect(resp.data).toBe(testData);

    });

    await Promise.resolve();
    assertUrlMatches(base_url + "/1", mockAxios.get);
});

test("update calls the correct url", async () => {
    const testData = "OK";
    letWebMethodReturnData(testData, mockAxios.put);

    testApi.update({id: 1, name: "Updated value"}).then(resp => {
        expect(resp.data).toBe(testData);
    });

    await Promise.resolve();
    assertUrlMatchesWithBody(base_url, mockAxios.put);
});

test("create calls the correct url", async () => {
    const testData = "OK";
    letWebMethodReturnData(testData, mockAxios.put);

    testApi.create({name: "Updated value"}).then(resp => {
        expect(resp.data).toBe(testData);
    });

    await Promise.resolve();
    assertUrlMatchesWithBody(base_url, mockAxios.put);
});

test("delete calls the correct url", async () => {
    const testData = "OK";
    letWebMethodReturnData(testData, mockAxios.delete);

    testApi.delete({id: 1}).then(resp => {
        expect(resp.data).toBe(testData);
    });

    await Promise.resolve();
    assertUrlMatches(base_url + "/1", mockAxios.delete);
});


function letWebMethodReturnData(data, method) {
    const response = {data: data};
    method.mockImplementation(() => Promise.resolve(response));
}

function generateTestObjects() {
    let list = [];
    for (let i = 0; i < 5; i++) {
        list.push(generateTestObject(i, "Name " + i))
    }
    return list;
}

function generateTestObject(id, name) {
    return {
        'id': id,
        'name': name
    }
}

function assertUrlMatches(expectedUrl, method) {
    expect(method).toHaveBeenCalledTimes(1);
    expect(method).toHaveBeenCalledWith(expectedUrl);
}

function assertUrlMatchesWithBody(expectedUrl, method) {
    expect(method).toHaveBeenCalledTimes(1);
    expect(method).toHaveBeenCalledWith(expectedUrl, expect.anything());
}