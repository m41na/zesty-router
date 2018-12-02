package com.practicaldime.zesty.view.twig;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.jtwig.JtwigTemplate;
import org.jtwig.resource.reference.ResourceReference;

public interface ViewProcessor {
    
    void write(HttpServletResponse response, JtwigTemplate template, String view, String contentType, Map<String, Object> model) throws IOException;
    
    JtwigTemplate resolve(String templatePath, ResourceReference where) throws Exception;
}
