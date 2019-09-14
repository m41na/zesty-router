export function renderFullPage(html, initialState){
    return `
    <!DOCTYPE html>
    <html lang="en">
        <head>
            <meta charset="UTF-8"/>
            <meta http-equiv="X-UA-Compatible" content="IE=edge,chroome=1"/>
            <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no"/>
            <title>Simplest React</title>
        </head>
        <body>
            <div id="root">${html}</div>
        <script type="text/javascript>
        window.__PRELOADED_STATE__ = ${JSON.stringify(initialState).replace(/</g, '\\u003c')}
        </script>
        <script type="text/javascript" src="bundle.js"></script></body>
    </html>`;
}