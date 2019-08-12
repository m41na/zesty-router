let templates = {}

//render and cache compiled templates
function renderTemplate(template, model, url){
    let compiledTemplate;
    if(templates[url] == undefined){
        compiledTemplate = ejs.compile(template, {});
        templates[url] = compiledTemplate;
    }
    else{
        compiledTemplate = templates[url]
    }
    return compiledTemplate(toJsonObject(model));
}

//create json object from json input string
function toJsonObject(model){
    return JSON.parse(model);
}