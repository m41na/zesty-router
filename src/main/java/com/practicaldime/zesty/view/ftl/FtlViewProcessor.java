package com.practicaldime.zesty.view.ftl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

public class FtlViewProcessor implements ViewProcessor{

    @Override
    public void write(HttpServletResponse response, Template template, String view, String contentType, Map<String, Object> model) throws IOException, TemplateException {
        response.setContentType(contentType);
        template.process(model, response.getWriter());
    }

    @Override
    public Template resolve(String templatePath, Configuration config) throws Exception {
        return config.getTemplate(templatePath);
    }    
}
