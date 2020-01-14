"use strict";

Object.defineProperty(exports, "__esModule", {
    value: true
});
exports["default"] = void 0;

var _path = _interopRequireDefault(require("path"));

var _express = _interopRequireDefault(require("express"));

var _cors = _interopRequireDefault(require("cors"));

var _router = _interopRequireDefault(require("./router"));

function _interopRequireDefault(obj) {
    return obj && obj.__esModule ? obj : {"default": obj};
}

var publicPath = _express["default"]["static"](_path["default"].join(__dirname, '../build'));

var app = (0, _express["default"])();
app.use((0, _cors["default"])());
app.use(publicPath);
app.get('*', _router["default"]);
var _default = app;
exports["default"] = _default;
