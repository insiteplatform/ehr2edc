const Config = require('./webpack.config');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const entries = [
    ...Config.entry
];
const cypressConfig = Object.assign(
    Config,
    {
        mode: 'development',
        entry: entries,
        plugins: [
            new HtmlWebpackPlugin({
                template: "./react/dev/index.html",
                filename: "./index.html"
            })
        ]
    }
);

module.exports = cypressConfig;