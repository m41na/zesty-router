import {renderToString} from 'react-dom/server';
import {renderFullPage} from './renderer';
import React from 'react';
import routes from './routes';
import {matchPath, StaticRouter} from 'react-router-dom';
import products from "../src/store";
import App from '../src/App';

export default function(req, resp){

    const match = routes.reduce((acc, route) => matchPath(req.url, {path: route, exact: true}) || acc, null);

    if(!match){
        resp.status(400).send('page not found')
        return
    }

    let context = {}    

    return new Promise(function(resolve, reject){
        let htmlString = renderToString(
            <StaticRouter context={context} location={req.url}>
                <App products={products} />
            </StaticRouter>
        );
    
        resolve(resp.status(200).send(renderFullPage(htmlString, products)))
        reject(resp.status(400).send("problem in heaven"))
    });
}