let templates = {}

//render and cache compiled templates
function render(template, model, url){
    let compiledTemplate;
    if(templates[url] = undefined){
        compiledTemplate = Handlebars.compile(template);
        templates[url] = compiledTemplate;
    }
    else{
        compiledTemplate = templates[url]
    }
    return compiledTemplate(toJsonModel(model));
}

//create a real json object from the java map
function toJsonModel(model){
    let json = {};
    for(k in model){
        if(model[k] instanceof Java.type("java.lang.Iterable")){
            json[k] = Java.from(model[k])
        }
        else{
            json[k] = model[k]
        }
    }
    return json;
}