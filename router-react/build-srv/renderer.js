"use strict";

Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.renderFullPage = renderFullPage;

function renderFullPage(html, initialState) {
    return "\n    <!DOCTYPE html>\n    <html lang=\"en\">\n        <head>\n            <meta charset=\"UTF-8\"/>\n            <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chroome=1\"/>\n            <meta name=\"viewport\" content=\"width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no\"/>\n            <title>Simplest React</title>\n        </head>\n        <body>\n            <div id=\"root\">".concat(html, "</div>\n        <script type=\"text/javascript>\n        window.__PRELOADED_STATE__ = ").concat(JSON.stringify(initialState).replace(/</g, "\\u003c"), "\n        </script>\n        <script type=\"text/javascript\" src=\"bundle.js\"></script></body>\n    </html>");
}
