let templates = {}

//render and cache compiled templates
function renderTemplate(template, model, url) {
    let compiledTemplate;
    if (templates[url] == undefined) {
        compiledTemplate = Handlebars.compile(template);
        templates[url] = compiledTemplate;
    } else {
        compiledTemplate = templates[url]
    }
    return compiledTemplate(toJsonObject(model));
}

//create json object from json input string
function toJsonObject(model) {
    return JSON.parse(model);
}

//create a real json object from the java map
function toJsonModel(model) {
    let json = {};
    for (key in model) {
        if (model[key] instanceof Java.type("java.lang.Iterable")) {
            json[key] = Java.from(model[key])
        } else {
            json[key] = model[key]
        }
    }
    return json;
}
