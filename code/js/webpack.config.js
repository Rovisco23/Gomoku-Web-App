const path = require('path')

module.exports = {
    mode: 'development',
    resolve: {
        extensions: ['.tsx', '.ts', '.js'],
    },
    module: {
        rules: [
            {
                test: /.tsx?$/,
                use: 'ts-loader',
                exclude: /node_modules/,
            },
            {
                test: /.(png|jpg|jpeg|gif)$/i,
                use: [
                    {
                        loader: 'file-loader',
                        options: {
                            name: '[name].[ext]',
                            outputPath: 'images',
                        },
                    },
                ],
            }
        ],
    },

    devServer: {
        static: path.resolve(__dirname, 'dist'),
        historyApiFallback: true,
        compress: false,
        port: 8000,
        proxy: {
            '/api': {
                target: 'http://localhost:9000',
                changeOrigin: true
            }
        }
    }
}
