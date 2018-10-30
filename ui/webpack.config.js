const path = require('path');
const HtmlWebPackPlugin = require("html-webpack-plugin");

const htmlWebpackPlugin = new HtmlWebPackPlugin({
	template : "./src/main/resources/index.html",
	filename : "views/index.html"
});

module.exports = {
	entry : "./src/main/js/app.js",
	devtool : 'sourcemaps',
	cache : true,
	stats : 'normal',
	output : {
		filename : 'js/bundled.js',
		publicPath: 'webjars/',
	},
	module : {
		rules : [ {
			test : /\.js$/,
			exclude : path.resolve(__dirname, 'node_modules'),
			use : {
				loader : 'babel-loader'
			}
		}, {
			test : /\.css$/,
			use : [ "style-loader", "css-loader" ]
		} ]
	},
	plugins : [ htmlWebpackPlugin ]
};