const CopyWebpackPlugin = require('copy-webpack-plugin');

const path = require('path');

module.exports = {
    mode: 'development',
    entry: './src/main/javascript/main.js',
    output: {
        path: path.resolve(__dirname, 'build/public/bpmn-editor-ui'),
        filename: 'main.js'
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
            }
        ]
    },
    plugins: [
        new CopyWebpackPlugin({
            patterns: [
                {from: 'src/main/resources/ui/bpmn-editor.html', to: '.'}
            ]
        })
    ],
    resolve: {
        extensions: ['.js', '.jsx']
    }
};