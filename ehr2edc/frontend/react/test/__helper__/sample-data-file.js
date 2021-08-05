'use strict';
const fs = require('fs');
const path = require("path");
const webserviceResponseFolder = "../../../../infrastructure/web/src/test/resources/samples/";

export default function readDataFromFile(filePath) {
    const fileContents = fs.readFileSync(path.resolve(__dirname, webserviceResponseFolder + filePath), 'utf-8');
    return JSON.parse(fileContents);
}