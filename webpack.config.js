const path = require('path');
const webpack = require('webpack');
const {VueLoaderPlugin} = require('vue-loader');
/**
 * `..` Since this config file is in the config folder so we need
 * to resolve path in the top level folder.
 */
const resolve = relativePath => path.resolve(__dirname, '.', relativePath);

module.exports = {
    mode: 'development',
    entry: {
        // Since we need to load vue in the entry page.
        vue: 'vue',
        // This is where the `main-content` component is
        index: resolve('src/main.js'),
    },
    output: {
        filename: '[name].js',
        // Folder where the output of webpack's result go.
        path: resolve('dist'),
    },
    module: {
        rules: [
            {
                // vue-loader config to load `.vue` files or single file components.
                test: /\.vue$/,
                loader: 'vue-loader',
                options: {
                    loaders: {
                        // https://vue-loader.vuejs.org/guide/scoped-css.html#mixing-local-and-global-styles
                        css: ['vue-style-loader', {
                            loader: 'css-loader',
                        }],
                        js: [
                            'babel-loader',
                        ],
                    },
                    cacheBusting: true,
                },
            },
            {
                test: /\.(ttf|eot|svg|png|jpg)(\?v=\d+\.\d+\.\d+)?$/,
                use: [{
                    loader: 'file-loader',
                    options: {
                        name: '[name].[ext]',
                        outputPath: 'img'
                    },
                }]
            },
            {
                test: /\.(woff(2)?|html)(\?v=\d+\.\d+\.\d+)?$/,
                use: [{
                    loader: 'file-loader',
                    options: {
                        name: '[name].[ext]',
                        outputPath: ''
                    },
                }]
            },
            {
                test: /\.css?$/,
                use: [
                    'vue-style-loader',
                    'css-loader'
                ]
            },
            {
                test: /\.scss$/,
                use: [
                    "vue-style-loader", // creates style nodes from JS strings
                    "css-loader", // translates CSS into CommonJS
                    "sass-loader" // compiles Sass to CSS, using Node Sass by default
                ],
                include: [
                    resolve('src/css'),
                ],
            },
            {
                // This is required for other javascript you are gonna write besides vue.
                test: /\.js$/,
                loader: 'babel-loader',
                include: [
                    resolve('src'),
                    resolve('node_modules/webpack-dev-server/client'),
                ],
            },
        ],
    },
    /**
     * There are multiple devtools available, check
     * https://github.com/webpack/webpack/tree/master/examples/source-map
     */
    devtool: 'eval',
    devServer: {
        // The url you want the webpack-dev-server to use for serving files.
        host: '0.0.0.0',
        // Can be the popular 8080 also.
        port: 8020,
        // gzip compression
        compress: true,
        // Open the browser window, set to false if you are in a headless browser environment.
        open: false,
        watchOptions: {
            ignored: /node_modules/,
            poll: true,
        },
        // The path you want webpack-dev-server to use for serving files
        publicPath: '/dist/',
        // For static assets
        contentBase: resolve('dist'),
        // Reload for code changes to static assets.
        watchContentBase: true,
    },
    plugins: [
        new VueLoaderPlugin(),
        new webpack.NamedModulesPlugin(),
        // Exchanges, adds, or removes modules while an application is running, without a full reload.
        new webpack.HotModuleReplacementPlugin(),
    ],
    resolve: {
        /**
         * The compiler-included build of vue which allows to use vue templates
         * without pre-compiling them
         */
        modules: [
            resolve('./src/'),
            resolve('./node_modules/'),
        ],
        alias: {
            'vue$': 'vue/dist/vue.esm.js',
        },
        extensions: ['*', '.vue', '.js', '.json'],
    },
    // webpack outputs performance related stuff in the browser.
    performance: {
        hints: false,
    },
};