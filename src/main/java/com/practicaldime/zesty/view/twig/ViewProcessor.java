package com.practicaldime.zesty.view.twig;

import org.jtwig.JtwigTemplate;
import org.jtwig.resource.reference.ResourceReference;

public interface ViewProcessor {
    
    JtwigTemplate resolve(String templatePath, ResourceReference where) throws Exception;
}
