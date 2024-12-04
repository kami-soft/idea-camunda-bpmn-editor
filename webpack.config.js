const TerserPlugin = require('terser-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const path = require('path');

module.exports = {
    optimization: {
        splitChunks: {
            chunks: 'all',
        },
        minimize: true,
        minimizer: [new TerserPlugin()],
    },
    mode: 'production',
    entry: './src/main/javascript/main.js',
    output: {
        path: path.resolve(__dirname, 'build/public/bpmn-editor-ui'),
        filename: '[name].[contenthash].js'
    },
    module: {
        rules: [
            {
                test: /\.less/,
                use: [
                    'style-loader',
                    'css-loader',
                    'less-loader'
                ]
            },
            {
                test: /\.css$/,
                use: [
                    'style-loader',
                    'css-loader',
                ]
            },
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader'
                }
            },
            {
                test: /\.(woff|woff2|ttf|otf|eot)$/i,
                type: 'asset/resource',
                generator: {
                    filename: 'font/[name][ext][query]'
                }
            },
            {
                test: /\.bpmnlintrc$/i,
                use: 'bpmnlint-loader',
            }
        ]
    },
    plugins: [
        new HtmlWebpackPlugin({
            template: 'src/main/resources/ui/bpmn-editor.html',
            inject: 'body',
            chunks: ['main']
        })
    ],
    resolve: {
        extensions: ['.js', '.jsx']
    }
};