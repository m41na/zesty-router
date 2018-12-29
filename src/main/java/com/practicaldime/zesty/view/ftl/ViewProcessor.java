package com.practicaldime.zesty.view.ftl;

import freemarker.template.Configuration;
import freemarker.template.Template;

public interface ViewProcessor {
    
    Template resolve(String templatePath, Configuration config) throws Exception;
}
