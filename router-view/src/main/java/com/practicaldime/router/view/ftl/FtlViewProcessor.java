package com.practicaldime.router.view.ftl;

import com.practicaldime.router.core.view.ViewProcessor;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class FtlViewProcessor implements ViewProcessor<Template, Configuration> {

    @Override
    public Template resolve(String templatePath, String template, Configuration lookup) throws Exception {
        return lookup.getTemplate(templatePath);
    }
}
