//render template using model provided
function ejsRender(template, model){
    return ejs.render(template, toJsonModel(model));
}

//create a real json object from the java map
function toJsonModel(model){
    var json = {};
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