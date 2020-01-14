import path from "path";

require('ignore-styles')

require("@babel/register")({
    ignore: [/(node_modules)/],
    presets: ["@babel/preset-env", "@babel/preset-react"]
});

// Import the rest of our application.
module.exports = require(path.join(__dirname, "./index.js"));
