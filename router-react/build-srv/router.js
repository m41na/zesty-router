"use strict";

Object.defineProperty(exports, "__esModule", {
    value: true
});
exports["default"] = _default;

var _server = require("react-dom/server");

var _renderer = require("./renderer");

var _react = _interopRequireDefault(require("react"));

var _routes = _interopRequireDefault(require("./routes"));

var _reactRouterDom = require("react-router-dom");

var _store = _interopRequireDefault(require("../src/store"));

var _App = _interopRequireDefault(require("../src/App"));

function _interopRequireDefault(obj) {
    return obj && obj.__esModule ? obj : {"default": obj};
}

function _default(req, resp) {
    var match = _routes["default"].reduce(function (acc, route) {
        return (0, _reactRouterDom.matchPath)(req.url, {
            path: route,
            exact: true
        }) || acc;
    }, null);

    if (!match) {
        resp.status(400).send('page not found');
        return;
    }

    var context = {};
    return new Promise(function (resolve, reject) {
        var htmlString = (0, _server.renderToString)(_react["default"].createElement(_reactRouterDom.StaticRouter, {
            context: context,
            location: req.url
        }, _react["default"].createElement(_App["default"], {
            products: _store["default"]
        })));
        resolve(resp.status(200).send((0, _renderer.renderFullPage)(htmlString, _store["default"])));
        reject(resp.status(400).send("problem in heaven"));
    });
}
