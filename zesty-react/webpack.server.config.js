var path = require("path");
const nodeExternals = require('webpack-node-externals');

module.exports = {
  entry: ["./srv/index.js"],
  output: {
    path: path.join(__dirname, "build-srv"),
    filename: "wstart.js"
  },
  module: {
    rules: [
      {
        test: /(\.jsx|\.js)$/,
        loader: "babel-loader",
        exclude: /(node_modules|public_libs)/
      },
      {
        test: /\.css$/i,
        use: ["style-loader", "css-loader"]
      }
    ]
  },
  mode: "production",
  externals: [nodeExternals()]
};
