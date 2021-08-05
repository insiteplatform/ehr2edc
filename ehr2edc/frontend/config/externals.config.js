const fetch = require("node-fetch");

const Externals = function () {
    const _getData = async function (url) {
        try {
            const response = await fetch(url);
            return response.text();
        } catch (error) {
            console.error('Error: Could not load ' + url + '(' + error + ')')
        }
    };
    const _header = function () {
        return process.env.PROXY_TARGET
            ? _getData(process.env.PROXY_TARGET + 'assets/index/html/header.html')
            : Promise.resolve('');
    };
    const _libs = function () {
        return process.env.PROXY_TARGET
            ? _getData(process.env.PROXY_TARGET + 'assets/index/html/libs.html')
            : Promise.resolve('');
    };
    const _config = function () {
        return process.env.PROXY_TARGET
            ? _getData(process.env.PROXY_TARGET + 'assets/index/html/config.html')
            : Promise.resolve('');
    };

    return {
        values: [_header(), _libs(), _config()]
    }
}();

exports = Externals;