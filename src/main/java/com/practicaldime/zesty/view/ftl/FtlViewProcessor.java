package com.practicaldime.zesty.view.ftl;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FtlViewProcessor implements ViewProcessor{

    @Override
    public Template resolve(String templatePath, Configuration config) throws Exception {
        return config.getTemplate(templatePath);
    }    
}
