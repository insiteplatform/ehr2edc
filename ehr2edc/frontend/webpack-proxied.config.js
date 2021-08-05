const Config = require('./webpack.config');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const Externals = require('./config/externals.config');

module.exports = () => {
    return Promise.all([Externals.values]).then(function (values) {
        return Promise.resolve(Object.assign(Config,
            {
            plugins: [
                new HtmlWebpackPlugin({
                    template: __dirname + '/react/dev/proxied.html',
                    filename: __dirname + '/dist/showcase.html',
                    title: 'React Kitchen Sink',
                    pageHeader: 'React Showcase',
                    header: values[0],
                    libs: values[1],
                    config: values[2]
                }),
            ],
            devServer: {
                contentBase: __dirname + '/dist',
                watchContentBase: true,
                compress: true,
                port: 9000,
                headers: {
                    'Access-Control-Allow-Origin': '*',
                    'Access-Control-Allow-Headers': '*',
                },
                proxy: [
                    {
                        context: ['/**', '!/public/javascripts/bundle*.js'],
                        target: process.env.PROXY_TARGET,
                        secure: false,
                        changeOrigin: true,
                        autoRewrite: true,
                        protocolRewrite: 'http'
                    }
                ],
                historyApiFallback: {
                    rewrites: [
                        {from: /^\/public\/javascripts\/bundle(.*).js/, to: '/bundle.js'},
                    ]
                }
            }
        }));
    });
};


